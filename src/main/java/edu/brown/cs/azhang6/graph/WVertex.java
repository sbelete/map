package edu.brown.cs.azhang6.graph;

import java.util.List;

/**
 * Vertex of a weighted graph.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public interface WVertex<V, E> extends Vertex<V, E> {

  /**
   * @return list of outgoing weighted edges
   */
  List<? extends WEdge<V, E>> getWEdges();

  /**
   * @param edges new list of outgoing weighted edges
   */
  default void setWEdges(List<? extends WEdge<V, E>> edges) {
    throw new UnsupportedOperationException("setWEdges() not implemented");
  }

  /**
   * Adds weighted edges.
   *
   * @param edges edges
   */
  default void addWEdges(WEdge<V, E>... edges) {
    throw new UnsupportedOperationException("addWEdges() not implemented");
  }

  /**
   * Removes weighted edges.
   *
   * @param edges edges
   */
  default void removeWEdges(WEdge<V, E>... edges) {
    throw new UnsupportedOperationException("removeWEdges() not implemented");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default List<? extends Edge<V, E>> getEdges() {
    return getWEdges();
  }
}
