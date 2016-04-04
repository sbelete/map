package edu.brown.cs.azhang6.graph;

import edu.brown.cs.azhang6.pair.UnorderedPair;

/**
 * Edge of a weighted digraph.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public interface DWEdge<V, E> extends DEdge<V, E>, WEdge<V, E> {

  /**
   * @return tail vertex as weighted digraph vertex
   */
  default DWVertex<V, E> getWTail() {
    return (DWVertex<V, E>) getTail();
  }

  /**
   * @param tail tail as weighted digraph vertex
   */
  default void setWTail(DWVertex<V, E> tail) {
    throw new UnsupportedOperationException("setWTail() not implemented");
  }

  /**
   * @return head vertex as weighted digraph vertex
   */
  default DWVertex<V, E> getWHead() {
    return (DWVertex<V, E>) getHead();
  }

  /**
   * @param head head as weighted digraph vertex
   */
  default void setWHead(DWVertex<V, E> head) {
    throw new UnsupportedOperationException("setWHead() not implemented");
  }

  /**
   * Gets endpoints. Overriden because both DEdge and WEdge provide defaults.
   *
   * @return {@inheritDoc}
   */
  @Override
  default UnorderedPair<? extends Vertex<V, E>> getEndpoints() {
    return new UnorderedPair<>(getTail(), getHead());
  }
}
