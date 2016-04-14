package edu.brown.cs.azhang6.autocorrect;

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
 * Tests for {@link AutocorrectTest}.
 * 
 * @author aaronzhang
 */
public class AutocorrectTest {
    
    /**
     * Database.
     */
    private static Database db;
    
    /**
     * Autocorrect.
     */
    private static Autocorrect ac;
    
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
                ac = new Autocorrect(streets);
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

    /**
     * Test suggestions.
     */
    @Test
    public void testSuggest() {
        assertTrue(ac.suggest("Yubaba").contains("Yubaba St"));
        assertTrue(ac.suggest("Yub").contains("Yubaba St"));
        assertFalse(ac.suggest("Yubaz").contains("Yubaba St"));
        assertTrue(ac.suggest("Ra").contains("Radish Spirit Blvd"));
        assertFalse(ac.suggest("Rb").contains("Radish Spirit Blvd"));
        assertTrue(ac.suggest("adish Spirit Blvd").contains("Radish Spirit Blvd"));
        assertTrue(ac.suggest("Chihiro Ave").contains("Chihiro Ave"));
        assertTrue(ac.suggest("Chihiro AveYubaba St").contains("Chihiro Ave Yubaba St"));
        assertFalse(ac.suggest("Chihiro AveYubaba Ss").contains("Chihiro Ave Yubaba St"));
    }
}
