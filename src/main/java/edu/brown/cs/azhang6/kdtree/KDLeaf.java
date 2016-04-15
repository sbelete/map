package edu.brown.cs.azhang6.kdtree;

import edu.brown.cs.azhang6.dimension.Dimensional;
import edu.brown.cs.azhang6.dimension.DimensionalDistance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Leaf of a k-d tree. All elements in a k-d tree are stored in one of its
 * leaves. A leaf contains at most {@value KDLeaf#MAX_COUNT} elements.
 *
 * @author aaronzhang
 * @param <T> a dimensional type
 */
public class KDLeaf<T extends Dimensional> implements KDVertex<T> {

  /**
   * Maximum number of elements in a {@code KDLeaf}.
   */
  public static final int MAX_COUNT = 10;

  /**
   * Elements contained in this leaf.
   */
  private final List<T> elements;

  /**
   * Constructs a new {@code KDLeaf} with the elements in the given list. The
   * list should contain at most {@link MAX_COUNT} elements. External changes to
   * the list argument after construction will not affect this object.
   *
   * @param elements list of elements
   * @throws IllegalArgumentException if list is too large
   */
  public KDLeaf(List<T> elements) {
    int size = elements.size();
    if (size > MAX_COUNT) {
      throw new IllegalArgumentException(
        String.format("Too many elements in a KDLeaf: %d", size));
    }
    this.elements = new ArrayList<>(elements);
  }

  /**
   * Finds number of elements in this leaf. Useful for testing.
   *
   * @return number of elements in this leaf
   */
  @Override
  public int size() {
    return elements.size();
  }

  /**
   * Checks whether this leaf contains the specified element. Useful for
   * testing.
   *
   * @param element element to search for
   * @return whether the element is in the tree
   */
  @Override
  public boolean contains(T element) {
    return elements.contains(element);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void nearestNeighbors(Dimensional d, int n, Predicate<T> ignore,
    List<DimensionalDistance<T>> current) {
    // Check validity of arguments
    if (n < 0) {
      throw new IllegalArgumentException(
        "number of nearest neighbors must be nonnegative");
    }
    if (n == 0) {
      return;
    }

    // For each element in this leaf that shouldn't be ignored:
    for (T element : elements) {
      if (ignore == null || !ignore.test(element)) {
        // Calculate distance to the given dimensional
        new DimensionalDistance<>(element, element.distanceTo(d))
          .insertInto(current);
      }
    }
    // Remove elements until there are at most n elements
    while (current.size() > n) {
      current.remove(n);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void withinRadius(Dimensional d, double r, Predicate<T> ignore,
    List<DimensionalDistance<T>> current) {
    // Check validity of arguments
    if (r < 0) {
      throw new IllegalArgumentException(
        "radius must be a non-negative decimal");
    }

    // For each element in this leaf that shouldn't be ignored:
    for (T element : elements) {
      if (ignore == null || !ignore.test(element)) {
        // Check if the element is within the given distance
        double distance = element.distanceTo(d);
        if (distance <= r) {
          current.add(
            new DimensionalDistance<>(element, distance));
        }
      }
    }
  }

  /**
   * Returns an unmodifiable view of the list of elements in this leaf.
   *
   * @return elements in this leaf
   */
  public List<T> getElements() {
    return Collections.unmodifiableList(elements);
  }

  /**
   * @return string representation of this {@code KDLeaf}
   */
  @Override
  public String toString() {
    return String.format("KDLeaf with elements: %s", elements);
  }
}
