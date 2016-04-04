package edu.brown.cs.azhang6.graph;

import java.util.Optional;

/**
 * Default implementation of mutable weighted digraph edge.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public class MutableDWEdge<V, E> extends ImmutableDWEdge<V, E> {

  /**
   * New immutable weighted digraph edge with optional value, tail, head, and
   * weight.
   *
   * @param value value
   * @param tail tail
   * @param head head
   * @param weight weight
   */
  public MutableDWEdge(Optional<E> value,
      DWVertex<V, E> tail, DWVertex<V, E> head, double weight) {
    super(value, tail, head, weight);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setWTail(DWVertex<V, E> tail) {
    this.tail = tail;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setWHead(DWVertex<V, E> head) {
    this.head = head;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setWeight(double weight) {
    this.weight = weight;
  }
}
