package edu.brown.cs.azhang6.kdtree;

import edu.brown.cs.azhang6.dimension.Dimensional;
import edu.brown.cs.azhang6.dimension.DimensionalDistance;
import edu.brown.cs.azhang6.dimension.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Oracle that checks if a {@link KDVertex} returns correct results for nearest
 * neighbor and radius search queries. Generates random inputs to nearest
 * neighbor and radius search queries. Does not explicity test edge cases and
 * invalid inputs; these cases should be tested separately.
 *
 * @author aaronzhang
 */
class KDTreeOracle<T extends Dimensional> {

  /**
   * When comparing two doubles, they must be within
   * {@value KDTreeOracle#EPSILON} to be considered equal.
   */
  private static final double EPSILON = 0.000001;

  /**
   * Number of inputs to generate. Tests the subject using
   * {@value KDTreeOracle#NUM_INPUTS} inputs of each type:<br/>
   * - nearest neighbor with exact input<br/>
   * - nearest neighbor with inexact input<br/>
   * - radius search with exact input<br/>
   * - radius search with inexact input<br/>
   * where exact inputs match one of the elements in the list provided to the
   * constructor.
   */
  private static final int NUM_INPUTS = 25;

  /**
   * Probability that a query will ignore its dimensional input.
   */
  private static final double IGNORE_FREQ = 0.25;

  /**
   * Subject to test for correctness.
   */
  private final KDVertex<T> subject;

  /**
   * Stub that returns correct results for queries.
   */
  private final KDTreeStub<T> correct;

  /**
   * List of elements to generate queries with.
   */
  private final ArrayList<T> elements = new ArrayList<>();
  
  /**
   * Constructor.
   */
  private Function<double[], Dimensional> constructor;

  /**
   * Creates a new {@code KDTreeOracle} that tests the subject against correct
   * results for queries on the given list of elements. The list of elements
   * should be nonempty.
   *
   * @param subject subject to test
   * @param elements nonempty list of elements
   * @throws IllegalArgumentException if list is empty
   * @throws NullPointerException if either argument is null
   */
  KDTreeOracle(KDVertex<T> subject, List<T> elements)
      throws IllegalArgumentException, NullPointerException {
    if (subject == null) {
      throw new NullPointerException(
          "Constructing KDTreeOracle with null subject");
    }
    this.subject = subject;
    if (elements == null) {
      throw new NullPointerException(
          "Constructing KDTreeOracle with null list");
    }
    if (elements.isEmpty()) {
      throw new IllegalArgumentException(
          "Constructing KDTreeOracle with no elements");
    }
    this.correct = new KDTreeStub<>(elements);
    this.elements.addAll(elements);
  }
  
  /**
   * Same as before, but allows providing a constructor for generating inexact
   * input.
   * 
   * @param subject subject
   * @param elements elements
   * @param constructor constructor
   */
  KDTreeOracle(KDVertex<T> subject, List<T> elements,
      Function<double[], Dimensional> constructor) {
      this(subject, elements);
      this.constructor = constructor;
  }

  /**
   * Tests subject with nearest neighbors queries. If the subject fails to
   * produce a valid result or throws an exception for any query, this method
   * returns {@code false}.
   *
   * @return whether the subject passes the tests
   */
  boolean testNearestNeighbors() {
    return testNearestNeighbors(this::generateDimensionalExact)
        && testNearestNeighbors(this::generateDimensionalInexact);
  }

