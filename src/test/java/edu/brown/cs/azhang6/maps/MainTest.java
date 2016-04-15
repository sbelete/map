package edu.brown.cs.azhang6.maps;

import edu.brown.cs.azhang6.db.Database;
import edu.brown.cs.azhang6.dimension.DimensionalDistance;
import edu.brown.cs.azhang6.dimension.LatLng;
import edu.brown.cs.azhang6.graphs.Graphs;
import edu.brown.cs.azhang6.graphs.Walk;
import edu.brown.cs.azhang6.kdtree.KDNodeParallel;
import edu.brown.cs.azhang6.kdtree.KDVertex;
import edu.brown.cs.azhang6.kdtree.LatLngKDTree;
import edu.brown.cs.azhang6.pair.OrderedPair;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests with a large dataset. After we ran these tests successfully, we
 * commented them out so mvn package doesn't take too long.
 *
 * @author aaronzhang
 */
public class MainTest {

  /**
   * Database.
   */
  private static Database db;

  /**
   * KD-tree.
   */
  private static KDVertex<Node> nodes;

  /**
   * For comparing doubles.
   */
  private static final double EPSILON = 0.001;

  /**
   * Opens database.
   */
  //@BeforeClass
  public static void setUpClass() {
    try {
      // Not in the GitHub directory since it's too large
      db = new Database("/home/aaronzhang/Downloads/maps.sqlite3");
      NodeProxy.setDB(db);
      WayProxy.setDB(db);
    } catch (ClassNotFoundException | SQLException e) {
      throw new RuntimeException(e);
    }
    // Setup KD-tree
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
      nodes = new LatLngKDTree<>(
        new KDNodeParallel<>(nodesToAdd, 0, Main.KD_TREE_PARALLEL_LEVEL));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      db.returnConnection(conn);
    }
  }

  /**
   * Closes database.
   */
  //@AfterClass
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
   * Testing Dijkstra's: example with a path.
   */
  //@Test
  public void testDijkstra1() {
    Node startNode = Node.of("/n/4182.7139.201273574");
    Node endNode = Node.of("/n/4182.7138.201383082");
    OrderedPair<Walk<Node, Way>, Double> shortestPath;
    shortestPath = Graphs.dijkstraAStarFail(
      startNode, n -> endNode.equals(n),
      n -> n.tunnelDistanceTo(endNode),
      Main.DIJKSTRA_FAIL, Main.DIJKSTRA_MAX_VERTICES);
    assertEquals(shortestPath.second(), 0.15068, EPSILON);
  }

  /**
   * Testing Dijkstra's: another example with a path.
   */
  //@Test
  public void testDijkstra2() {
    Node startNode = Node.of("/n/4182.7139.201273574");
    Node endNode = Node.of("/n/4182.7139.201273569");
    OrderedPair<Walk<Node, Way>, Double> shortestPath;
    shortestPath = Graphs.dijkstraAStarFail(
      startNode, n -> endNode.equals(n),
      n -> n.tunnelDistanceTo(endNode),
      Main.DIJKSTRA_FAIL, Main.DIJKSTRA_MAX_VERTICES);
    assertEquals(shortestPath.second(), 0.18504, EPSILON);
  }

  /**
   * Testing Dijkstra's: example with no path.
   */
  //@Test
  public void testDijkstra3() {
    Node startNode = Node.of("/n/4182.7139.201273574");
    // A residential point, so there should be no path
    Node endNode = Node.of("/n/4182.7138.1554287644");
    OrderedPair<Walk<Node, Way>, Double> shortestPath;
    shortestPath = Graphs.dijkstraAStarFail(
      startNode, n -> endNode.equals(n),
      n -> n.tunnelDistanceTo(endNode),
      Main.DIJKSTRA_FAIL, Main.DIJKSTRA_MAX_VERTICES);
    assertTrue(shortestPath == null);
  }

  /**
   * Testing nearest neighbor.
   */
  //@Test
  public void testNN1() {
    double latitude = 41.824057500778;
    double longitude = -71.38909810645524;
    List<DimensionalDistance<Node>> nearestNeighbors
      = nodes.nearestNeighbors(new LatLng(latitude, longitude), 1, null);
    Node nearestNeighbor = nearestNeighbors.get(0).getDimensional();
    assertEquals(nearestNeighbor.getId(), "/n/4182.7138.1554287644");
  }

  /**
   * Another test for nearest neighbor.
   */
  //@Test
  public void testNN2() {
    double latitude = 41.823219167698;
    double longitude = -71.39002477275925;
    List<DimensionalDistance<Node>> nearestNeighbors
      = nodes.nearestNeighbors(new LatLng(latitude, longitude), 1, null);
    Node nearestNeighbor = nearestNeighbors.get(0).getDimensional();
    assertEquals(nearestNeighbor.getId(), "/n/4182.7138.201383082");
  }
}
