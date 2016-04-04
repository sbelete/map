package edu.brown.cs.azhang6.graph;

import edu.brown.cs.azhang6.pair.UnorderedPair;

import java.util.Optional;

/**
 * Default implementation of mutable edge.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public class MutableEdge<V, E> extends ImmutableEdge<V, E> {

  /**
   * New mutable edge with value and unordered pair of endpoints.
   *
   * @param value value
   * @param endpoints endpoints
   */
  public MutableEdge(Optional<E> value,
      UnorderedPair<? extends Vertex<V, E>> endpoints) {
    super(value, endpoints);
  }

  /**
   * {@inheritDoc}
   */
  public void setValue(Optional<E> value) {
    this.value = value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setEndpoints(UnorderedPair<? extends Vertex<V, E>> endpoints) {
    this.endpoints = endpoints;
  }
}
