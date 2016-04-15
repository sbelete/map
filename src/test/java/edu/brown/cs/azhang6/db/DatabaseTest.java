package edu.brown.cs.azhang6.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link Database}.
 *
 * @author aaronzhang
 */
public class DatabaseTest {

  /**
   * Database used for testing.
   */
  private static Database db;

  /**
   * Open database.
   */
  @BeforeClass
  public static void setUpClass() {
    try {
      db = new Database("files/smallMaps.sqlite3");
    } catch (ClassNotFoundException | SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Close database.
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
   * Unit tests for {@link Database#query(PreparedStatement)}.
   *
   * @throws SQLException should not be thrown
   */
  @Test
  public void testQuery_PreparedStatement() throws SQLException {
    Connection conn = db.getConnection();
    try (PreparedStatement prep1 = conn.prepareStatement(
      "SELECT * FROM node;")) {
      List<String> expected1 = new ArrayList<>();
      expected1.add("/n/0");
      expected1.add("/n/1");
      expected1.add("/n/2");
      expected1.add("/n/3");
      expected1.add("/n/4");
      expected1.add("/n/5");
      assertEquals(db.query(prep1), expected1);
    } finally {
      db.returnConnection(conn);
    }
  }

  /**
   * Unit tests for {@link Database#query(PreparedStatement, Function)}.
   *
   * @throws SQLException should not be thrown
   */
  @Test
  public void testQuery_PreparedStatement_Function() throws SQLException {
    Connection conn = db.getConnection();
    try (PreparedStatement prep1 = conn.prepareStatement(
      "SELECT * FROM way;")) {
      Set<String> expected1 = new TreeSet<>();
      expected1.add("Chihiro Ave");
      expected1.add("Radish Spirit Blvd");
      expected1.add("Sootball Ln");
      expected1.add("Kamaji Pl");
      expected1.add("Yubaba St");
      assertEquals(db.query(prep1, rs -> {
        try {
          Set<String> names = new TreeSet<>();
          while (rs.next()) {
            names.add(rs.getString("name"));
          }
          return names;
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }), expected1);
    } finally {
      db.returnConnection(conn);
    }
  }

  /**
   * Unit tests for {@link Database#query(PreparedStatement, Consumer)}.
   *
   * @throws SQLException should not be thrown
   */
  @Test
  public void testQuery_PreparedStatement_Consumer() throws Exception {
    Connection conn = db.getConnection();
    try (PreparedStatement prep1 = conn.prepareStatement(
      "SELECT * FROM way;")) {
      Set<String> expected1 = new TreeSet<>();
      expected1.add("Chihiro Ave");
      expected1.add("Radish Spirit Blvd");
      expected1.add("Sootball Ln");
      expected1.add("Kamaji Pl");
      expected1.add("Yubaba St");
      db.query(prep1, rs -> {
        try {
          Set<String> names = new TreeSet<>();
          while (rs.next()) {
            names.add(rs.getString("name"));
          }
          assertEquals(names, expected1);
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      });
    } finally {
      db.returnConnection(conn);
    }
  }

  /**
   * Unit tests for
   * {@link Database#has(String, String, String, String, String)}.
   */
  @Test
  public void testHas() {
    assertTrue(db.has("way", "start", "/n/0", "end", "/n/1"));
    assertTrue(db.has("way", "start", "/n/2", "end", "/n/5"));
    assertFalse(db.has("way", "start", "/n/0", "end", "/n/2"));
    assertFalse(db.has("way", "start", "/n/5", "end", "/n/4"));
  }
}
