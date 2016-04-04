package edu.brown.cs.azhang6.graph;

import java.util.Optional;

/**
 * Default implementation of mutable digraph edge.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public class MutableDEdge<V, E> extends ImmutableDEdge<V, E> {

  /**
   * New immutable digraph edge with optional value, tail, and head.
   *
   * @param value value
   * @param tail tail
   * @param head head
   */
  public MutableDEdge(
      Optional<E> value, DVertex<V, E> tail, DVertex<V, E> head) {
    super(value, tail, head);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTail(DVertex<V, E> tail) {
    this.tail = tail;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setHead(DVertex<V, E> head) {
    this.head = head;
  }
}
