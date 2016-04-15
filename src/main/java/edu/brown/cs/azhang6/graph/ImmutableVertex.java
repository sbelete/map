package edu.brown.cs.azhang6.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of immutable vertex.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public class ImmutableVertex<V, E> implements Vertex<V, E> {

  /**
   * Optional value.
   */
  protected Optional<V> value;

  /**
   * List of incident edges.
   */
  protected List<? extends Edge<V, E>> edges;

  /**
   * New immutable vertex with optional value and list of incident edges.
   *
   * @param value value
   * @param edges incident edges
   */
  public ImmutableVertex(
    Optional<V> value, List<? extends Edge<V, E>> edges) {
    this.value = value;
    this.edges = new ArrayList<>(edges);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<V> getValue() {
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<? extends Edge<V, E>> getEdges() {
    return Collections.unmodifiableList(edges);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format("(value=%s)", value);
  }
}
