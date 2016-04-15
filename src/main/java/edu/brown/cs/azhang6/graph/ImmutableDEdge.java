package edu.brown.cs.azhang6.graph;

import edu.brown.cs.azhang6.pair.UnorderedPair;

import java.util.Optional;

/**
 * Default implementation of immutable digraph edge.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public class ImmutableDEdge<V, E>
  extends ImmutableEdge<V, E> implements DEdge<V, E> {

  /**
   * Tail vertex.
   */
  protected DVertex<V, E> tail;

  /**
   * Head vertex.
   */
  protected DVertex<V, E> head;

  /**
   * New immutable digraph edge with optional value, tail, and head.
   *
   * @param value value
   * @param tail tail
   * @param head head
   */
  public ImmutableDEdge(
    Optional<E> value, DVertex<V, E> tail, DVertex<V, E> head) {
    super(value, new UnorderedPair<>(tail, head));
    this.tail = tail;
    this.head = head;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DVertex<V, E> getTail() {
    return tail;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DVertex<V, E> getHead() {
    return head;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format("[value=%s: %s -> %s]", value, tail, head);
  }
}
