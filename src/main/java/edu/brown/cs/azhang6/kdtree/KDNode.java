package edu.brown.cs.azhang6.kdtree;

import edu.brown.cs.azhang6.dimension.Dimensional;
import edu.brown.cs.azhang6.dimension.DimensionalDistance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Node of a k-d tree. Nodes contain no dimensional elements themselves; all the
 * elements in a k-d tree are contained in one of its leaves.
 *
 * @author aaronzhang
 * @param <T> a dimensional type
 */
public class KDNode<T extends Dimensional> implements KDVertex<T> {

  /**
   * Index of coordinate to split.
   */
  private final int coordinate;

  /**
   * Value of coordinate to split at.
   */
  private final double split;
  
  /**
   * Minimum value of coordinate to be stored in this node.
   */
  private final double min;
  
  /**
   * Maximum value of coordinate to be stored in this node.
   */
  private final double max;

  /**
   * Left subtree.
   */
  private final KDVertex<T> left;

  /**
   * Right subtree.
   */
  private final KDVertex<T> right;

  /**
   * If a list of elements contains more than {@link RANDOM_MEDIAN} elements and
   * calculating its median is required, the median will be estimated.
   */
  private static final int RANDOM_MEDIAN = 20;

  /**
   * Constructs a new {@code KDNode} with the given elements and index of
   * coordinate to split. The list of elements should be nonempty, and every
   * element in the list should have the same number of dimensions. External
   * changes to the list argument after construction will not affect this
   * object.
   *
   * @param elements nonempty list of elements
   * @param coordinate index of coordinate to split
   * @param min minimum splitting coordinate
   * @param max maximum splitting coordiante
   * @throws IllegalArgumentException if list of elements is empty
   */
  public KDNode(List<T> elements, int coordinate, double min, double max)
      throws IllegalArgumentException {
    this.coordinate = coordinate;
    this.min = min;
    this.max = max;
    List<T> elementsCopy = new ArrayList<>(elements);

    // Find median coordinate, or estimate the median if list is long
    int size = elementsCopy.size();
    if (size == 0) {
      throw new IllegalArgumentException("Constructing empty KDNode");
    }
    if (size <= RANDOM_MEDIAN) {
      this.split = median(elementsCopy);
    } else {
      List<T> sample = new ArrayList<>();
      for (int i = 0; i < 20; i++) {
        sample.add(elementsCopy.get((int) (Math.random() * size)));
      }
      this.split = median(sample);
    }

    // Put each element in either the left or right subtree
    List<T> leftList = new ArrayList<>();
    List<T> rightList = new ArrayList<>();
    for (T element : elementsCopy) {
      if (element.getCoordinate(coordinate) < split) {
        leftList.add(element);
      } else {
        rightList.add(element);
      }
    }

    // The subtrees are either nodes or leaves, based on size
    int numDimensions = elementsCopy.get(0).numDimensions();
    if (leftList.size() <= KDLeaf.MAX_COUNT) {
      left = new KDLeaf<>(leftList, min, split);
    } else {
      left = new KDNode<>(leftList, (coordinate + 1) % numDimensions, min, split);
    }
    if (rightList.size() <= KDLeaf.MAX_COUNT) {
      right = new KDLeaf<>(rightList, split, max);
    } else {
      right = new KDNode<>(rightList, (coordinate + 1) % numDimensions, split, max);
    }
  }

  /**
   * Calculates the median of the list of elements relative to the coordinate to
   * split. May mutate the list in the process.
   *
   * @param elements list to find median of, which must be modifiable
   * @return median coordinate of list of elements
   */
  private double median(List<T> elements) {
    Collections.sort(elements, (T e1, T e2)
        -> e1.getCoordinate(coordinate) < e2.getCoordinate(coordinate)
            ? -1 : 1);
    return elements.get(elements.size() / 2).getCoordinate(coordinate);
  }

  /**
   * Finds number of elements in the k-d tree beginning at this node. The nodes
   * and leaves in the tree do not count as elements; only the elements within
   * each leaf are counted. Useful for testing.
   *
   * @return number of elements in this node
   */
  @Override
  public int size() {
    return left.size() + right.size();
  }

