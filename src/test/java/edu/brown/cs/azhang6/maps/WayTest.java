package edu.brown.cs.azhang6.maps;

import edu.brown.cs.azhang6.db.Database;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for {@link Way} and {@link WayProxy}.
 * 
 * @author aaronzhang
 */
public class WayTest {
    
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
     * Tests for {@link Way#of(String)}.
     */
    @Test
    public void testOf() {
        assertEquals(Way.of("/w/0").getName(), "Chihiro Ave");
        assertEquals(Way.of("/w/0").getType(), "residential");
        assertEquals(Way.of("/w/0").getStart(), "/n/0");
        assertEquals(Way.of("/w/0").getEnd(), "/n/1");
    }

    /**
     * Tests for {@link Way#cache(Way)} and {@link Way#has(String)}.
     */
    @Test
    public void testCache() {
        assertFalse(Way.has("/w/1"));
        Way.cache(Way.of("/w/1"));
        assertTrue(Way.has("/w/1"));
    }

    /**
     * Tests for {@link Way#clearCache()}.
     */
    @Test
    public void testClearCache() {
        Way.cache(Way.of("/w/2"));
        assertTrue(Way.has("/w/2"));
        Way.clearCache();
        assertFalse(Way.has("/w/2"));
    }
    
    /**
     * Tests for {@link WayProxy#idsForName(String)}.
     */
    @Test
    public void testIdsForName() {
        List<String> expected1 = new ArrayList<>();
        expected1.add("/w/0");
        expected1.add("/w/1");
        assertEquals(WayProxy.idsForName("Chihiro Ave"), expected1);
        assertEquals(WayProxy.idsForName("doesn't exist"), Collections.emptyList());
    }
}
