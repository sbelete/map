package edu.brown.cs.azhang6.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of mutable digraph vertex.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public class MutableDVertex<V, E> extends ImmutableDVertex<V, E> {

  /**
   * New mutable digraph vertex with optional value and list of out-edges.
   *
   * @param value value
   * @param edges out-edges
   */
  public MutableDVertex(
      Optional<V> value, List<? extends DEdge<V, E>> edges) {
    super(value, edges);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDEdges(List<? extends DEdge<V, E>> edges) {
    this.edges = new ArrayList<>(edges);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addDEdges(DEdge<V, E>... edges) {
    List<DEdge<V, E>> currentEdges = (List<DEdge<V, E>>) this.edges;
    currentEdges.addAll(Arrays.asList(edges));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeDEdges(DEdge<V, E>... edges) {
    this.edges.removeAll(Arrays.asList(edges));
  }
}
