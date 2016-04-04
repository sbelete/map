package edu.brown.cs.azhang6.graphs;

import edu.brown.cs.azhang6.graph.WEdge;
import edu.brown.cs.azhang6.graph.ImmutableWEdge;
import edu.brown.cs.azhang6.graph.WVertex;
import edu.brown.cs.azhang6.pair.OrderedPair;

import java.util.function.ToDoubleFunction;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.function.Predicate;

/**
 * Static methods for operations on graphs. For example, implements Dijkstra's
 * algorithm for digraphs with weighted edges.
 *
 * @author aaronzhang
 */
public final class Graphs {

  /**
   * Can't be instantiated.
   */
  private Graphs() {

  }

  /**
   * Runs Dijkstra's algorithm from the root vertex until a vertex that
   * satisfies the predicate is reached. Returns a walk from the root to the
   * vertex satisfying the predicate and the weight of the walk. The walk is a
   * path of minimum weight from the start to the end vertex. If there is no
   * such walk, returns null.
   *
   * @param <V> type of vertex value
   * @param <E> type of edge value
   * @param root start vertex
   * @param stop stop condition
   * @return minimum-weight walk from start to end, or null if no such walk
   */
  public static <V, E> OrderedPair<Walk<V, E>, Double> dijkstra(
      WVertex<V, E> root, Predicate<WVertex<V, E>> stop) {
    // First, we check if the root itself satisfies the predicate
    if (stop.test(root)) {
      return new OrderedPair<>(new Walk.Builder<>(root).build(), 0D);
    }

    // Maps visited vertices to weight and last edge of minimum path
    // Start by putting the root in the visited set
    HashMap<WVertex<V, E>, OrderedPair<Double, DijkstraEdge<V, E>>> visited
        = new HashMap<>();
    visited.put(root, new OrderedPair(0, null));
    // Heap of outgoing edges from vertices in the visited set
    // Start by putting the root's edges in the heap
    PriorityQueue<DijkstraEdge<V, E>> edges = new PriorityQueue<>();
    for (WEdge<V, E> we : root.getWEdges()) {
      edges.add(new DijkstraEdge<>(
          we, we.getWEndpoints().not(root), we.getWeight()));
    }

    // Iterate until we've found an appropriate vertex or no more edges
    while (!edges.isEmpty()) {
      // The edge of least weight (starting from a visited vertex)
      DijkstraEdge<V, E> e = edges.poll();
      // If the edge leads to a visited vertex, ignore it
      WVertex<V, E> dest = e.getHead();
      if (visited.keySet().contains(dest)) {
        continue;
      }
      // Otherwise, keep track of the total weight to the head
      double newWeight = e.getTotalWeight();

      // Check if we've found a vertex that satisfies the predicate
      if (stop.test(dest)) {
        // Get the walk from the vertex back to the root
        Walk.Builder builder = new Walk.Builder(dest);
        // While there are still edges in the walk
        while (e != null) {
          // Follow the edge backward to its tail vertex
          builder.addEdgeVertex(e, e.getTail());
          // Then check the edge that lead to the tail vertex
          e = visited.get(e.getTail()).second();
        }
        // Finally, reverse the walk and return the distance
        return new OrderedPair<>(builder.reverse().build(), newWeight);
      }

      // Otherwise, we can grow our visited set
      visited.put(dest, new OrderedPair(newWeight, e));
      // And add the outgoing edges from the new vertex to the heap
      for (WEdge<V, E> newEdge : dest.getWEdges()) {
        edges.add(new DijkstraEdge(
            newEdge, newEdge.getWEndpoints().not(dest),
            newWeight + newEdge.getWeight()));
      }
    }

    // No more edges to check, but we haven't found what we're looking for
    return null;
  }

  /**
   * A weighted edge that also stores the total weight of its head. Used in
   * Dijkstra's algorithm; the total weight of the head is the minimum weight
   * from the root to the tail, plus the weight of the edge. Can be compared to
   * another Dijkstra edge by total weight.
   *
   * <p>
   * Note that Dijkstra's algorithm doesn't have to be run on a directed graph;
   * objects of this class just represent temporary directed edges pointing away
   * from the root.
   *
   * @param <V> type of vertex value
   * @param <E> type of edge value
   */
  private static class DijkstraEdge<V, E> extends ImmutableWEdge<V, E>
      implements Comparable<DijkstraEdge<V, E>> {

    /**
     * Tail vertex.
     */
    private final WVertex<V, E> tail;

    /**
     * Head vertex.
     */
    private final WVertex<V, E> head;

    /**
     * Total weight of head.
     */
    private final double totalWeight;

    /**
     * New Dijkstra edge from the weighted edge, with the given vertex
     * designated the head and assigned the total weight.
     *
     * @param tail tail
     * @param head head
     * @param totalWeight total weight of head
     */
    DijkstraEdge(WEdge<V, E> we, WVertex<V, E> head, double totalWeight) {
      super(we.getValue(), we.getWEndpoints(), we.getWeight());
      this.tail = getWEndpoints().not(head);
      this.head = head;
      this.totalWeight = totalWeight;
    }

    /**
     * Compares to another Dijkstra edge by total weight.
     *
     * @param other other Dijkstra edge
     * @return comparison of total weights
     */
    @Override
    public int compareTo(DijkstraEdge other) {
      double diff = this.totalWeight - other.totalWeight;
      if (diff < 0) {
        return -1;
      }
      if (diff == 0) {
        return 0;
      }
      return 1;
    }

    /**
     * @return total weight of head
     */
    double getTotalWeight() {
      return totalWeight;
    }

    /**
     * @return tail
     */
    WVertex<V, E> getTail() {
      return tail;
    }

    /**
     * @return head
     */
    WVertex<V, E> getHead() {
      return head;
    }
  }

