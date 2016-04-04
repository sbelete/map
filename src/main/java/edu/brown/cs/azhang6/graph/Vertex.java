package edu.brown.cs.azhang6.graph;

import java.util.List;
import java.util.Optional;

/**
 * Vertex of a graph. A vertex has an optional value and a list of outgoing
 * edges; unless otherwise constrained multiple edges and loops are allowed.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public interface Vertex<V, E> {

  /**
   * @return optional value
   */
  Optional<V> getValue();

  /**
   * @param value new value
   */
  default void setValue(Optional<V> value) {
    throw new UnsupportedOperationException("setValue() not implemented");
  }

  /**
   * @return unmodifiable view of list of outgoing edges
   */
  List<? extends Edge<V, E>> getEdges();

  /**
   * @param edges new list of outgoing edges
   */
  default void setEdges(List<? extends Edge<V, E>> edges) {
    throw new UnsupportedOperationException("setEdges() not implemented");
  }

  /**
   * Adds edges.
   *
   * @param edges edges
   */
  default void addEdges(Edge<V, E>... edges) {
    throw new UnsupportedOperationException("addEdges() not implemented");
  }

  /**
   * Removes edges.
   *
   * @param edges edges
   */
  default void removeEdges(Edge<V, E>... edges) {
    throw new UnsupportedOperationException("removeEdges() not implemented");
  }
}
