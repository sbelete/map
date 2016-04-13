package edu.brown.cs.azhang6.graphs;

import edu.brown.cs.azhang6.graph.DWGraph;
import edu.brown.cs.azhang6.pair.OrderedPair;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for graphs.
 *
 * @author aaronzhang
 */
public class GraphsTest {

    /**
     * Some simple cases.
     */
    @Test
    public void testDijkstra() {
        DWGraph<Integer, Integer, Integer> g = new DWGraph.Builder<>()
            .addVertices(1, 2, 3)
            .addEdge(1, 2, 12)
            .addEdge(2, 1, 10)
            .addEdge(3, 2, 9).build();
        OrderedPair<Walk<Integer, Integer>, Double> result
            = Graphs.dijkstra(g.vertexByName(1), v -> v.equals(g.vertexByName(2)));
        assertTrue(result.second() == 12);

        DWGraph<Integer, Integer, Integer> g2 = new DWGraph.Builder<>()
            .addVertices(1, 2, 3)
            .addEdge(1, 2, 14)
            .addEdge(2, 1, 15)
            .addEdge(1, 2, 9)
            .addEdge(1, 3, 4)
            .addEdge(3, 2, 1).build();
        OrderedPair<Walk<Integer, Integer>, Double> result2
            = Graphs.dijkstra(g2.vertexByName(1), v -> v.equals(g2.vertexByName(2)));
        assertTrue(result2.second() == 5);
    }

    /**
     * The above tests for
     * {@link Graphs#dijkstraAStar(WVertex, Predicate, ToDoubleFunction)}.
     */
    @Test
    public void testDijkstraAStar() {
        DWGraph<Integer, Integer, Integer> g = new DWGraph.Builder<>()
            .addVertices(1, 2, 3)
            .addEdge(1, 2, 12)
            .addEdge(2, 1, 10)
            .addEdge(3, 2, 9).build();
        OrderedPair<Walk<Integer, Integer>, Double> result
            = Graphs.dijkstraAStar(g.vertexByName(1), v -> v.equals(g.vertexByName(2)),
                d -> 8);
        assertTrue(result.second() == 12);

        DWGraph<Integer, Integer, Integer> g2 = new DWGraph.Builder<>()
            .addVertices(1, 2, 3)
            .addEdge(1, 2, 14)
            .addEdge(2, 1, 15)
            .addEdge(1, 2, 9)
            .addEdge(1, 3, 4)
            .addEdge(3, 2, 1).build();
        OrderedPair<Walk<Integer, Integer>, Double> result2
            = Graphs.dijkstraAStar(g2.vertexByName(1), v -> v.equals(g2.vertexByName(2)),
                v -> 0);
        assertTrue(result2.second() == 5);
    }

    /**
     * Some edge cases.
     */
    @Test
    public void testDijkstra2() {
        DWGraph<Integer, Integer, Integer> g = new DWGraph.Builder<>()
            .addVertices(1, 2, 3)
            .build();
        OrderedPair<Walk<Integer, Integer>, Double> result
            = Graphs.dijkstra(g.vertexByName(1), v -> v.equals(g.vertexByName(2)));
        assertTrue(result == null);

        DWGraph<Integer, Integer, Integer> g2 = new DWGraph.Builder<>()
            .addVertices(1, 2, 3)
            .build();
        OrderedPair<Walk<Integer, Integer>, Double> result2
            = Graphs.dijkstra(g2.vertexByName(1), v -> v.equals(g2.vertexByName(1)));
        assertTrue(result2.second() == 0);
    }
    
    /**
     * The above tests for the A* version.
     */
    @Test
    public void testDijkstraAStar2() {
        DWGraph<Integer, Integer, Integer> g = new DWGraph.Builder<>()
            .addVertices(1, 2, 3)
            .build();
        OrderedPair<Walk<Integer, Integer>, Double> result
            = Graphs.dijkstraAStar(g.vertexByName(1), v -> v.equals(g.vertexByName(2)),
                v -> 0);
        assertTrue(result == null);

        DWGraph<Integer, Integer, Integer> g2 = new DWGraph.Builder<>()
            .addVertices(1, 2, 3)
            .build();
        OrderedPair<Walk<Integer, Integer>, Double> result2
            = Graphs.dijkstraAStar(g2.vertexByName(1), v -> v.equals(g2.vertexByName(1)),
                v -> 5);
        assertTrue(result2.second() == 0);
    }

