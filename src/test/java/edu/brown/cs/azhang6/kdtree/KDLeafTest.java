package edu.brown.cs.azhang6.kdtree;

import edu.brown.cs.azhang6.db.Database;
import edu.brown.cs.azhang6.dimension.LatLng;
import edu.brown.cs.azhang6.dimension.Point;
import edu.brown.cs.azhang6.maps.Node;
import edu.brown.cs.azhang6.maps.NodeProxy;
import edu.brown.cs.azhang6.stars.Star;
import edu.brown.cs.azhang6.stars.StarsReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for {@link KDLeaf}.
 *
 * @author aaronzhang
 */
public class KDLeafTest {

  /**
   * Unit tests for constructor {@link KDLeaf#KDLeaf(List)}.
   */
  @Test
  public void unitConstructor() {
    // Empty leaf
    KDLeaf<Point> instance1 = new KDLeaf<>(Collections.emptyList());
    assertTrue(instance1.getElements().isEmpty());

    // Leaf with elements
    ArrayList<Point> points2 = new ArrayList<>();
    Point point1 = new Point(1, 2, 1);
    Point point2 = new Point(2, 1.1, 0);
    Point point3 = new Point(1, 2, 1);
    points2.add(point1);
    points2.add(point2);
    points2.add(point3);
    KDLeaf<Point> instance2 = new KDLeaf<>(points2);
    assertTrue(instance2.getElements().size() == 3);
    assertTrue(instance2.getElements().contains(point1));
    assertTrue(instance2.getElements().contains(point2));
    assertTrue(instance2.getElements().contains(point3));

    // Leaf with maximum number of elements
    for (int i = 0; i < 7; i++) {
      points2.add(new Point(i, i, i));
    }
    KDLeaf<Point> instance3 = new KDLeaf<>(points2);
    assertTrue(instance3.getElements().size() == 10);
    for (int i = 0; i < 10; i++) {
      assertTrue(instance3.getElements().contains(points2.get(i)));
    }

    // Check that mutating points1 didn't affect instance2
    assertTrue(instance2.getElements().size() == 3);

    // List with too many elements
    points2.add(new Point(0, 0, 0));
    boolean caught4 = false;
    try {
      KDLeaf<Point> instance4 = new KDLeaf<>(points2);
    } catch (IllegalArgumentException e) {
      caught4 = true;
    }
    assertTrue(caught4);

    // Null argument
    boolean caught5 = false;
    try {
      KDLeaf<Point> instance5 = new KDLeaf<>(null);
    } catch (NullPointerException e) {
      caught5 = true;
    }
    assertTrue(caught5);
  }

  /**
   * Unit tests for {@link KDLeaf#size()}.
   */
  @Test
  public void unitSize() {
    // Empty leaf
    KDLeaf<Point> instance1 = new KDLeaf<>(Collections.emptyList());
    assertEquals(instance1.size(), 0);

    // Nonempty leaf
    ArrayList<Point> points2 = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      points2.add(new Point(i, i, i));
    }
    KDLeaf<Point> instance2 = new KDLeaf<>(points2);
    assertEquals(instance2.size(), 5);