  /**
   * Tests subject with nearest neighbors queries. This method accepts a
   * supplier of dimensional input for the queries. If the subject fails to
   * produce a valid result or throws an exception for any query, this method
   * returns {@code false}.
   *
   * @param generateInput supplier of dimensional input
   * @return whether the subject passes the tests
   */
  private boolean testNearestNeighbors(Supplier<Dimensional> generateInput) {
    for (int i = 0; i < NUM_INPUTS; i++) {
      // Generate inputs
      Dimensional queryInput = generateInput.get();
      int n = generateN();
      Predicate<T> pred = generatePredicate(queryInput);

      // Get the subject's output and the correct output
      List<DimensionalDistance<T>> subjectOutput;
      try {
        subjectOutput = subject.nearestNeighbors(queryInput, n, pred);
      } catch (Exception e) {
        return false;
      }
      List<DimensionalDistance<T>> correctOutput
          = correct.nearestNeighbors(queryInput, n, pred);

      // Check subject's output for validity
      if (!isValid(subjectOutput, correctOutput, queryInput)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Tests subject with radius search queries. If the subject fails to produce a
   * valid result or throws an exception for any query, this method returns
   * {@code false}.
   *
   * @return whether the subject passes the tests
   */
  boolean testRadiusSearch() {
    return testRadiusSearch(this::generateDimensionalExact)
        && testRadiusSearch(this::generateDimensionalInexact);
  }

  /**
   * Tests subject with radius search queries. This method accepts a supplier of
   * dimensional input for the queries. If the subject fails to produce a valid
   * result or throws an exception for any query, this method returns
   * {@code false}.
   *
   * @param generateInput supplier of dimensional input
   * @return whether the subject passes the tests
   */
  private boolean testRadiusSearch(Supplier<Dimensional> generateInput) {
    for (int i = 0; i < NUM_INPUTS; i++) {
      // Generate inputs
      Dimensional queryInput = generateInput.get();
      double r = generateR();
      Predicate<T> pred = generatePredicate(queryInput);

      // Get the subject's output and the correct output
      List<DimensionalDistance<T>> subjectOutput;
      try {
        subjectOutput = subject.withinRadius(queryInput, r, pred);
      } catch (Exception e) {
        return false;
      }
      List<DimensionalDistance<T>> correctOutput
          = correct.withinRadius(queryInput, r, pred);

      // Check subject's output for validity
      if (!isValid(subjectOutput, correctOutput, queryInput)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Generates a random dimensional that matches one of the listed elements.
   *
   * @return random dimensional
   */
  private Dimensional generateDimensionalExact() {
    return elements.get((int) (Math.random() * elements.size()));
  }

  /**
   * Generates a random dimensional that might not match any listed element.
   *
   * @return random dimensional
   */
  private Dimensional generateDimensionalInexact() {
    // Pick two random elements and use them to generate a reasonable input
    // for a query
    Dimensional e1 = elements.get((int) (Math.random() * elements.size()));
    Dimensional e2 = elements.get((int) (Math.random() * elements.size()));
    double[] coordinates = new double[e1.numDimensions()];
    for (int i = 0; i < coordinates.length; i++) {
        double random = Math.random();
        coordinates[i] =
            random * e1.getCoordinate(i) + (1 - random) * e2.getCoordinate(i);
    }
    if (constructor == null) {
        return new Point(coordinates);
    }
    return constructor.apply(coordinates);
  }

  /**
   * Generates a random integer input for an n-nearest neighbors query.
   *
   * @return random integer
   */
  private int generateN() {
    if (elements.size() < 100) {
      return 1 + (int) (Math.random() * 10);
    } else {
      return 1 + (int) (Math.random() * elements.size() / 10);
    }
  }

  /**
   * Generates a random double input for a radius search query.
   *
   * @return random double
   */
  private double generateR() {
    Dimensional e1 = elements.get((int) (Math.random() * elements.size()));
    Dimensional e2 = elements.get((int) (Math.random() * elements.size()));
    return e1.distanceTo(e2) * Math.random() * 2;
  }

  /**
   * Generates a random predicate input for a nearest neighbors or radius search
   * query. The random predicate causes the query either to ignore or not ignore
   * the query point.
   *
   * @param queryPoint dimensional input for query
   * @return random predicate for query
   */
  private Predicate<T> generatePredicate(Dimensional queryPoint) {
    if (Math.random() < IGNORE_FREQ) {
      return d -> d.equals(queryPoint);
    } else {
      return null;
    }
  }

  /**
   * Checks if the subject's output matches the correct output. The subject's
   * output does not necessarily have to match the correct output exactly to be
   * considered correct, because of possible ties.
   *
   * @param subjectOutput subject's output
   * @param correctOutput correct output
   * @param queryPoint dimensional submitted to query
   * @return whether subject's output is valid
   * @throws NullPointerException if dimensional is null
   */
  private boolean isValid(List<DimensionalDistance<T>> subjectOutput,
      List<DimensionalDistance<T>> correctOutput,
      Dimensional queryPoint) {
    // Check validity of arguments
    if (queryPoint == null) {
      throw new NullPointerException(
          "Calling isValid() with null dimensional");
    }

    // Check that both outputs are null or not null
    if (subjectOutput == null || correctOutput == null) {
      return subjectOutput == correctOutput;
    }

    // Check that both outputs are same size
    if (subjectOutput.size() != correctOutput.size()) {
      return false;
    }

    /*
     For each element in the subject's output, check that:
     - the element and the corresponding eleemnt in the correct output are
     either both null or both not null
     - the dimensional is in the list of elements
     - the distance equals the distance of the corresponding element in the
     correct output
     - the distance to the query point is correct
     */
    for (int i = 0; i < subjectOutput.size(); i++) {
      DimensionalDistance<T> subjectDD = subjectOutput.get(i);
      DimensionalDistance<T> correctDD = correctOutput.get(i);
      if (subjectDD == null || correctDD == null) {
        if (subjectDD == correctDD) {
          continue;
        } else {
          return false;
        }
      }
      if (!elements.contains(subjectDD.getDimensional())) {
        return false;
      }
      if (Math.abs(subjectDD.getDistance() - correctDD.getDistance())
          > EPSILON) {
        return false;
      }
      if (Math.abs(subjectDD.getDimensional().distanceTo(queryPoint)
          - subjectDD.getDistance()) > EPSILON) {
        return false;
      }
    }
    return true;
  }

  /**
   * @return string representation of this {@code KDTreeOracle}
   */
  @Override
  public String toString() {
    return String.format("KDTreeOracle testing %s", subject);
  }
}
