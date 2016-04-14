package edu.brown.cs.azhang6.maps;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.azhang6.autocorrect.Autocorrect;
import edu.brown.cs.azhang6.db.Database;
import edu.brown.cs.azhang6.dimension.Dimensional;
import edu.brown.cs.azhang6.dimension.DimensionalDistance;
import edu.brown.cs.azhang6.dimension.LatLng;
import edu.brown.cs.azhang6.graph.Edge;
import edu.brown.cs.azhang6.graph.Vertex;
import edu.brown.cs.azhang6.graphs.Graphs;
import edu.brown.cs.azhang6.graphs.Walk;
import edu.brown.cs.azhang6.kdtree.KDNode;
import edu.brown.cs.azhang6.kdtree.KDNodeParallel;
import edu.brown.cs.azhang6.kdtree.KDVertex;
import edu.brown.cs.azhang6.kdtree.LatLngKDTree;
import edu.brown.cs.azhang6.pair.OrderedPair;
import freemarker.template.Configuration;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.IllegalCharsetNameException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * Runs application.
 *
 * @author aaronzhang
 */
public class Main {

    /**
     * Usage message.
     */
    private static final String USAGE =
        "Usage: ./run [--gui] [--traffic-port=port] database";

    /**
     * Command line arguments.
     */
    private final String[] args;

    /**
     * Database.
     */
    private Database db;

    /**
     * Traffic client.
     */
    private TrafficClient traffic;

    /**
     * Default traffic server port.
     */
    private static final int TRAFFIC_PORT = 8080;

    /**
     * Number of milliseconds between traffic queries.
     */
    private static final int TRAFFIC_QUERY_RATE = 5000;

    /**
     * GSON. Package-protected since the traffic client also uses GSON.
     */
    static final Gson GSON = new Gson();

    /**
     * KD-tree.
     */
    private KDVertex<Node> nodes;

    /**
     * Flag for an empty database of nodes.
     */
    private boolean empty = false;

    /**
     * KD-tree parallel level, for efficiency.
     */
    static final int KD_TREE_PARALLEL_LEVEL = 3;

    /**
     * Used in REPL: if input has no quotes, it splits into this many pieces.
     */
    private static final int PIECES_NO_QUOTES = 4;

    /**
     * Used in REPL: if input has quotes, it splits into this many pieces.
     */
    private static final int PIECES_QUOTES = 8;
    
    /**
     * Fail limit for Dijkstra search.  Prevents searching too many nodes if
     * start and end are disconnected.
     */
    static final double DIJKSTRA_FAIL = 10;
    
    /**
     * Maximum number of vertices to search in Dijkstra.
     */
    static final int DIJKSTRA_MAX_VERTICES = 5000;

    /**
     * Autocorrect.
     */
    private Autocorrect corrector;

    /**
     * IDs of ways that have already been sent to the frontend.
     */
    private final Set<String> sentWays = new HashSet<>();
    
    /**
     * Old edges.
     */
    private final List<Object[]> oldEdges = new CopyOnWriteArrayList<>();
    
    /**
     * New edges.
     */
    private final List<Object[]> newEdges = new CopyOnWriteArrayList<>();
    
    /**
     * Coordinates of new edges.
     */
    private final List<double[]> newCoords = new CopyOnWriteArrayList<>();

    /**
     * Edges in shortest path.
     */
    private final Map<String, Boolean> pathEdges = new ConcurrentHashMap<>();

    public static boolean done = false;
    
    /**
     * Runs application with command line arguments.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new Main(args).run();
    }

    /**
     * Constructs main class with the given command line arguments.
     *
     * @param args command line arguments
     */
    private Main(String[] args) {
        this.args = args;
    }

