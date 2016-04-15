package edu.brown.cs.azhang6.kdtree;

import edu.brown.cs.azhang6.db.Database;
import edu.brown.cs.azhang6.dimension.LatLng;
import edu.brown.cs.azhang6.dimension.Point;
import edu.brown.cs.azhang6.maps.Node;
import edu.brown.cs.azhang6.maps.NodeProxy;
import edu.brown.cs.azhang6.stars.Star;
import edu.brown.cs.azhang6.stars.StarsReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for {@link KDNode} and {@link KDNodeParallel}.
 *
 * @author aaronzhang
 */
public class KDNodeTest {

  /**
   * Unit tests for the constructor {@link KDNode#KDNode(List, int)}, the method
   * {@link KDNode#size()}, and the method {@link KDNode#contains(Dimensional)}.
   * The recursive nature of the KDNode makes it hard to test the constructor
   * and the methods separately.
   */
  @Test
  public void unit() {
    // Throw exception for empty node
    ArrayList<Point> points1 = new ArrayList<>();
    boolean caught1 = false;
    try {
      KDNode<Point> instance1 = new KDNode<>(points1, 0);
    } catch (IllegalArgumentException e) {
      caught1 = true;
    }
    assertTrue(caught1);

    // KDNode with a few points
    for (int i = 0; i < 5; i++) {
      points1.add(new Point(i, 0));
    }
    KDNode<Point> instance2 = new KDNode<>(points1, 0);
    assertEquals(instance2.size(), 5);
    for (Point p : points1) {
      assertTrue(instance2.contains(p));
    }

    // KDNode with more points
    for (int i = 5; i < 50; i++) {
      points1.add(new Point(i, 0));
    }
    KDNode<Point> instance3 = new KDNode<>(points1, 0);
    assertEquals(instance3.size(), 50);
    for (Point p : points1) {
      assertTrue(instance3.contains(p));
    }

    // Calling contains with a null argument
    assertFalse(instance3.contains(null));

    // Check that instance2 isn't affected by modifying points1
    assertEquals(instance2.size(), 5);
    assertFalse(instance2.contains(new Point(49, 0)));

    // Throw an exception for a dimension mismatch
    points1.add(new Point(50));
    boolean caught4 = false;
    try {
      KDNode<Point> instance4 = new KDNode<>(points1, 0);
    } catch (IllegalArgumentException e) {
      caught4 = true;
    }
    assertTrue(caught4);

    // Null argument in constructor
    boolean caught5 = false;
    try {
      KDNode<Point> instance5 = new KDNode<>(null, 0);
    } catch (NullPointerException e) {
      caught5 = true;
    }
    assertTrue(caught5);
  }

  /**
   * Integration tests for
   * {@link KDNode#nearestNeighbors(Dimensional, int, Predicate)} and
   * {@link KDNode#withinRadius(Dimensional, double, Predicate)}. Uses
   * {@link StarsReader} to generate data. Explicitly tests edge cases, then
   * uses {@link KDTreeOracle} to run many tests.
   *
   * @throws Exception should not be thrown
   */
  @Test
  public void integration() throws Exception {
    // File with 1000 lines
    StarsReader reader = new StarsReader("files/stardata-med.csv", ",");
    List<Star> stars = reader.readToList();
    KDNode<Star> tree = new KDNode<>(stars, 0);

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
    assertEquals(tree.nearestNeighbors(
      new Point(0, 0, 0), 1000, null).size(), 999);
    // All stars are eliminated by the predicate
    assertTrue(tree.nearestNeighbors(
      new Point(0, 0, 0), 1000, s -> true).isEmpty());

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
      new Point(0, 0, 0), Integer.MAX_VALUE, null).size(), 999);
    // All stars are eliminated by the predicate
    assertTrue(tree.withinRadius(
      new Point(0, 0, 0), Integer.MAX_VALUE, s -> true).isEmpty());

    // Create an oracle to run many more tests
    KDTreeOracle<Star> oracle = new KDTreeOracle<>(tree, stars);
    assertTrue(oracle.testNearestNeighbors());
    assertTrue(oracle.testRadiusSearch());

    // Oracle tests for parallel kd-tree
    KDNodeParallel<Star> treeParallel = new KDNodeParallel<>(stars, 0, 3);
    KDTreeOracle<Star> oracleParallel = new KDTreeOracle<>(treeParallel, stars);
    assertTrue(oracleParallel.testNearestNeighbors());
    assertTrue(oracleParallel.testRadiusSearch());
  }
}
