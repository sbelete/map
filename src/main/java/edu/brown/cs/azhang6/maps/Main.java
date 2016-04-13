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
import java.nio.charset.IllegalCharsetNameException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final String USAGE = "Usage: ./run [--gui] database";

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
     * KD-tree parallel level, for efficiency.
     */
    private static final int KD_TREE_PARALLEL_LEVEL = 4;
    
    /**
     * Autocorrect.
     */
    private StreetComplete corrector;

    /**
     * Latitude of center of screen.
     */
    private double lat;

    /**
     * Longitude of center of screen.
     */
    private double lng;

    /**
     * Size of screen in terms of degrees latitude.
     */
    private double size;

    /**
     * Edges shown on screen.
     */
    private double[][] shownEdges = new double[0][];

    /**
     * Edges shown on screen that are part of a shortest path.
     */
    private double[][] pathEdges = new double[0][];

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

        try {
            // Parse options
            OptionSet options = parser.parse(args);
            String dbString = options.valueOf(dbSpec);
            if (dbString == null) {
                System.out.println(USAGE);
                System.exit(1);
            }
            if (options.has("help")) {
                // Exit if --help is given
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
                        System.out.println("ERROR:" + e.getMessage());
                    }
                }));
                // Make sure node and way proxies use the database
                NodeProxy.setDB(db);
                WayProxy.setDB(db);
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("ERROR: couldn't load database");
            }
            // Setup traffic server
            try {
                traffic = new TrafficClient(String.format(
                    "http://localhost:%d?last=", TRAFFIC_PORT));
                Way.setTrafficClient(traffic);
                traffic.query();
                traffic.start(TRAFFIC_QUERY_RATE);
            } catch (IOException e) {
                System.out.println("ERROR: couldn't connect to traffic server");
            }
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
        try (PreparedStatement prep = db.getConn().prepareStatement(
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
            nodes = new LatLngKDTree<>(
                new KDNodeParallel<>(nodesToAdd, 0, KD_TREE_PARALLEL_LEVEL));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets up trie.
     */
    private void setupAutocorrect() {
        try (PreparedStatement prep = db.getConn().prepareStatement(
            "SELECT name FROM way;")) {
            List<String> streets = db.query(prep);
            corrector = new StreetComplete(streets, streets);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Runs REPL.
     */
    private void runREPL() {
        setupKDTree();
    }

    /**
     * Runs gui.
     */
    private void runSparkServer() {
        setupKDTree();
        //setupAutocorrect();

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
        Spark.post("/findIntersection", new FindIntersectionHandler());
        // Autocorrect (Streetcorrect)
        Spark.post("/auto", new AutocorrectHandler());
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
        public Object handle(final Request req, final Response res) {
            // Find nearest neighbor
            QueryParamsMap qm = req.queryMap();
            double latitude = Double.parseDouble(qm.value("lat"));
            double longitude = Double.parseDouble(qm.value("lon"));
            List<DimensionalDistance<Node>> nearestNeighbors
                = nodes.nearestNeighbors(new LatLng(latitude, longitude), 1, null);
            Node nearestNeighbor = nearestNeighbors.get(0).getDimensional();

            // Send latitude and longitude of nearest neighbor
            Map<String, Object> variables = ImmutableMap.of(
                "id", nearestNeighbor.getId(),
                "lat", nearestNeighbor.getLat(),
                "lng", nearestNeighbor.getLng());
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
        public Object handle(final Request req, final Response res) {
            // Find shortest path
            QueryParamsMap qm = req.queryMap();
            String startId = qm.value("start_id");
            String endId = qm.value("end_id");
            Node startNode = Node.of(startId);
            Node endNode = Node.of(endId);
            OrderedPair<Walk<Node, Way>, Double> shortestPath;
            synchronized (traffic) {
                shortestPath = Graphs.dijkstra(startNode, n -> endNode.equals(n));
            }

            // Send information about shortest path
            List<Vertex<Node, Way>> vertices = shortestPath.first().getVertices();
            List<Edge<Node, Way>> edges = shortestPath.first().getEdges();
            pathEdges = new double[edges.size()][];
            for (int i = 0; i < edges.size(); i++) {
                Node v1 = vertices.get(i).getValue().get();
                Node v2 = vertices.get(i + 1).getValue().get();
                pathEdges[i] = new double[]{
                    v1.getLat(), v1.getLng(), v2.getLat(), v2.getLng(),
                    edges.get(i).getValue().get().traffic()
                };
            }
            Map<String, Object> variables = ImmutableMap.of(
                "shownEdges", shownEdges,
                "pathEdges", pathEdges);
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
        public Object handle(final Request req, final Response res) {
            QueryParamsMap qm = req.queryMap();
            double latitude = Double.parseDouble(qm.value("lat"));
            double longitude = Double.parseDouble(qm.value("lon"));
            double size = Double.parseDouble(qm.value("s"));
            // Get all points that should be displayed
            LatLngSize box = new LatLngSize(latitude, longitude, size);
            List<DimensionalDistance<Node>> withinRadius
                = nodes.withinRadius(new LatLng(latitude, longitude), box.radius, null);
            List<Node> displayedNodes = withinRadius.stream()
                .map(dd -> dd.getDimensional())
                .filter(ll -> box.contains(ll)).collect(Collectors.toList());
            // Get all edges that should be displayed
            List<Edge<Node, Way>> displayedEdges = new ArrayList<>();
            displayedNodes.forEach(n -> displayedEdges.addAll(n.getDWEdges()));

            // Send information about shown edges
            shownEdges = new double[displayedEdges.size()][];
            for (int i = 0; i < shownEdges.length; i++) {
                Edge<Node, Way> e = displayedEdges.get(i);
                Node v1 = e.getEndpoints().s().getValue().get();
                Node v2 = e.getEndpoints().t().getValue().get();
                shownEdges[i] = new double[]{
                    v1.getLat(), v1.getLng(), v2.getLat(), v2.getLng(),
                    e.getValue().get().traffic()
                };
            }
            Map<String, Object> variables = ImmutableMap.of(
                "shownEdges", shownEdges,
                "pathEdges", pathEdges);
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
        public Object handle(final Request req, final Response res) {
            QueryParamsMap qm = req.queryMap();
            String streetName = qm.value("street_name");
            List<String> suggestions = corrector.suggest(streetName);

            List<Object> variables
                = ImmutableList.of(suggestions.subList(0, 4));

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
            System.out.println("calculated radius = " + radius);
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
        public Object handle(final Request req, final Response res) {
            pathEdges = new double[0][];
            Map<String, Object> variables = ImmutableMap.of(
                "shownEdges", shownEdges,
                "pathEdges", pathEdges);
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
        public Object handle(final Request req, final Response res) {
            // Get intersection
            QueryParamsMap qm = req.queryMap();
            String way1Name = qm.value("first_street");
            String way2Name = qm.value("second_street");
            Node intersection = NodeProxy.atIntersection(way1Name, way2Name);

            // Send information about intersection
            Map<String, Object> variables;
            if (intersection == null) {
                variables = ImmutableMap.of(
                    "id", null,
                    "lat", null,
                    "lng", null);
            } else {
                variables = ImmutableMap.of(
                    "id", intersection.getId(),
                    "lat", intersection.getLat(),
                    "lng", intersection.getLng());
            }
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