    // Leaf with equal elements
    Point point3 = new Point(0, 0, 0);
    points2.add(point3);
    points2.add(point3);
    points2.add(point3);
    KDLeaf<Point> instance3 = new KDLeaf<>(points2);
    assertEquals(instance3.size(), 8);
  }

  /**
   * Unit tests for {@link KDLeaf#contains(Dimensional)}.
   */
  @Test
  public void unitContains() {
    // Empty leaf
    ArrayList<Point> points1 = new ArrayList<>();
    KDLeaf<Point> instance1 = new KDLeaf<>(points1);
    assertFalse(instance1.contains(new Point(0, 0, 0)));

    // Leaf with the specified element
    for (int i = 0; i < 10; i++) {
      points1.add(new Point(i, i, i));
    }
    KDLeaf<Point> instance2 = new KDLeaf<>(points1);
    assertTrue(instance2.contains(new Point(0, 0, 0)));
    assertTrue(instance2.contains(new Point(5, 5, 5)));
    assertTrue(instance2.contains(new Point(9, 9, 9)));

    // Leaf without the specified element
    assertFalse(instance2.contains(new Point(3, 4, 5)));

    // Null argument
    assertFalse(instance2.contains(null));
  }

  /**
   * Integration tests for
   * {@link KDLeaf#nearestNeighbors(Dimensional, int, Predicate)} and
   * {@link KDLeaf#withinRadius(Dimensional, double, Predicate)}. Uses
   * {@link StarsReader} to generate data. Explicitly tests edge cases, then
   * uses {@link KDTreeOracle} to run many tests.
   *
   * @throws Exception should not be thrown
   */
  @Test
  public void integration() throws Exception {
    // File with 10 lines
    StarsReader reader = new StarsReader("files/stardata-small.csv", ",");
    List<Star> stars = reader.readToList();
    KDLeaf<Star> tree = new KDLeaf<>(stars);

    /*
     Cases that should throw exceptions for nearest neighbors
     */
    // Dimensional argument is null
    boolean caught1 = false;
    try {
      tree.nearestNeighbors(null, 5, d -> false);
    } catch (NullPointerException e) {
      caught1 = true;
    }
    assertTrue(caught1);
    // n argument is negative
    /*
    boolean caught2 = false;
    try {
      tree.nearestNeighbors(new Point(0, 0, 0), 0, null);
    } catch (IllegalArgumentException e) {
      caught2 = true;
    }
    assertTrue(caught2);
    */
    boolean caught3 = false;
    try {
      tree.nearestNeighbors(new Point(0, 0, 0), -1, null);
    } catch (IllegalArgumentException e) {
      caught3 = true;
    }
    assertTrue(caught3);
    // Dimensional argument doesn't have exactly three dimensions
    boolean caught4 = false;
    try {
      tree.nearestNeighbors(new Point(0, 0), 1, null);
    } catch (IllegalArgumentException e) {
      caught4 = true;
    }
    assertTrue(caught4);
    boolean caught5 = false;
    try {
      tree.nearestNeighbors(new Point(0, 0, 0, 0), 1, null);
    } catch (IllegalArgumentException e) {
      caught5 = true;
    }
    assertTrue(caught5);

    /*
     Edge cases for nearest neighbors
     */
    // Request more neighbors than size of tree
    assertEquals(
        tree.nearestNeighbors(new Point(0, 0, 0), 10, null).size(), 9);
    // All stars are eliminated by the predicate
    assertTrue(tree.nearestNeighbors(
        new Point(0, 0, 0), 10, s -> true).isEmpty());

    /*
     Cases that should throw exceptions for radius search
     */
    // Dimensional argument is null
    boolean caught6 = false;
    try {
      tree.withinRadius(null, 1.5, d -> false);
    } catch (NullPointerException e) {
      caught6 = true;
    }
    assertTrue(caught6);
    // Dimensional argument doesn't have exactly three dimensions
    boolean caught7 = false;
    try {
      tree.withinRadius(new Point(0, 0), 1.5, null);
    } catch (IllegalArgumentException e) {
      caught7 = true;
    }
    assertTrue(caught7);
    boolean caught8 = false;
    try {
      tree.withinRadius(new Point(0, 0, 0, 0), 1.5, null);
    } catch (IllegalArgumentException e) {
      caught8 = true;
    }
    assertTrue(caught8);
    // r argument is negative
    boolean caught9 = false;
    try {
      tree.withinRadius(new Point(0, 0, 0), -0.1, null);
    } catch (IllegalArgumentException e) {
      caught9 = true;
    }
    assertTrue(caught9);

    /*
     Edge cases for radius search
     */
    // Radius of 0
    assertEquals(tree.withinRadius(new Point(0, 0, 0), 0, null).size(), 1);
    assertEquals(tree.withinRadius(new Point(1, 1, 1), 0, null).size(), 0);
    // All stars are within the radius
    assertEquals(tree.withinRadius(
        new Point(0, 0, 0), 10000, null).size(), 9);
    // All stars are eliminated by the predicate
    assertTrue(tree.withinRadius(
        new Point(0, 0, 0), 10000, s -> true).isEmpty());

    // Create an oracle to run many more tests
    KDTreeOracle oracle = new KDTreeOracle<>(tree, stars);
    assertTrue(oracle.testNearestNeighbors());
    assertTrue(oracle.testRadiusSearch());
  }
  
  /**
   * Uses oracle to test kd-tree with LatLng.
   */
  @Test
  public void testLatLng() {
      try {
          Database db = new Database("files/smallMaps.sqlite3");
          NodeProxy.setDB(db);
          Connection conn = db.getConnection();
          try (PreparedStatement prep = conn.prepareStatement(
              "SELECT id FROM node;")) {
              List<Node> nodes = db.query(prep).stream().map(s -> Node.of(s))
                  .collect(Collectors.toList());
              KDLeaf<Node> tree = new KDLeaf<>(nodes);
              KDTreeOracle<Node> oracle =
                  new KDTreeOracle<>(tree, nodes, LatLng::new);
              assertTrue(oracle.testNearestNeighbors());
              assertTrue(oracle.testRadiusSearch());
          } finally {
              db.returnConnection(conn);
          }
      } catch (ClassNotFoundException | SQLException e) {
          throw new RuntimeException(e);
      }
  }
}
