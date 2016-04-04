package edu.brown.cs.azhang6.graphs;

import edu.brown.cs.azhang6.graph.Edge;
import edu.brown.cs.azhang6.graph.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A walk in a graph. A walk is an alternating list of vertices and edges,
 * starting and ending with vertices, such that the endpoints of an edge are the
 * vertices before and after it. Objects of this class are immutable once
 * constructed, so this class comes with a builder: {@link Builder}.
 *
 * @author aaronzhang
 * @param <V> type of vertex value
 * @param <E> type of edge value
 */
public class Walk<V, E> {

  /**
   * List of vertices in order they appear.
   */
  private final List<Vertex<V, E>> vertices;

  /**
   * List of edges in order they appear.
   */
  private final List<Edge<V, E>> edges;

  /**
   * Walk builder.
   *
   * @param <V> type of vertex value
   * @param <E> type of edge value
   */
  public static class Builder<V, E> {

    /**
     * Vertices in walk.
     */
    private final List<Vertex<V, E>> vertices = new ArrayList<>();

    /**
     * Edges in walk.
     */
    private final List<Edge<V, E>> edges = new ArrayList<>();

    /**
     * New walk builder starting at given vertex.
     *
     * @param start start vertex
     */
    public Builder(Vertex<V, E> start) {
      vertices.add(start);
    }

    /**
     * Appends vertex to list of vertices in walk.
     *
     * @param v vertex
     * @return this builder
     */
    public Builder<V, E> addVertex(Vertex<V, E> v) {
      vertices.add(v);
      return this;
    }

    /**
     * Appends edge to list of edges in walk.
     *
     * @param e edge
     * @return this builder
     */
    public Builder<V, E> addEdge(Edge<V, E> e) {
      edges.add(e);
      return this;
    }

    /**
     * Appends edge and vertex to walk.
     *
     * @param e edge
     * @param v vertex
     * @return this builder
     */
    public Builder<V, E> addEdgeVertex(Edge<V, E> e, Vertex<V, E> v) {
      return this.addEdge(e).addVertex(v);
    }

    /**
     * Reverses vertex and edge lists.
     *
     * @return this builder
     */
    public Builder<V, E> reverse() {
      Collections.reverse(vertices);
      Collections.reverse(edges);
      return this;
    }

    /**
     * Builds the walk, if the list of vertices and edges is valid. A walk could
     * be invalid if, for example, the endpoints of the edges don't correspond
     * to the adjacent vertices.
     *
     * @return walk
     * @throws IllegalStateException if walk invalid
     */
    public Walk<V, E> build() {
      // Must have exactly one more vertex than edge
      if (vertices.size() != edges.size() + 1) {
        throw new IllegalStateException("walk must be an "
            + "alternating list of vertices and edges");
      }
      // Endpoints of edges must match adjacent vertices
      for (int i = 0; i < edges.size(); i++) {
        Edge<V, E> e = edges.get(i);
        if (!(e.getEndpoints().s().equals(vertices.get(i))
            && e.getEndpoints().t().equals(vertices.get(i + 1))
            || e.getEndpoints().t().equals(vertices.get(i))
            && e.getEndpoints().s().equals(vertices.get(i + 1)))) {
          throw new IllegalStateException(String.format("edge %d "
              + "must have endpoints vertex %d and vertex %d",
              i, i, i + 1));
        }
      }
      return new Walk<>(vertices, edges);
    }
  }

  /**
   * Should be instantiated by builder.
   *
   * @param vertices list of vertices
   * @param edges list of edges
   */
  protected Walk(List<Vertex<V, E>> vertices, List<Edge<V, E>> edges) {
    this.vertices = vertices;
    this.edges = edges;
  }

  /**
   * @return list of vertices in order of traversal
   */
  public List<Vertex<V, E>> getVertices() {
    return vertices;
  }

  /**
   * @return list of edges in order of traversal
   */
  public List<Edge<V, E>> getEdges() {
    return edges;
  }

  /**
   * @return string representation
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(String.format(
        "[Walk:%n%s", vertices.get(0)));
    for (int i = 1; i < vertices.size(); i++) {
      sb.append(edges.get(i - 1)).append(vertices.get(i));
    }
    return sb.append("]").toString();
  }
}
