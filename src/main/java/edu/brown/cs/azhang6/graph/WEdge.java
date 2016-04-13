package edu.brown.cs.azhang6.graph;

import edu.brown.cs.azhang6.pair.UnorderedPair;

/**
 * Edge of a weighted graph. Has a numerical weight; unless otherwise
 * constrained, weight can be any real number.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public interface WEdge<V, E> extends Edge<V, E> {

  /**
   * @return weight
   */
  double getWeight();

  /**
   * @param weight new weight
   */
  default void setWeight(double weight) {
    throw new UnsupportedOperationException("setWeight() not implemented");
  }

  /**
   * @return unordered pair of endpoints as weighted graph vertices
   */
  default UnorderedPair<? extends WVertex<V, E>> getWEndpoints() {
    return new UnorderedPair<>(
        (WVertex<V, E>) getEndpoints().s(), (WVertex<V, E>) getEndpoints().t());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default UnorderedPair<? extends Vertex<V, E>> getEndpoints() {
    return getWEndpoints();
  }
}
