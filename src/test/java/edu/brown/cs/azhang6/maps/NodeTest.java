package edu.brown.cs.azhang6.maps;

import edu.brown.cs.azhang6.db.Database;
import java.sql.SQLException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for {@link Node} and {@link NodeProxy}.
 *
 * @author aaronzhang
 */
public class NodeTest {

    /**
     * Database.
     */
    private static Database db;
    
    /**
     * For comparing doubles.
     */
    private static final double EPSILON = 0.001;

    /**
     * Opens database.
     */
    @BeforeClass
    public static void setUpClass() {
        try {
            db = new Database("files/smallMaps.sqlite3");
            NodeProxy.setDB(db);
            WayProxy.setDB(db);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes database.
     */
    @AfterClass
    public static void tearDownClass() {
        try {
            if (db != null) {
                db.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tests for {@link Node#of(String)}.
     */
    @Test
    public void testOf() {
        assertEquals(Node.of("/n/0").getLat(), 41.82, EPSILON);
        assertEquals(Node.of("/n/0").getLng(), -71.4, EPSILON);
        assertEquals(Node.of("/n/4").getLat(), 41.8203, EPSILON);
        assertEquals(Node.of("/n/5").getLng(), -71.4003, EPSILON);
    }

    /**
     * Tests for {@link Node#cache(Node)} and {@link Node#has(String)}.
     */
    @Test
    public void testCache() {
        assertFalse(Node.has("/n/1"));
        Node.cache(Node.of("/n/1"));
        assertTrue(Node.has("/n/1"));
    }

    /**
     * Tests for {@link Node#clearCache()}.
     */
    @Test
    public void testClearCache() {
        Node.cache(Node.of("/n/3"));
        assertTrue(Node.has("/n/3"));
        Node.clearCache();
        assertFalse(Node.has("/n/3"));
    }
    
    /**
     * Tests for {@link NodeProxy#atIntersection(String, String)}.
     */
    @Test
    public void testAtIntersection() {
        assertEquals(NodeProxy.atIntersection(
            "Chihiro Ave", "Sootball Ln").getId(), "/n/1");
        assertEquals(NodeProxy.atIntersection(
            "Sootball Ln", "Chihiro Ave").getId(), "/n/1");
        assertEquals(NodeProxy.atIntersection(
            "Chihiro Ave", "Radish Spirit Blvd").getId(), "/n/0");
        assertEquals(NodeProxy.atIntersection(
            "Radish Spirit Blvd", "Chihiro Ave").getId(), "/n/0");
        assertTrue(NodeProxy.atIntersection(
            "Chihiro Ave", "Yubaba St") == null);
        assertTrue(NodeProxy.atIntersection(
            "Chihiro Ave", "nonexistent") == null);
    }
}
