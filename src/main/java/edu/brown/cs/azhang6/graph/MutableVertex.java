package edu.brown.cs.azhang6.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of mutable vertex.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public class MutableVertex<V, E> extends ImmutableVertex<V, E> {

  /**
   * New mutable vertex with value and list of incident edges.
   *
   * @param value optional value
   * @param edges list of incident edges
   */
  public MutableVertex(
      Optional<V> value, List<? extends ImmutableEdge<V, E>> edges) {
    super(value, edges);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setValue(Optional<V> value) {
    this.value = value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setEdges(List<? extends Edge<V, E>> edges) {
    this.edges = new ArrayList<>(edges);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addEdges(Edge<V, E>... edges) {
    List<Edge<V, E>> currentEdges = (List<Edge<V, E>>) this.edges;
    currentEdges.addAll(Arrays.asList(edges));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeEdges(Edge<V, E>... edges) {
    this.edges.removeAll(Arrays.asList(edges));
  }
}
