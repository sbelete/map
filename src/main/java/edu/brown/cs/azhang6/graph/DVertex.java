package edu.brown.cs.azhang6.graph;

import java.util.List;

/**
 * Vertex of a digraph. Has a list of edges to successors.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public interface DVertex<V, E> extends Vertex<V, E> {

  /**
   * @return list of outgoing digraph edges
   */
  List<? extends DEdge<V, E>> getDEdges();

  /**
   * @param edges new list of outgoing digraph edges
   */
  default void setDEdges(List<? extends DEdge<V, E>> edges) {
    throw new UnsupportedOperationException("setDEdges() not implemented");
  }

  /**
   * Adds digraph edges.
   *
   * @param edges edges
   */
  default void addDEdges(DEdge<V, E>... edges) {
    throw new UnsupportedOperationException("addDEdges() not implemented");
  }

  /**
   * Removes digraph edges.
   *
   * @param edges edges
   */
  default void removeDEdges(DEdge<V, E>... edges) {
    throw new UnsupportedOperationException("removeDEdges() not implemented");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default List<? extends Edge<V, E>> getEdges() {
    return getDEdges();
  }
}
