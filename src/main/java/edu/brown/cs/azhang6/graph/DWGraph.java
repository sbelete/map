package edu.brown.cs.azhang6.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.TreeMultiset;

/**
 * Test graph.
 *
 * @author aaronzhang
 * @param <V> vertex value type
 * @param <E> edge value type
 * @param <T> vertex name type
 */
public class DWGraph<V, E, T> {

  /**
   * Map from name to vertex. The name does not have to equal the value.
   */
  private final Map<T, DWVertex<V, E>> vertices = new HashMap<>();

  /**
   * Strings representing edges. Used for {@link Graph#toString()}.
   */
  private final TreeMultiset<String> edgeStrings = TreeMultiset.create();

  /**
   * Graph builder.
   *
   * @param <V> type of vertex value
   * @param <E> type of edge value
   * @param <T> type of name
   */
  public static class Builder<V, E, T> {

    /**
     * The graph being built.
     */
    private final DWGraph<V, E, T> graph = new DWGraph();

    /**
     * New builder with no vertices or edges.
     */
    public Builder() {

    }

    /**
     * Adds vertex with given name and value. Overwrites any vertices that
     * already have the given name. The newly added vertex has no incident
     * edges.
     *
     * @param name name
     * @param value value
     * @return this builder
     */
    public Builder<V, E, T> addVertex(T name, Optional<V> value) {
      graph.vertices.put(name, new MutableDWVertex<>(
          value, Collections.emptyList()));
      return this;
    }

    /**
     * Adds vertex with given name. Overwrites any vertices that already have
     * the given name. The newly added vertex has no value or incident edges.
     *
     * @param name name
     * @return this builder
     */
    public Builder<V, E, T> addVertex(T name) {
      return addVertex(name, Optional.empty());
    }

    /**
     * Adds vertices with the given names. Overwrites any vertices that already
     * have the given names. The newly added vertices have no value or incident
     * edges.
     *
     * @param names one or more names
     * @return this builder
     */
    public Builder<V, E, T> addVertices(T... names) {
      for (T name : names) {
        graph.vertices.put(name, new MutableDWVertex<>(
            Optional.empty(), Collections.emptyList()));
      }
      return this;
    }

    /**
     * Adds an edge with the given value between the vertices corresponding to
     * the two names. The vertices must have been added beforehand.
     *
     * @param u vertex name
     * @param v vertex name
     * @param value edge value
     * @param weight weight
     * @return this builder
     */
    public Builder<V, E, T> addEdge(
      T u, T v, Optional<E> value, double weight) {
      // The names must be in the graph
      if (!graph.containsName(u)) {
        throw new IllegalArgumentException(String.format(
            "no vertex with name: %s", u));
      }
      if (!graph.containsName(v)) {
        throw new IllegalArgumentException(String.format(
            "no vertex with name: %s", v));
      }

      // Make the new edge and add it to the graph
      DWVertex<V, E> uVertex = graph.vertexByName(u);
      DWVertex<V, E> vVertex = graph.vertexByName(v);
      MutableDWEdge<V, E> newEdge = new MutableDWEdge<>(value,
          uVertex, vVertex, weight);
      uVertex.addDWEdges(newEdge);

      // The string representation of the edge
      graph.edgeStrings.add(String.format("%s->%s (%f)", u, v, weight));

      return this;
    }

    /**
     * Adds an edge between the vertices corresponding to the two names. The
     * vertices must have been added beforehand. The newly added edge has no
     * value.
     *
     * @param u vertex name
     * @param v vertex name
     * @param weight weight
     * @return this builder
     */
    public Builder<V, E, T> addEdge(T u, T v, double weight) {
      return addEdge(u, v, Optional.empty(), weight);
    }

    /**
     * Build the graph.
     *
     * @return graph
     */
    public DWGraph build() {
      return graph;
    }
  }

  /**
   * Should be constructed by builder.
   */
  protected DWGraph() {

  }

  /**
   * @return map of name to vertex
   */
  public Map<T, DWVertex<V, E>> vertices() {
    return Collections.unmodifiableMap(vertices);
  }

  /**
   * Gets vertex with given name, or null if no such vertex.
   *
   * @param name name
   * @return vertex with given name
   */
  public DWVertex<V, E> vertexByName(T name) {
    return vertices.get(name);
  }

  /**
   * Whether a vertex with the given name is in the graph.
   *
   * @param name name
   * @return whether graph contains vertex with name
   */
  public boolean containsName(T name) {
    return vertices.containsKey(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format("[graph with vertices %s and edges %s]",
        vertices, edgeStrings);
  }
}
