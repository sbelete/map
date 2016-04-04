package edu.brown.cs.azhang6.graph;

import edu.brown.cs.azhang6.pair.UnorderedPair;

import java.util.Optional;

/**
 * Edge of a graph. An edge has an optional value and two endpoints.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public interface Edge<V, E> {

  /**
   * @return optional value
   */
  Optional<E> getValue();

  /**
   * @param value new value
   */
  default void setValue(Optional<E> value) {
    throw new UnsupportedOperationException("setValue() not implemented");
  }

  /**
   * @return unordered pair of endpoints
   */
  UnorderedPair<? extends Vertex<V, E>> getEndpoints();

  /**
   * @param endpoints new unordered pair of endpoints
   */
  default void setEndpoints(UnorderedPair<? extends Vertex<V, E>> endpoints) {
    throw new UnsupportedOperationException(
        "setEndpoints() not implemented");
  }
}