  /**
   * Runs Dijkstra's algorithm from the root vertex until a vertex that
   * satisfies the predicate is reached, using the A* optimization. The function
   * argument is the heuristic used for A*.
   *
   * @param <V> type of vertex value
   * @param <E> type of edge value
   * @param <T> vertex type
   * @param root start vertex
   * @param stop stop condition
   * @param heuristic heuristic on vertices
   * @return minimum-weight walk from start to end, or null if no such walk
   */
  public static <V, E, T extends WVertex<V, E>> OrderedPair<Walk<V, E>, Double>
      dijkstraAStar(T root, Predicate<T> stop,
          ToDoubleFunction<? super T> heuristic) {
    // Same idea as dijkstra, but include the heuristic
    if (stop.test(root)) {
      return new OrderedPair<>(new Walk.Builder<>(root).build(), 0D);
    }
    HashMap<T, OrderedPair<Double, DijkstraEdgeAStar<V, E, T>>> visited
        = new HashMap<>();
    visited.put(root, new OrderedPair(0, null));
    // Here, the edges are sorted by total weight including heuristic
    PriorityQueue<DijkstraEdgeAStar<V, E, T>> edges = new PriorityQueue<>();
    for (WEdge<V, E> we : root.getWEdges()) {
      edges.add(new DijkstraEdgeAStar<>(
          we, (T) we.getWEndpoints().not(root), we.getWeight(), heuristic));
    }

    // Again, keep checking the outgoing edge of least weight
    while (!edges.isEmpty()) {
      DijkstraEdgeAStar<V, E, T> e = edges.poll();
      T dest = e.getHead();
      if (visited.keySet().contains(dest)) {
        continue;
      }
      double newWeight = e.getTotalWeight();
      if (stop.test(dest)) {
        Walk.Builder builder = new Walk.Builder(dest);
        while (e != null) {
          builder.addEdgeVertex(e, e.getTail());
          e = visited.get(e.getTail()).second();
        }
        return new OrderedPair<>(builder.reverse().build(), newWeight);
      }
      visited.put(dest, new OrderedPair(newWeight, e));
      for (WEdge<V, E> newEdge : dest.getWEdges()) {
        edges.add(new DijkstraEdgeAStar(
            newEdge, newEdge.getWEndpoints().not(dest),
            newWeight + newEdge.getWeight(), heuristic));
      }
    }

    // Return null if the start and end are disconnected
    return null;
  }

  /**
   * Dijkstra edge for the A* optimization.
   *
   * @param <V> type of vertex value
   * @param <E> type of edge value
   */
  private static class DijkstraEdgeAStar<V, E, T extends WVertex<V, E>>
      extends ImmutableWEdge<V, E>
      implements Comparable<DijkstraEdgeAStar<V, E, T>> {

    /**
     * Tail vertex.
     */
    private final T tail;

    /**
     * Head vertex.
     */
    private final T head;

    /**
     * Total weight of head.
     */
    private final double totalWeight;

    /**
     * Total weight, including the addition from the heuristic.
     */
    private final double heuristicWeight;

    /**
     * New edge from the given edge, head vertex, total weight, and heuristic.
     *
     * @param tail tail
     * @param head head
     * @param totalWeight total weight of head
     * @param heuristic heuristic
     * @param heuristicWeight total weight including heuristic
     */
    DijkstraEdgeAStar(WEdge<V, E> we, T head, double totalWeight,
        ToDoubleFunction<? super T> heuristic) {
      super(we.getValue(), we.getWEndpoints(), we.getWeight());
      this.tail = (T) getWEndpoints().not(head);
      this.head = head;
      this.totalWeight = totalWeight;
      this.heuristicWeight = totalWeight + heuristic.applyAsDouble(head);
    }

    /**
     * Compares to another Dijkstra edge by heuristic weight.
     *
     * @param other other Dijkstra edge
     * @return comparison of total weights
     */
    @Override
    public int compareTo(DijkstraEdgeAStar other) {
      return (int) Math.signum(this.heuristicWeight - other.heuristicWeight);
    }

    /**
     * @return total weight of head
     */
    double getTotalWeight() {
      return totalWeight;
    }

    /**
     * @return total weight plus heuristic
     */
    double getHeuristicWeight() {
      return heuristicWeight;
    }

    /**
     * @return tail
     */
    T getTail() {
      return tail;
    }

    /**
     * @return head
     */
    T getHead() {
      return head;
    }
  }
}