    /**
     * Runs application.
     */
    private void run() {
        // Possible options
        OptionParser parser = new OptionParser();
        OptionSpec<String> dbSpec
            = parser.nonOptions().ofType(String.class);
        parser.accepts("help", "display help message");
        parser.accepts("gui", "run spark server");
        parser.accepts("traffic-port", "traffic server port")
            .withRequiredArg().ofType(int.class);

        try {
            // Parse options
            OptionSet options = parser.parse(args);
            String dbString = options.valueOf(dbSpec);
            if (dbString == null) {
                System.out.println(USAGE);
                System.exit(1);
            }
            if (options.has("help")) {
                parser.printHelpOn(System.out);
                System.out.println(USAGE);
                System.exit(0);
            }

            // Build database
            try {
                db = new Database(dbString);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        if (db != null) {
                            db.close();
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR: error closing database: " + e);
                    }
                }));
                // Make sure node and way proxies use the database
                NodeProxy.setDB(db);
                WayProxy.setDB(db);
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("ERROR: couldn't load database");
            }

            // Setup traffic server
            if (options.has("traffic-port")) {
                int trafficPort = (int) options.valueOf("traffic-port");
                traffic = new TrafficClient(String.format(
                    "http://localhost:%d?last=", trafficPort));
            } else {
                traffic = new TrafficClient(String.format(
                    "http://localhost:%d?last=", TRAFFIC_PORT));
            }
            traffic.start(TRAFFIC_QUERY_RATE);

            // Whether to run GUI or REPL
            if (options.has("gui")) {
                runSparkServer();
            } else {
                runREPL();
            }
        } catch (OptionException e) {
            System.out.println("ERROR: " + USAGE);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
    }

    /**
     * Sets up kd-tree.
     */
    private void setupKDTree() {
        Connection conn = db.getConnection();
        try (PreparedStatement prep = conn.prepareStatement(
            "SELECT id FROM node;")) {
            List<Node> nodesToAdd = new ArrayList<>();
            db.query(prep, rs -> {
                try {
                    while (rs.next()) {
                        nodesToAdd.add(Node.of(rs.getString(1)));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            if (nodesToAdd.isEmpty()) {
                empty = true;
            }
            nodes = new LatLngKDTree<>(
                new KDNodeParallel<>(nodesToAdd, 0, KD_TREE_PARALLEL_LEVEL));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            db.returnConnection(conn);
        }
    }

    /**
     * Sets up trie.
     */
    private void setupAutocorrect() {
        Connection conn = db.getConnection();
        try (PreparedStatement prep = conn.prepareStatement(
            "SELECT name FROM way;")) {
            List<String> streets = db.query(prep);
            corrector = new Autocorrect(streets);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            db.returnConnection(conn);
        }
    }

    /**
     * Runs REPL.
     */
    private void runREPL() {
        // Make sure database of nodes isn't empty
        setupKDTree();
        System.out.println("READY");
        if (empty) {
            System.out.println("ERROR: empty database of nodes");
            return;
        }
        // Read standard input
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
            System.in, "UTF-8"))) {
            String line;
            replLoop:
            while ((line = reader.readLine()) != null) {
                // End if given empty line
                if (line.isEmpty()) {
                    break;
                }

                // Else, split line into pieces
                boolean hasQuotes = line.contains("\"");
                List<String> piecesQuotes = null;
                List<Double> piecesSpaces = null;
                if (hasQuotes) {
                    // Split on quotes to get streets
                    String[] splitByQuotes = line.split("\"");
                    if (splitByQuotes.length != PIECES_QUOTES) {
                        System.out.println(
                            "ERROR: wrong number of quotes");
                        continue;
                    }
                    piecesQuotes = new ArrayList<>();
                    for (int i = 1; i < splitByQuotes.length; i += 2) {
                        piecesQuotes.add(splitByQuotes[i]);
                    }
                } else {
                    // Split on spaces to get coordinates
                    String[] splitBySpaces = line.split(" ");
                    if (splitBySpaces.length != PIECES_NO_QUOTES) {
                        System.out.println(
                            "ERROR: wrong number of spaces");
                        continue;
                    }
                    piecesSpaces = new ArrayList<>();
                    for (int i = 0; i < splitBySpaces.length; i++) {
                        try {
                            piecesSpaces.add(Double.parseDouble(splitBySpaces[i]));
                        } catch (NumberFormatException e) {
                            System.out.println(
                                "ERROR: can't parse coordinates");
                            continue replLoop;
                        }
                    }
                }

                // Get start and end nodes
                Node start;
                Node end;
                if (hasQuotes) {
                    start = NodeProxy.atIntersection(
                        piecesQuotes.get(0), piecesQuotes.get(1));
                    if (start == null) {
                        System.out.println("ERROR: first two streets don't intersect");
                        continue;
                    }
                    end = NodeProxy.atIntersection(
                        piecesQuotes.get(2), piecesQuotes.get(3));
                    if (end == null) {
                        System.out.println("ERROR: last two streets don't intersect");
                        continue;
                    }
                } else {
                    start = nodes.nearestNeighbors(
                        new LatLng(piecesSpaces.get(0), piecesSpaces.get(1)),
                        1, null).get(0).getDimensional();
                    end = nodes.nearestNeighbors(
                        new LatLng(piecesSpaces.get(2), piecesSpaces.get(3)),
                        1, null).get(0).getDimensional();
                }

                // Find and print shortest path
                if (start.equals(end)) {
                    System.out.println("ERROR: start and end nodes are the same");
                    continue;
                }
                OrderedPair<Walk<Node, Way>, Double> shortestPath
                    = Graphs.dijkstraAStarFail(start, n -> n.equals(end),
                        n -> n.tunnelDistanceTo(end),
                        DIJKSTRA_FAIL, DIJKSTRA_MAX_VERTICES);
                System.out.println(
                    formatOutput(shortestPath, "\n", start.getId(), end.getId()));
            }
        } catch (IOException e) {
            System.out.println("ERROR: error reading input");
        }
    }

    /**
     * Formats output of Dijkstra's algorithm with the specified line separator.
     *
     * @param result output of Dijkstra's algorithm
     * @param lineSeparator appended to each line
     * @param start start vertex of Dijkstra's algorithm
     * @param end end vertex of Dijkstra's algorithm
     */
    private String formatOutput(OrderedPair<Walk<Node, Way>, Double> result,
        String lineSeparator, String start, String end) {
        StringBuilder sb = new StringBuilder();

        // If there is a path, format it
        if (result != null) {
            Walk<Node, Way> walk = result.first();
            List<Vertex<Node, Way>> walkVertices = walk.getVertices();
            List<Edge<Node, Way>> walkEdges = walk.getEdges();
            for (int i = 0; i < walkEdges.size(); i++) {
                Edge<Node, Way> edge = walkEdges.get(i);
                Vertex<Node, Way> tail = walkVertices.get(i);
                Vertex<Node, Way> head = walkVertices.get(i + 1);
                sb.append(String.format("%s -> %s : %s",
                    tail.getValue().get().getId(),
                    head.getValue().get().getId(),
                    edge.getValue().get().getId()));
                // Don't append the line separator after the last line
                if (i < walkEdges.size() - 1) {
                    sb.append(lineSeparator);
                }
            }
        } else {
            // If there isn't a path
            sb.append(String.format("%s -/- %s", start, end));
        }

        return sb.toString();
    }

    /**
     * Runs gui.
     */
    private void runSparkServer() {
        Thread kdThread = new Thread(() -> {
            setupKDTree();
        });
        kdThread.start();
        Thread autoThread = new Thread(() -> {
            setupAutocorrect();
        });
        autoThread.start();
        try {
            kdThread.join();
            autoThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(
                "interrupted while setting up kdtree and autocorrect", e);
        }

        // Setup Spark
        Spark.externalStaticFileLocation("src/main/resources/static");
        Spark.exception(Exception.class, new ExceptionPrinter());
        FreeMarkerEngine freeMarker = createEngine();
        // Home page
        Spark.get("/home", new HomeHandler(), freeMarker);
        // Nearest neighbor
        Spark.post("/nearestNeighbor", new NearestNeighborHandler());
        // Shortest path
        Spark.post("/shortestPath", new ShortestPathHandler());
        // Get edges to display
        Spark.post("/getEdges", new GetEdgesHandler());
        // Clear shortest path
        Spark.post("/clear", new ClearHandler());
        // Find intersection of two streets
        Spark.post("/findIntersection", new FindIntersectionHandler());
        // Autocorrect (Streetcorrect)
        Spark.post("/auto", new AutocorrectHandler());
        done = true;
    }

    /**
     * Handler for /home.
     */
    private class HomeHandler implements TemplateViewRoute {

        /**
         * Supplies page title.
         *
         * @param req unused
         * @param res unused
         * @return ModelAndView
         */
        @Override
        public ModelAndView handle(Request req, Response res) {
            sentWays.clear();
            oldEdges.clear();
            newEdges.clear();
            newCoords.clear();
            pathEdges.clear();
            return new ModelAndView(ImmutableMap.of(), "maps.ftl");
        }
    }

    /**
     * Handler for /nearestNeighbor.
     */
    private class NearestNeighborHandler implements Route {

        /**
         * Gets nearest neighbor.
         *
         * @param req request containing latitude and longitude
         * @param res unused
         * @return nearest neighbor
         */
        @Override
        public synchronized Object handle(final Request req, final Response res) {
            // Find nearest neighbor
            QueryParamsMap qm = req.queryMap();
            double latitude = Double.parseDouble(qm.value("lat"));
            double longitude = Double.parseDouble(qm.value("lon"));
            System.out.println("nearestNeighbor");
            System.out.println("latitude: " + latitude);
            System.out.println("longitude: " + longitude);
            List<DimensionalDistance<Node>> nearestNeighbors
                = nodes.nearestNeighbors(new LatLng(latitude, longitude), 1, null);
            Node nearestNeighbor = nearestNeighbors.get(0).getDimensional();

            // Send latitude and longitude of nearest neighbor
            Map<String, Object> variables = ImmutableMap.of(
                "id", nearestNeighbor.getId(),
                "lat", nearestNeighbor.getLat(),
                "lng", nearestNeighbor.getLng());
            System.out.println("found nearest neighbor:");
            System.out.println(variables);
            return GSON.toJson(variables);
        }
    }

    /**
     * Handler for /shortestPath.
     */
    private class ShortestPathHandler implements Route {

        /**
         * Gets shortest path.
         *
         * @param req request containing start and end nodes
         * @param res unused
         * @return shortest path
         */
        @Override
        public synchronized Object handle(final Request req, final Response res) {
            // Find shortest path
            QueryParamsMap qm = req.queryMap();
            String startId = qm.value("start_id");
            String endId = qm.value("finish_id");
            System.out.println("shortestPath");
            System.out.println("startId: " + startId);
            System.out.println("endId: " + endId);
            Node startNode = Node.of(startId);
            Node endNode = Node.of(endId);
            OrderedPair<Walk<Node, Way>, Double> shortestPath;
            synchronized (traffic) {
                shortestPath = Graphs.dijkstraAStarFail(
                    startNode, n -> endNode.equals(n),
                    n -> n.tunnelDistanceTo(endNode),
                    DIJKSTRA_FAIL, DIJKSTRA_MAX_VERTICES);
            }
            if (shortestPath != null) {
            
            System.out.println("found a shortest path of length: " + shortestPath.second());

            // Send information about shortest path
            List<Edge<Node, Way>> edges = shortestPath.first().getEdges();
            pathEdges.clear();
            edges.forEach(e -> pathEdges.put(e.getValue().get().getId(), true));
            oldEdges.addAll(newEdges);
            newEdges.clear();
            newCoords.clear();
            } else {
                System.out.println("no shortest path");
                oldEdges.addAll(newEdges);
                newEdges.clear();
                newCoords.clear();
                pathEdges.clear();
            }
            Map<String, Object> variables = ImmutableMap.of(
                "oldEdges", oldEdges,
                "newEdges", newEdges,
                "newCoords", newCoords,
                "pathEdges", pathEdges
            );
            return GSON.toJson(variables);
        }
    }

    /**
     * Handler for /getEdges.
     */
    private class GetEdgesHandler implements Route {

        /**
         * Gets edges.
         *
         * @param req request containing bounding box
         * @param res unused
         * @return shortest path
         */
        @Override
        public synchronized Object handle(final Request req, final Response res) {
            QueryParamsMap qm = req.queryMap();
            double latitude = Double.parseDouble(qm.value("lat"));
            double longitude = Double.parseDouble(qm.value("lon"));
            double size = Double.parseDouble(qm.value("s"));
            // Get all edges that should be displayed
            LatLngSize box = new LatLngSize(latitude, longitude, size);
            List<DimensionalDistance<Node>> withinRadius
                = nodes.withinRadius(new LatLng(latitude, longitude), box.radius, null);
            List<Node> displayedNodes = withinRadius.stream()
                .map(dd -> dd.getDimensional())
                .filter(ll -> box.contains(ll)).collect(Collectors.toList());
            List<Edge<Node, Way>> edges = new ArrayList<>();
            displayedNodes.forEach(n -> edges.addAll(n.getDWEdges()));
            // Send information about edges
            oldEdges.clear();
            newEdges.clear();
            newCoords.clear();
            for (int i = 0; i < edges.size(); i++) {
                Way e = edges.get(i).getValue().get();
                if (sentWays.contains(e.getId())) {
                    oldEdges.add(new Object[]{e.getId(), e.getTraffic()});
                } else {
                    newEdges.add(new Object[]{e.getId(), e.getTraffic()});
                    Node v1 = edges.get(i).getEndpoints().s().getValue().get();
                    Node v2 = edges.get(i).getEndpoints().t().getValue().get();
                    newCoords.add(new double[] {
                        v1.getLat(), v1.getLng(), v2.getLat(), v2.getLng()});
                    sentWays.add(e.getId());
                }
            }
            Map<String, Object> variables = ImmutableMap.of(
                "oldEdges", oldEdges,
                "newEdges", newEdges,
                "newCoords", newCoords,
                "pathEdges", pathEdges
            );
            return GSON.toJson(variables);
        }
    }

    /**
     * Handler for /auto.
     */
    private class AutocorrectHandler implements Route {

        /**
         * Autocorrect.
         *
         * @param req request
         * @param res unused
         * @return autocorrect suggestions
         */
        @Override
        public synchronized Object handle(final Request req, final Response res) {
            QueryParamsMap qm = req.queryMap();
            String streetName = qm.value("street_name");
            List<String> suggestions = corrector.suggest(streetName);
            List<String> trimmedSuggestions = new ArrayList<>();
            for (int i = 0; i < suggestions.size() && i < 5; i++) {
                trimmedSuggestions.add(suggestions.get(i));
            }
            List<Object> variables
                = ImmutableList.of(trimmedSuggestions);

            return GSON.toJson(variables);
        }
    }

    /**
     * Information obtained from latitude, longitude, and size.
     */
    private static class LatLngSize {

        // Latitude, longitude, and size
        private final double lat;
        private final double lng;
        private final double size;

        // Additional information calculated from the above three
        private final double minLat;
        private final double maxLat;
        private final double lngSize;
        private final double minLng;
        private final double maxLng;
        private final double radius;

        /**
         * Calculates info given latitude, longitude, and size.
         *
         * @param lat latitude
         * @param lng longitude
         * @param size size (degrees latitude)
         */
        private LatLngSize(double lat, double lng, double size) {
            this.lat = lat;
            this.lng = lng;
            this.size = size;
            // Min and max latitude shown
            minLat = lat - size / 2;
            maxLat = lat + size / 2;
            // Degrees of longitude shown
            lngSize = size * 110.574 / (111.320 * Math.cos(Math.toRadians(lat)));
            // Min and max longitude shown
            minLng = lng - lngSize / 2;
            maxLng = lng + lngSize / 2;
            // Approximate radius
            radius = Math.max(
                new LatLng(lat, lng).distanceTo(new LatLng(minLat, minLng)),
                new LatLng(lat, lng).distanceTo(new LatLng(maxLat, maxLng)));
        }

        /**
         * Whether point is contained in this region.
         *
         * @param point LatLng
         * @return whether point is contained in this region
         */
        private boolean contains(LatLng point) {
            return containsHelper(point) || containsHelper(wrapped(point));
        }

        /**
         * Helper for above method.
         *
         * @param point LatLng
         * @return whether point is contained in this region
         */
        private boolean containsHelper(LatLng point) {
            return point.getLat() >= minLat
                && point.getLat() <= maxLat
                && point.getLng() >= minLng
                && point.getLng() <= maxLng;
        }

        /**
         * Returns a LatLng equivalent to the original LatLng, but with
         * longitude wrapped. So if the original has longitude 160, the result
         * has longitude -200.
         *
         * @param original original LatLng
         * @return wrapped LatLng
         */
        private static LatLng wrapped(LatLng original) {
            double originalLng = original.getLng();
            if (originalLng > 0) {
                return (LatLng) original.withCoordinate(1, originalLng - 360);
            } else if (originalLng < 0) {
                return (LatLng) original.withCoordinate(1, originalLng + 360);
            }
            return original;
        }
    }

    /**
     * Handler for /clear.
     */
    private class ClearHandler implements Route {

        /**
         * Clears shortest path.
         *
         * @param req unused
         * @param res unused
         * @return shown edges
         */
        @Override
        public synchronized Object handle(final Request req, final Response res) {
            pathEdges.clear();
            oldEdges.addAll(newEdges);
            newEdges.clear();
            newCoords.clear();
            Map<String, Object> variables = ImmutableMap.of(
                "oldEdges", oldEdges,
                "newEdges", newEdges,
                "newCoords", newCoords,
                "pathEdges", pathEdges
            );
            return GSON.toJson(variables);
        }
    }

    /**
     * Handler for /findIntersection.
     */
    private class FindIntersectionHandler implements Route {

        /**
         * Finds intersection of two ways.
         *
         * @param req request with names of two ways
         * @param res unused
         * @return intersection of two ways
         */
        @Override
        public synchronized Object handle(final Request req, final Response res) {
            // Get intersection
            QueryParamsMap qm = req.queryMap();
            String way1Name = qm.value("first_street");
            String way2Name = qm.value("second_street");
            System.out.println("findIntersection");
            System.out.println("way1: " + way1Name);
            System.out.println("way2: " + way2Name);
            Node intersection = NodeProxy.atIntersection(way1Name, way2Name);

            // Send information about intersection
            Map<String, Object> variables;
            if (intersection == null) {
                variables = ImmutableMap.of(
                    "id", "",
                    "lat", "",
                    "lng", "");
            } else {
                variables = ImmutableMap.of(
                    "id", intersection.getId(),
                    "lat", intersection.getLat(),
                    "lng", intersection.getLng());
            }
            System.out.println("found intersection:");
            System.out.println(variables);
            return GSON.toJson(variables);
        }
    }

    /**
     * @return freemarker engine
     */
    private static FreeMarkerEngine createEngine() {
        Configuration config = new Configuration();
        File templates
            = new File("src/main/resources/spark/template/freemarker");
        try {
            config.setDirectoryForTemplateLoading(templates);
        } catch (IOException ioe) {
            System.out.printf(
                "ERROR: Unable use %s for template loading.%n", templates);
            System.exit(1);
        }
        return new FreeMarkerEngine(config);
    }

    /**
     * Exception printer for spark.
     */
    private static class ExceptionPrinter implements ExceptionHandler {

        /**
         * Default status.
         */
        private static final int STATUS = 500;

        @Override
        public void handle(Exception e, Request req, Response res) {
            res.status(STATUS);
            StringWriter stacktrace = new StringWriter();
            try (PrintWriter pw = new PrintWriter(stacktrace)) {
                pw.println("<pre>");
                e.printStackTrace(pw);
                pw.println("</pre>");
            }
            res.body(stacktrace.toString());
        }
    }
}
