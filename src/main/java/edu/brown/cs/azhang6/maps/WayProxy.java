package edu.brown.cs.azhang6.maps;

import edu.brown.cs.azhang6.db.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Way proxy that queries a database for information.
 *
 * @author aaronzhang
 */
public class WayProxy extends Way {

  /**
   * Database with way information.
   */
  private static Database db;

  /**
   * Internal way.
   */
  private final Way internal = new Way(getId());

  /**
   * Whether internal has been filled.
   */
  private boolean filled = false;

  /**
   * New way proxy with ID.
   *
   * @param id ID
   */
  WayProxy(String id) {
    super(id);
    Way.cache(this);
  }

  /**
   * Uses the provided database for way information.
   *
   * @param db database
   */
  public static void setDB(Database db) {
    WayProxy.db = db;
  }

  /**
   * Gets IDs corresponding to way name.
   *
   * @param name name
   * @return list of IDs corresponding to name
   */
  public static List<String> idsForName(String name) {
    Connection conn = db.getConnection();
    try (PreparedStatement prep = conn.prepareStatement(
      "SELECT id FROM way WHERE name=?;")) {
      prep.setString(1, name);
      return db.query(prep);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      db.returnConnection(conn);
    }
  }

  /**
   * Fills internal way with information from database.
   *
   * @return internal way
   */
  private Way fill() {
    if (!filled) {
      Connection conn = db.getConnection();
      try (PreparedStatement prep = conn.prepareStatement(
        "SELECT * FROM way WHERE id=?;")) {
        prep.setString(1, id);
        db.query(prep, r -> {
          try {
            r.next();
            internal.start = r.getString("start");
            internal.end = r.getString("end");
            internal.name = r.getString("name");
            internal.type = r.getString("type");
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });
        filled = true;
      } catch (SQLException e) {
        throw new RuntimeException(e);
      } finally {
        db.returnConnection(conn);
      }
    }
    return internal;
  }

  @Override
  public String getStart() {
    return fill().getStart();
  }

  @Override
  public String getEnd() {
    return fill().getEnd();
  }

  @Override
  public String getName() {
    return fill().getName();
  }

  @Override
  public String getType() {
    return fill().getType();
  }
}
