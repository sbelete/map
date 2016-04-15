package edu.brown.cs.azhang6.kdtree;

import edu.brown.cs.azhang6.dimension.Dimensional;
import edu.brown.cs.azhang6.dimension.DimensionalDistance;
import edu.brown.cs.azhang6.dimension.LatLng;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Wrapper around a kd-tree to allow handling latitude and longitude. A normal
 * kd-tree cannot handle latitude and longitude because of wrapping of longitude
 * (-180 degrees longitude = 180 degrees longitude).
 *
 * @author aaronzhang
 * @param <T> LatLng type
 */
public class LatLngKDTree<T extends LatLng> implements KDVertex<T> {

  /**
   * KD-tree.
   */
  private final KDVertex<T> tree;

  /**
   * Used to handle longitude wrapping.
   */
  private static final double WRAP_LNG = LatLng.MAX_LNG - LatLng.MIN_LNG;

  /**
   * New wrapper around the given KD-tree.
   *
   * @param tree kd-tree
   */
  public LatLngKDTree(KDVertex<T> tree) {
    this.tree = tree;
  }

  /**
   * Returns a point equivalent to the original LatLng, but with longitude
   * wrapped. So if the original has longitude 160, the result has longitude
   * -200.
   *
   * @param original original LatLng
   * @return wrapped LatLng
   */
  private static Dimensional wrapped(Dimensional orig) {
    LatLng original = (LatLng) orig;
    double originalLng = original.getLng();
    if (originalLng > 0) {
      return original.withCoordinate(1, originalLng
        - (LatLng.MAX_LNG - LatLng.MIN_LNG));
    } else if (originalLng < 0) {
      return original.withCoordinate(1, originalLng
        + (LatLng.MAX_LNG - LatLng.MIN_LNG));
    }
    return original;
  }

  @Override
  public int size() {
    return tree.size();
  }

  @Override
  public boolean contains(T element) {
    return tree.contains(element);
  }

  @Override
  public void nearestNeighbors(Dimensional d, int n, Predicate<T> ignore,
    List<DimensionalDistance<T>> current) {
    // Make two copies of current...
    List<DimensionalDistance<T>> currentCopy1 = new ArrayList<>(current);
    tree.nearestNeighbors(d, n, ignore, currentCopy1);
    // And search the second one from the wrapped LatLng
    List<DimensionalDistance<T>> currentCopy2 = new ArrayList<>(current);
    tree.nearestNeighbors(wrapped(d), n, ignore, currentCopy2);
    // Then merge the two lists, making sure not to duplicate dimensionals
    current.clear();
    current.addAll(merge(currentCopy1, currentCopy2));
  }

  @Override
  public void withinRadius(Dimensional d, double r, Predicate<T> ignore,
    List<DimensionalDistance<T>> current) {
    // Same idea as in nearestNeighbors
    List<DimensionalDistance<T>> currentCopy1 = new ArrayList<>(current);
    tree.withinRadius(d, r, ignore, currentCopy1);
    List<DimensionalDistance<T>> currentCopy2 = new ArrayList<>(current);
    tree.withinRadius(wrapped(d), r, ignore, currentCopy2);
    current.clear();
    // Use mergeAll instead of merge so we don't miss any points
    current.addAll(mergeAll(currentCopy1, currentCopy2));
  }

  /**
   * Merges two lists of query results.
   *
   * @param <T> LatLng type
   * @param l1 list of query results
   * @param l2 list of query results
   * @return merged list of query results
   */
  private static <T extends LatLng> List<DimensionalDistance<T>> merge(
    List<DimensionalDistance<T>> l1, List<DimensionalDistance<T>> l2) {
    // Size of output list
    int n = l1.size();
    Set<Dimensional> usedDimensionals = new HashSet<>();
    int l1Index = 0;
    int l2Index = 0;
    List<DimensionalDistance<T>> result = new ArrayList<>();
    while (result.size() < n) {
      // Pick the smallest remaining DimensionalDistance
      DimensionalDistance<T> l1dd = l1.get(l1Index);
      DimensionalDistance<T> l2dd = l2.get(l2Index);
      DimensionalDistance<T> smallest;
      if (l1dd.compareTo(l2dd) < 0) {
        smallest = l1dd;
        l1Index++;
      } else {
        smallest = l2dd;
        l2Index++;
      }
      // Check if the dimensional has already been used
      if (!usedDimensionals.contains(smallest.getDimensional())) {
        result.add(smallest);
        usedDimensionals.add(smallest.getDimensional());
      }
    }
    return result;
  }

  /**
   * Merges two lists of query results, but considers all elements.
   *
   * @param <T> LatLng type
   * @param l1 list of query results
   * @param l2 list of query results
   * @return merged list of query results
   */
  private static <T extends LatLng> List<DimensionalDistance<T>> mergeAll(
    List<DimensionalDistance<T>> l1, List<DimensionalDistance<T>> l2) {
    Set<Dimensional> usedDimensionals = new HashSet<>();
    int l1Index = 0;
    int l2Index = 0;
    List<DimensionalDistance<T>> result = new ArrayList<>();
    while (l1Index < l1.size() || l2Index < l2.size()) {
      // Pick the smallest remaining DimensionalDistance
      DimensionalDistance<T> l1dd
        = l1Index < l1.size() ? l1.get(l1Index) : null;
      DimensionalDistance<T> l2dd
        = l2Index < l2.size() ? l2.get(l2Index) : null;
      DimensionalDistance<T> smallest;
      if (l1dd != null && (l2dd == null || l1dd.compareTo(l2dd) < 0)) {
        smallest = l1dd;
        l1Index++;
      } else {
        smallest = l2dd;
        l2Index++;
      }
      // Check if the dimensional has already been used
      if (!usedDimensionals.contains(smallest.getDimensional())) {
        result.add(smallest);
        usedDimensionals.add(smallest.getDimensional());
      }
    }
    return result;
  }
}
