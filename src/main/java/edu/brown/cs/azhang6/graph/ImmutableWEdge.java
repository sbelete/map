package edu.brown.cs.azhang6.graph;

import edu.brown.cs.azhang6.pair.UnorderedPair;

import java.util.Optional;

/**
 * Default implementation of immutable weighted edge.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public class ImmutableWEdge<V, E>
  extends ImmutableEdge<V, E> implements WEdge<V, E> {

  /**
   * Weight.
   */
  protected double weight;

  /**
   * New immutable weighted edge with optional value, unordered pair of
   * endpoints, and weight.
   *
   * @param value value
   * @param endpoints endpoints
   * @param weight weight
   */
  public ImmutableWEdge(Optional<E> value,
    UnorderedPair<? extends Vertex<V, E>> endpoints, double weight) {
    super(value, endpoints);
    this.weight = weight;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getWeight() {
    return weight;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format("[value=%s, weight=%f: %s <-> %s]",
      value, weight, endpoints.s(), endpoints.t());
  }
}
