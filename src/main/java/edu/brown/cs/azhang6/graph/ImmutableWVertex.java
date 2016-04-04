package edu.brown.cs.azhang6.graph;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of immutable weighted graph vertex.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public class ImmutableWVertex<V, E>
    extends ImmutableVertex<V, E> implements WVertex<V, E> {

  /**
   * New immutable weighted graph vertex with optional value and list of
   * incident edges.
   *
   * @param value value
   * @param edges incident edges
   */
  public ImmutableWVertex(
      Optional<V> value, List<? extends Edge<V, E>> edges) {
    super(value, edges);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<? extends WEdge<V, E>> getWEdges() {
    return Collections.unmodifiableList(
        (List<? extends WEdge<V, E>>) edges);
  }
}