    /**
     * Some larger cases. First example from example from
     * https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm.
     */
    @Test
    public void testDijkstra3() {
        DWGraph<Integer, Integer, Integer> g = new DWGraph.Builder<>()
            .addVertices(1, 2, 3, 4, 5, 6)
            .addEdge(1, 6, 14)
            .addEdge(1, 3, 9)
            .addEdge(1, 2, 7)
            .addEdge(2, 1, 7)
            .addEdge(2, 3, 10)
            .addEdge(2, 4, 15)
            .addEdge(3, 1, 9)
            .addEdge(3, 2, 10)
            .addEdge(3, 4, 11)
            .addEdge(3, 6, 2)
            .addEdge(4, 5, 6)
            .addEdge(4, 3, 11)
            .addEdge(4, 2, 15)
            .addEdge(5, 4, 6)
            .addEdge(5, 6, 9)
            .addEdge(6, 5, 9)
            .addEdge(6, 3, 2)
            .addEdge(6, 1, 14).build();
        assertTrue(Graphs.dijkstra(
            g.vertexByName(1), v -> v.equals(g.vertexByName(1))).second() == 0);
        assertTrue(Graphs.dijkstra(
            g.vertexByName(1), v -> v.equals(g.vertexByName(2))).second() == 7);
        assertTrue(Graphs.dijkstra(
            g.vertexByName(1), v -> v.equals(g.vertexByName(3))).second() == 9);
        assertTrue(Graphs.dijkstra(
            g.vertexByName(1), v -> v.equals(g.vertexByName(4))).second() == 20);
        assertTrue(Graphs.dijkstra(
            g.vertexByName(1), v -> v.equals(g.vertexByName(5))).second() == 20);
        assertTrue(Graphs.dijkstra(
            g.vertexByName(1), v -> v.equals(g.vertexByName(6))).second() == 11);
    }
    
    /**
     * The above tests for the A* version.
     */
    @Test
    public void testDijkstraAStar3() {
        DWGraph<Integer, Integer, Integer> g = new DWGraph.Builder<>()
            .addVertices(1, 2, 3, 4, 5, 6)
            .addEdge(1, 6, 14)
            .addEdge(1, 3, 9)
            .addEdge(1, 2, 7)
            .addEdge(2, 1, 7)
            .addEdge(2, 3, 10)
            .addEdge(2, 4, 15)
            .addEdge(3, 1, 9)
            .addEdge(3, 2, 10)
            .addEdge(3, 4, 11)
            .addEdge(3, 6, 2)
            .addEdge(4, 5, 6)
            .addEdge(4, 3, 11)
            .addEdge(4, 2, 15)
            .addEdge(5, 4, 6)
            .addEdge(5, 6, 9)
            .addEdge(6, 5, 9)
            .addEdge(6, 3, 2)
            .addEdge(6, 1, 14).build();
        assertTrue(Graphs.dijkstraAStar(
            g.vertexByName(1), v -> v.equals(g.vertexByName(1)), v -> 2).second() == 0);
        assertTrue(Graphs.dijkstraAStar(
            g.vertexByName(1), v -> v.equals(g.vertexByName(2)), v -> 2).second() == 7);
        assertTrue(Graphs.dijkstraAStar(
            g.vertexByName(1), v -> v.equals(g.vertexByName(3)), v -> 2).second() == 9);
        assertTrue(Graphs.dijkstraAStar(
            g.vertexByName(1), v -> v.equals(g.vertexByName(4)), v -> 2).second() == 20);
        assertTrue(Graphs.dijkstraAStar(
            g.vertexByName(1), v -> v.equals(g.vertexByName(5)), v -> 2).second() == 20);
        assertTrue(Graphs.dijkstraAStar(
            g.vertexByName(1), v -> v.equals(g.vertexByName(6)), v -> 2).second() == 11);
    }
}
