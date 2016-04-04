package edu.brown.cs.azhang6.graph;

import edu.brown.cs.azhang6.pair.UnorderedPair;
import java.util.Optional;

/**
 * Default implementation of mutable weighted edge.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public class MutableWEdge<V, E> extends ImmutableWEdge<V, E> {

  /**
   * New mutable weighted edge with optional value, unordered pair of endpoints,
   * and weight.
   *
   * @param value value
   * @param endpoints endpoints
   * @param weight weight
   */
  public MutableWEdge(Optional<E> value,
      UnorderedPair<? extends Vertex<V, E>> endpoints, double weight) {
    super(value, endpoints, weight);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setWeight(double weight) {
    this.weight = weight;
  }
}
