package edu.brown.cs.azhang6.kdtree;

import edu.brown.cs.azhang6.stars.Star;
import edu.brown.cs.azhang6.stars.StarsReader;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for {@link KDTreeOracle}.
 *
 * @author aaronzhang
 */
public class KDTreeOracleTest {

  /**
   * Tests oracle against k-d trees that return correct results for queries.
   *
   * @throws Exception should not be thrown
   */
  @Test
  public void testCorrect() throws Exception {
    // File with 1000 lines
    StarsReader reader = new StarsReader("files/stardata-med.csv", ",");
    List<Star> stars = reader.readToList();
    KDTreeStub<Star> stub = new KDTreeStub<>(stars);
    KDTreeOracle<Star> stubOracle = new KDTreeOracle<>(stub, stars);
    assertTrue(stubOracle.testNearestNeighbors());
    assertTrue(stubOracle.testRadiusSearch());
  }

  /**
   * Tests oracle against k-d trees that return incorrect results for queries.
   *
   * @throws Exception should not be thrown
   */
  @Test
  public void testIncorrect() throws Exception {
    StarsReader reader = new StarsReader("files/stardata-med.csv", ",");
    List<Star> stars = reader.readToList();

    // Returns empty lists for queries
    IncorrectEmpty<Star> incorrect1 = new IncorrectEmpty<>(stars);
    KDTreeOracle<Star> incorrect1Oracle
      = new KDTreeOracle<>(incorrect1, stars);
    assertFalse(incorrect1Oracle.testNearestNeighbors());
    assertFalse(incorrect1Oracle.testRadiusSearch());

    // Omits an element in the correct list
    IncorrectOmitsElement<Star> incorrect2
      = new IncorrectOmitsElement<>(stars);
    KDTreeOracle<Star> incorrect2Oracle
      = new KDTreeOracle<>(incorrect2, stars);
    assertFalse(incorrect2Oracle.testNearestNeighbors());
    assertFalse(incorrect2Oracle.testRadiusSearch());

    // Duplicates first element in list
    IncorrectDuplicatesFirst<Star> incorrect3
      = new IncorrectDuplicatesFirst<>(stars);
    KDTreeOracle<Star> incorrect3Oracle
      = new KDTreeOracle<>(incorrect3, stars);
    assertFalse(incorrect3Oracle.testNearestNeighbors());
    assertFalse(incorrect3Oracle.testRadiusSearch());

    // Swaps two elements in the list
    IncorrectSwaps<Star> incorrect4 = new IncorrectSwaps<>(stars);
    KDTreeOracle<Star> incorrect4Oracle
      = new KDTreeOracle<>(incorrect4, stars);
    assertFalse(incorrect4Oracle.testNearestNeighbors());
    assertFalse(incorrect4Oracle.testRadiusSearch());

    // Reverses list
    IncorrectReverses<Star> incorrect5 = new IncorrectReverses<>(stars);
    KDTreeOracle<Star> incorrect5Oracle
      = new KDTreeOracle<>(incorrect5, stars);
    assertFalse(incorrect5Oracle.testNearestNeighbors());
    assertFalse(incorrect5Oracle.testRadiusSearch());

    // Modifies distance for an element in the list
    IncorrectModifiesDistance<Star> incorrect6
      = new IncorrectModifiesDistance<>(stars);
    KDTreeOracle<Star> incorrect6Oracle
      = new KDTreeOracle<>(incorrect6, stars);
    assertFalse(incorrect6Oracle.testNearestNeighbors());
    assertFalse(incorrect6Oracle.testRadiusSearch());
  }
}
