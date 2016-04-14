package edu.brown.cs.azhang6.maps;

import edu.brown.cs.azhang6.db.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 * Tests for {@link StreetComplete}.
 * 
 * @author aaronzhang
 */
public class StreetCompleteTest {
    
    /**
     * Database.
     */
    private static Database db;
    
    private static StreetComplete sc;

    /**
     * Opens database.
     * 
     * @throws SQLException should not occur
     */
    @BeforeClass
    public static void setUpClass() throws SQLException {
        try {
            db = new Database("files/smallMaps.sqlite3");
            Connection conn = db.getConnection();
            try (PreparedStatement prep = conn.prepareStatement(
                "SELECT name FROM way;")) {
                List<String> streets = db.query(prep);
                sc = new StreetComplete(streets, streets);
            } finally {
                db.returnConnection(conn);
            }
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

    @Test
    public void testSuggest() {
    }
    
}
