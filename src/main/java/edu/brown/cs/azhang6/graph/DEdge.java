package edu.brown.cs.azhang6.graph;

import edu.brown.cs.azhang6.pair.UnorderedPair;

/**
 * Edge of a digraph. Has a tail (source) and head (destination) vertex.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public interface DEdge<V, E> extends Edge<V, E> {

  /**
   * @return tail digraph vertex
   */
  DVertex<V, E> getTail();

  /**
   * @param tail new tail digraph vertex
   */
  default void setTail(DVertex<V, E> tail) {
    throw new UnsupportedOperationException("setTail() not implemented");
  }

  /**
   * @return head digraph vertex
   */
  DVertex<V, E> getHead();

  /**
   * @param head new head digraph vertex
   */
  default void setHead(DVertex<V, E> head) {
    throw new UnsupportedOperationException("setHead() not implemented");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default UnorderedPair<? extends Vertex<V, E>> getEndpoints() {
    return new UnorderedPair<>(getTail(), getHead());
  }
}
