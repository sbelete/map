package edu.brown.cs.azhang6.graph;

import edu.brown.cs.azhang6.pair.UnorderedPair;

import java.util.Optional;

/**
 * Default implementation of immutable edge.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public class ImmutableEdge<V, E> implements Edge<V, E> {

  /**
   * Optional value.
   */
  protected Optional<E> value;

  /**
   * Endpoints.
   */
  protected UnorderedPair<? extends Vertex<V, E>> endpoints;

  /**
   * New immutable edge with optional value and unordered pair of endpoints.
   *
   * @param value value
   * @param endpoints endpoints
   */
  public ImmutableEdge(Optional<E> value,
    UnorderedPair<? extends Vertex<V, E>> endpoints) {
    this.value = value;
    this.endpoints = endpoints;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<E> getValue() {
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UnorderedPair<? extends Vertex<V, E>> getEndpoints() {
    return endpoints;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format(
      "[value=%s: %s <-> %s]", value, endpoints.s(), endpoints.t());
  }
}
