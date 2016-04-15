package edu.brown.cs.azhang6.graph;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of immutable weighted digraph vertex.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public class ImmutableDWVertex<V, E>
  extends ImmutableVertex<V, E> implements DWVertex<V, E> {

  /**
   * New immutable weighted digraph vertex with optional value and list of
   * out-edges.
   *
   * @param value value
   * @param edges out-edges
   */
  public ImmutableDWVertex(
    Optional<V> value, List<? extends DWEdge<V, E>> edges) {
    super(value, edges);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<? extends DWEdge<V, E>> getDWEdges() {
    return Collections.unmodifiableList(
      (List<? extends DWEdge<V, E>>) edges);
  }
}
