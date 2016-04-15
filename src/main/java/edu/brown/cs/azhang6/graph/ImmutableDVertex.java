package edu.brown.cs.azhang6.graph;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of immutable digraph vertex.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public class ImmutableDVertex<V, E>
  extends ImmutableVertex<V, E> implements DVertex<V, E> {

  /**
   * New immutable digraph vertex with optional value and list of out-edges.
   *
   * @param value value
   * @param edges out-edges
   */
  public ImmutableDVertex(
    Optional<V> value, List<? extends DEdge<V, E>> edges) {
    super(value, edges);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<? extends DEdge<V, E>> getDEdges() {
    return Collections.unmodifiableList(
      (List<? extends DEdge<V, E>>) edges);
  }
}
