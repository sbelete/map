package edu.brown.cs.azhang6.maps;

import edu.brown.cs.azhang6.dimension.LatLng;
import edu.brown.cs.azhang6.graph.DWEdge;
import edu.brown.cs.azhang6.graph.DWVertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A node represents a point on the map. A node has an ID and a position given
 * by latitude and longitude. In addition, a node has a list of outgoing edges
 * representing ways that start from the node.
 *
 * @author aaronzhang
 */
public class Node extends LatLng implements DWVertex<Node, Way> {

    /**
     * Cache mapping ID to node.
     */
    private static final HashMap<String, Node> cache = new HashMap<>();

    /**
     * ID.
     */
    private final String id;

    /**
     * Outgoing edges. An outgoing edge represents a way starting from this
     * node.
     */
    protected List<DWEdge<Node, Way>> edges = null;

    /**
     * New node with given ID, latitude, longitude, and edges.
     *
     * @param id id
     * @param latitude latitude
     * @param longitude longitude
     * @param edges edges
     */
    public Node(String id, double latitude, double longitude,
        List<? extends DWEdge<Node, Way>> edges) {
        super(latitude, longitude);
        this.id = id;
        if (edges != null) {
            this.edges = new ArrayList<>(edges);
        }
    }

    /**
     * New node with given ID, latitude, and longitude. List of edges will be
     * null.
     *
     * @param id id
     * @param latitude latitude
     * @param longitude longitude
     */
    public Node(String id, double latitude, double longitude) {
        this(id, latitude, longitude, null);
    }

    /**
     * Returns a node with the given ID. If a cached node has the given ID, the
     * cached node will be returned.
     *
     * @param id id
     * @return node with id
     */
    public static Node of(String id) {
        return cache.computeIfAbsent(id, i -> new NodeProxy(i));
    }

    /**
     * Caches the node. A cached node can be retrieved by ID using
     * {@link Node#of(String)}. Overrides any node previously in the cache with
     * the same ID.
     *
     * @param node node
     */
    public static void cache(Node node) {
        cache.put(node.id, node);
    }

    /**
     * Whether the cache has a node with the given ID.
     *
     * @param id id
     * @return whether node with id is in cache
     */
    public static boolean has(String id) {
        return cache.containsKey(id);
    }

    /**
     * Clears cache.
     */
    public static void clearCache() {
        cache.clear();
    }

    /**
     * Whether the id of this node matches the id of another node.
     *
     * @param o other object
     * @return whether this object equals other object
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Node)) {
            return false;
        }
        Node other = (Node) o;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format(
            "[Node with id=%s, latitude=%f, longitude=%f", id, getLat(), getLng()));
        if (edges != null) {
            sb.append(String.format(",%nedges=%s", edges));
        }
        return sb.append("]").toString();
    }

    /**
     * @return node id
     */
    public String getId() {
        return id;
    }

    @Override
    public List<? extends DWEdge<Node, Way>> getDWEdges() {
        return Collections.unmodifiableList(edges);
    }

    @Override
    public void setDWEdges(List<? extends DWEdge<Node, Way>> edges) {
        this.edges = new ArrayList<>(edges);
    }

    @Override
    public void addDWEdges(DWEdge<Node, Way>... edges) {
        this.edges.addAll(Arrays.asList(edges));
    }

    @Override
    public Optional<Node> getValue() {
        return Optional.of(this);
    }
}
