package edu.brown.cs.azhang6.kdtree;

import edu.brown.cs.azhang6.db.Database;
import edu.brown.cs.azhang6.dimension.LatLng;
import edu.brown.cs.azhang6.maps.Node;
import edu.brown.cs.azhang6.maps.NodeProxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Uses KD-tree oracle to test {@link LatLngKDTree}.
 *
 * @author aaronzhang
 */
public class LatLngKDTreeTest {

  /**
   * Tests LatLngKDTree using oracle.
   */
  @Test
  public void test() {
    try {
      // File with ~1000 nodes
      Database db = new Database("files/medMaps.sqlite3");
      NodeProxy.setDB(db);
      Connection conn = db.getConnection();
      try (PreparedStatement prep = conn.prepareStatement(
        "SELECT id FROM node;")) {
        List<Node> nodes = db.query(prep).stream().map(s -> Node.of(s))
          .collect(Collectors.toList());
        KDNode<Node> origTree = new KDNode<>(nodes, 0);
        LatLngKDTree<Node> tree = new LatLngKDTree(origTree);
        KDTreeOracle<Node> oracle
          = new KDTreeOracle<>(tree, nodes, LatLng::new);
        assertTrue(oracle.testNearestNeighbors());
        assertTrue(oracle.testRadiusSearch());
      } finally {
        db.returnConnection(conn);
      }
    } catch (ClassNotFoundException | SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * More tests that focus on longitude wrapping.
   */
  @Test
  public void test2() {
    // Should wrap around
    List<LatLng> lls1 = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      lls1.add(randomLatLng());
    }
    LatLng a1 = new LatLng(0, -179.9);
    LatLng b1 = new LatLng(0, 179.9);
    lls1.add(a1);
    lls1.add(b1);
    KDNode<LatLng> origTree1 = new KDNode<>(lls1, 0);
    LatLngKDTree<LatLng> tree1 = new LatLngKDTree(origTree1);
    assertEquals(tree1.nearestNeighbors(a1, 1, l -> l.equals(a1))
      .get(0).getDimensional(), b1);
    // Should wrap around
    List<LatLng> lls2 = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      lls2.add(randomLatLng());
    }
    LatLng a2 = new LatLng(0.12, -179.99);
    LatLng b2 = new LatLng(-0.34, 179.99);
    lls2.add(a2);
    lls2.add(b2);
    KDNode<LatLng> origTree2 = new KDNode<>(lls2, 0);
    LatLngKDTree<LatLng> tree2 = new LatLngKDTree(origTree2);
    assertEquals(tree2.nearestNeighbors(a2, 1, l -> l.equals(a2))
      .get(0).getDimensional(), b2);
    // Should not wrap around
    List<LatLng> lls3 = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      lls3.add(randomLatLng());
    }
    LatLng a3 = new LatLng(0, -179.9);
    LatLng b3 = new LatLng(0, 179.9);
    LatLng c3 = new LatLng(0.12, -179.89);
    lls3.add(a3);
    lls3.add(b3);
    lls3.add(c3);
    KDNode<LatLng> origTree3 = new KDNode<>(lls3, 0);
    LatLngKDTree<LatLng> tree3 = new LatLngKDTree(origTree3);
    assertEquals(tree3.nearestNeighbors(a3, 1, l -> l.equals(a3))
      .get(0).getDimensional(), c3);
  }

  /**
   * More tests that focus on longitude wrapping: make sure results are merged
   * correctly.
   */
  @Test
  public void test3() {
    List<LatLng> lls1 = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      lls1.add(randomLatLng());
    }
    LatLng a1 = new LatLng(0, -179.9);
    LatLng b1 = new LatLng(0, 179.9);
    lls1.add(a1);
    lls1.add(b1);
    KDNode<LatLng> origTree1 = new KDNode<>(lls1, 0);
    LatLngKDTree<LatLng> tree1 = new LatLngKDTree(origTree1);
    assertEquals(tree1.nearestNeighbors(a1, 21, l -> l.equals(a1))
      .get(0).getDimensional(), b1);
  }

  /**
   * Generates random LatLng to use in tests.
   *
   * @return random LatLng
   */
  private static LatLng randomLatLng() {
    double latMag = Math.random() * 89;
    double lat = latMag * (Math.random() > 0.5 ? 1 : -1);
    double lngMag = Math.random() * 179;
    double lng = lngMag * (Math.random() > 0.5 ? 1 : -1);
    return new LatLng(lat, lng);
  }
}
