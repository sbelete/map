package edu.brown.cs.azhang6.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of mutable weighted graph vertex.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public class MutableWVertex<V, E> extends ImmutableWVertex<V, E> {

  /**
   * New mutable weighted graph vertex with optional value, list of incident
   * edges, and weight.
   *
   * @param value value
   * @param edges incident edges
   */
  public MutableWVertex(
      Optional<V> value, List<? extends Edge<V, E>> edges) {
    super(value, edges);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setWEdges(List<? extends WEdge<V, E>> edges) {
    this.edges = new ArrayList<>(edges);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addWEdges(WEdge<V, E>... edges) {
    List<WEdge<V, E>> currentEdges = (List<WEdge<V, E>>) this.edges;
    currentEdges.addAll(Arrays.asList(edges));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeWEdges(WEdge<V, E>... edges) {
    this.edges.removeAll(Arrays.asList(edges));
  }
}
