package edu.brown.cs.azhang6.graph;

import java.util.List;

/**
 * Vertex of a weighted digraph.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public interface DWVertex<V, E> extends DVertex<V, E>, WVertex<V, E> {

  /**
   * @return list of outgoing weighted digraph edges
   */
  List<? extends DWEdge<V, E>> getDWEdges();

  /**
   * @param edges new list of outgoing weighted digraph edges
   */
  default void setDWEdges(List<? extends DWEdge<V, E>> edges) {
    throw new UnsupportedOperationException("setDWEdges() not implemented");
  }

  /**
   * Adds weighted digraph edges.
   *
   * @param edges edges
   */
  default void addDWEdges(DWEdge<V, E>... edges) {
    throw new UnsupportedOperationException("addDWEdges() not implemented");
  }

  /**
   * Removes weighted digraph edges.
   *
   * @param edges edges
   */
  default void removeDWEdges(DWEdge<V, E>... edges) {
    throw new UnsupportedOperationException("removeDWEdges() not implemented");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default List<? extends DEdge<V, E>> getDEdges() {
    return getDWEdges();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default List<? extends WEdge<V, E>> getWEdges() {
    return getDWEdges();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default List<? extends Edge<V, E>> getEdges() {
    return getDWEdges();
  }
}
