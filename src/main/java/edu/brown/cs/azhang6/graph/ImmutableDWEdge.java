package edu.brown.cs.azhang6.graph;

import java.util.Optional;

/**
 * Default implementation of immutable weighted digraph edge.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public class ImmutableDWEdge<V, E>
    extends ImmutableDEdge<V, E> implements DWEdge<V, E> {

  /**
   * Weight.
   */
  protected double weight;

  /**
   * New immutable weighted digraph edge with optional value, tail, head, and
   * weight.
   *
   * @param value value
   * @param tail tail
   * @param head head
   * @param weight weight
   */
  public ImmutableDWEdge(Optional<E> value,
      DWVertex<V, E> tail, DWVertex<V, E> head, double weight) {
    super(value, tail, head);
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
    return String.format("[value=%s, weight=%f: %s -> %s]",
        value, weight, tail, head);
  }
}
