package edu.brown.cs.azhang6.maps;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.azhang6.db.Database;
import freemarker.template.Configuration;
import java.nio.charset.IllegalCharsetNameException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
     * GSON.
     */
    private static final Gson GSON = new Gson();

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
                System.out.println("ERROR: couldn't load database: " + e);
            }
            if (options.has("gui")) {
                // Run gui
                runSparkServer();
            } else {
                // If neither --help nor --gui, run REPL
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

    }

    /**
     * Sets up trie.
     */
    private void setupAutocorrect() {

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
        setupAutocorrect();

        // Setup Spark
        Spark.externalStaticFileLocation("src/main/resources/static");
        Spark.exception(Exception.class, new ExceptionPrinter());
        FreeMarkerEngine freeMarker = createEngine();
        // Home page
        Spark.get("/home", new HomeHandler(), freeMarker);
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

        /**
         * {@inheritDoc}
         */
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