  /**
   * Checks whether this node contains the specified element. Useful for
   * testing.
   *
   * @param element element to search for
   * @return whether the element is in this node
   */
  @Override
  public boolean contains(T element) {
    if (element == null) {
      return false;
    }
    try {
      if (element.getCoordinate(coordinate) < split) {
        return left.contains(element);
      } else {
        return right.contains(element);
      }
    } catch (IllegalArgumentException e) {
      // If the element does not have the splitting coordinate
      return false;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void nearestNeighbors(Dimensional d, int n, Predicate<T> ignore,
      List<DimensionalDistance<T>> current)
      throws IllegalArgumentException, NullPointerException {
    // Check validity of arguments
    if (n < 0) {
      throw new IllegalArgumentException(
          "number of nearest neighbors must be nonnegative");
    }
    if (n == 0) {
      return;
    }

    // Which branch to check first
    boolean leftFirst = d.getCoordinate(coordinate) < split;
    if (leftFirst) {
      left.nearestNeighbors(d, n, ignore, current);
    } else {
      right.nearestNeighbors(d, n, ignore, current);
    }

    // If we've found fewer than n neighbors, definitely check the other branch
    if (current.size() < n) {
        if (leftFirst) {
        right.nearestNeighbors(d, n, ignore, current);
      } else {
        left.nearestNeighbors(d, n, ignore, current);
      }
    } else {
        // Otherwise, determine if we need to check the other branch
        double farthest = current.get(current.size() - 1).getDistance();
        if (d.distanceTo(d.withCoordinate(coordinate, split)) < farthest) {
            if (leftFirst) {
                right.nearestNeighbors(d, n, ignore, current);
            } else {
                left.nearestNeighbors(d, n, ignore, current);
            }
        } else if (leftFirst) {
            // Here, we're checking for possible "wrapping around"
            if (d.distanceTo(d.withCoordinate(coordinate, right.getMax()))
                    < farthest) {
                right.nearestNeighbors(d, n, ignore, current);
            }
        } else {
            if (d.distanceTo(d.withCoordinate(coordinate, left.getMin()))
                    < farthest) {
                left.nearestNeighbors(d, n, ignore, current);
            }
        }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void withinRadius(Dimensional d, double r, Predicate<T> ignore,
      List<DimensionalDistance<T>> current)
      throws IllegalArgumentException, NullPointerException {
    // Check validity of arguments
    if (r < 0) {
      throw new IllegalArgumentException(
          "radius must be a non-negative decimal");
    }

    // Which branch to check first
    boolean leftFirst = d.getCoordinate(coordinate) < split;
    if (leftFirst) {
      left.withinRadius(d, r, ignore, current);
    } else {
      right.withinRadius(d, r, ignore, current);
    }
    
    // Determine if we need to check the other branch
    if (d.distanceTo(d.withCoordinate(coordinate, split)) < r) {
        if (leftFirst) {
            right.withinRadius(d, r, ignore, current);
        } else {
            left.withinRadius(d, r, ignore, current);
        }
    } else if (leftFirst) {
        if (d.distanceTo(d.withCoordinate(coordinate, right.getMax())) < r) {
            right.withinRadius(d, r, ignore, current);
        }
    } else {
        if (d.distanceTo(d.withCoordinate(coordinate, left.getMin())) < r) {
            left.withinRadius(d, r, ignore, current);
        }
    }
  }

  /**
   * @return left subtree
   */
  public KDVertex getLeft() {
    return left;
  }

  /**
   * @return right subtree
   */
  public KDVertex getRight() {
    return right;
  }

  /**
   * @return coordinate to split
   */
  public int getCoordinate() {
    return coordinate;
  }

  /**
   * @return value of coordinate to split at
   */
  public double getSplit() {
    return split;
  }

  /**
   * @return string representation of this {@code KDNode}
   */
  @Override
  public String toString() {
    return String.format("KDNode splitting coordinate %d at %f",
        coordinate, split);
  }

    /**
     * {@inheritDoc}
     */
  @Override
    public double getMin() {
        return min;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMax() {
        return max;
    }
}
