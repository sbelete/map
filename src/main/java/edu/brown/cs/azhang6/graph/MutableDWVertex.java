package edu.brown.cs.azhang6.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of mutable weighted digraph vertex.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public class MutableDWVertex<V, E> extends ImmutableDWVertex<V, E> {

  /**
   * New mutable digraph vertex with optional value and list of out-edges.
   *
   * @param value value
   * @param edges out-edges
   */
  public MutableDWVertex(
      Optional<V> value, List<? extends DWEdge<V, E>> edges) {
    super(value, edges);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDWEdges(List<? extends DWEdge<V, E>> edges) {
    this.edges = new ArrayList<>(edges);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addDWEdges(DWEdge<V, E>... edges) {
    List<DWEdge<V, E>> currentEdges = (List<DWEdge<V, E>>) this.edges;
    currentEdges.addAll(Arrays.asList(edges));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeDWEdges(DWEdge<V, E>... edges) {
    this.edges.removeAll(Arrays.asList(edges));
  }
}
