package edu.brown.cs.azhang6.kdtree;

import edu.brown.cs.azhang6.dimension.DimensionalDistance;
import edu.brown.cs.azhang6.dimension.Dimensional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Vertex of a k-d tree of dimensional objects.
 *
 * @author aaronzhang
 * @param <T> a dimensional type
 */
public interface KDVertex<T extends Dimensional> {

  /**
   * Finds number of elements in this vertex. Useful for testing.
   *
   * @return number of elements in this vertex
   */
  int size();
  
  /**
   * Minimum value of splitting coordinate that can be stored in this vertex.
   * 
   * @return minimum value of splitting coordinate
   */
  double getMin();
  
  /**
   * Maximum value of splitting coordinate that can be stored in this vertex.
   * 
   * @return maximum value of splitting coordinate
   */
  double getMax();

  /**
   * Checks whether this vertex contains an element. Useful for testing.
   *
   * @param element element to search for
   * @return whether vertex contains the element
   */
  boolean contains(T element);

  /**
   * Finds up to {@code n} nearest neighbors of the given {@link Dimensional}.
   * Searches this vertex and its subvertices, but elements that satisfy the
   * predicate are ignored. If the predicate is null, no elements are ignored.
   * This method takes a list of the nearest neighbors found so far, sorted with
   * the nearest first, and updates the list if one or more elements in this
   * vertex are closer than some element in the list. The list must be
   * modifiable.
   *
   * @param d point to find nearest neighbors from
   * @param n number of nearest neighbors, a nonnegative integer
   * @param ignore ignore elements in this vertex that satisfy this predicate
   * @param current nearest neighbors found so far, sorted nearest first; list
   * must be modifiable
   * @throws IllegalArgumentException if any distances between the given
   * dimensional and elements in this vertex are not defined; or if given list
   * is not modifiable; or if n is not a positive integer
   * @throws NullPointerException if {@code d} or {@code current} are null
   */
  void nearestNeighbors(Dimensional d, int n, Predicate<T> ignore,
      List<DimensionalDistance<T>> current)
      throws IllegalArgumentException, NullPointerException;

  /**
   * Finds up to {@code n} nearest neighbors of the given {@link Dimensional}.
   * Searches this vertex and its subvertices, but elements that satisfy the
   * predicate are ignored. If the predicate is null, no elements are ignored.
   * Wrapper method for
   * {@link KDVertex#nearestNeighbors(Dimensional, int, Predicate, List)}.
   *
   * @param d point to find nearest neighbors from
   * @param n number of nearest neighbors, a positive integer
   * @param ignore ignore elements that satisfy this predicate
   * @return up to n nearest neighbors of the given point in this vertex
   * @throws IllegalArgumentException if any distances between the given point
   * and elements in this vertex are undefined; or if n is not a positive
   * integer
   * @throws NullPointerException if {@code d} is null
   */
  default List<DimensionalDistance<T>> nearestNeighbors(Dimensional d, int n,
      Predicate<T> ignore)
      throws IllegalArgumentException, NullPointerException {
    List<DimensionalDistance<T>> nn = new ArrayList<>();
    nearestNeighbors(d, n, ignore, nn);
    return nn;
  }

  /**
   * Finds all elements within distance {@code r} of {@link Dimensional}
   * {@code d}. Elements a distance of exactly {@code r} from {@code d} are
   * included. Searches this vertex and its subvertices, but elements that
   * satisfy the predicate are ignored. If the predicate is null, no elements
   * are ignored. This method takes a list of elements that have been found so
   * far and updates the list. The list must be modifiable.
   *
   * <p>
   * This method does not guarantee that the list is sorted by distance in
   * ascending order. Use the wrapper method
   * {@link KDVertex#withinRadius(Dimensional, double, Predicate)} for a sorted
   * list.</p>
   *
   * @param d search for elements in the given radius around this point
   * @param r radius around the given point to search, nonnegative
   * @param ignore elements that satisfy this predicate are ignored
   * @param current elements found so far; list must be modifiable
   * @throws IllegalArgumentException if any distances between the given point
   * and elements in this vertex are undefined; or the given list is immutable;
   * or if r is negative
   * @throws NullPointerException if {@code d} or {@code current} is null
   */
  void withinRadius(Dimensional d, double r, Predicate<T> ignore,
      List<DimensionalDistance<T>> current)
      throws IllegalArgumentException, NullPointerException;

  /**
   * Finds all elements within distance {@code r} of {@link Dimensional}
   * {@code d}. Elements a distance of exactly {@code r} from {@code d} are
   * included. Searches this vertex and its subvertices, but elements that
   * satisfy the predicate are ignored. If the predicate is null, no elements
   * are ignored. This method returns a list sorted in ascending order by
   * distance. Wrapper method for
   * {@link KDVertex#withinRadius(Dimensional, double, Predicate, List)}.
   *
   * @param d search for elements in the given radius around this point
   * @param r radius, nonnegative
   * @param ignore ignore elements that satisfy this predicate
   * @return elements within radius of given point
   * @throws IllegalArgumentException if any distances between the given point
   * and elements in this vertex are undefined; or if r is negative
   * @throws NullPointerException if {@code d} is null
   */
  default List<DimensionalDistance<T>> withinRadius(Dimensional d, double r,
      Predicate<T> ignore)
      throws IllegalArgumentException, NullPointerException {
    List<DimensionalDistance<T>> wr = new ArrayList<>();
    withinRadius(d, r, ignore, wr);
    Collections.sort(wr);
    return wr;
  }
}
