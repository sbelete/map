package edu.brown.cs.azhang6.maps;

import edu.brown.cs.azhang6.db.Database;
import edu.brown.cs.azhang6.graph.DWEdge;
import edu.brown.cs.azhang6.graph.MutableDWEdge;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Node proxy that queries a database for node information when necessary.
 *
 * @author aaronzhang
 */
public class NodeProxy extends Node {

    /**
     * Database with node information.
     */
    private static Database db;

    /**
     * Internal node.
     */
    private final Node internal = new Node(getId(), getLat(), getLng());

    /**
     * New node proxy with ID.
     *
     * @param id ID
     */
    NodeProxy(String id) {
        this(id, latLngForId(id));
    }

    /**
     * New node proxy with ID, latitude, and longitude.
     *
     * @param id id
     * @param latLng latitude and longitude
     */
    private NodeProxy(String id, double[] latLng) {
        super(id, latLng[0], latLng[1]);
        Node.cache(this);
    }

    /**
     * Looks up latitude and longitude for ID.
     *
     * @param id id
     * @return latitude and longitude
     */
    private static double[] latLngForId(String id) {
        Connection conn = db.getConnection();
        try (PreparedStatement prep = conn.prepareStatement(
            "SELECT * FROM node WHERE id=?;")) {
            if (Main.done) {
                System.out.println("prep closed 0? " + prep.isClosed());
            }
            prep.setString(1, id);
            return db.query(prep, r -> {
                try {
                    if (Main.done) {
                System.out.println("prep closed 1? " + prep.isClosed());
                        System.out.println("rs closed 1? " + r.isClosed());
            }
                    r.next();
                    double[] latLng = new double[2];
                    latLng[0] = r.getDouble("latitude");
                    latLng[1] = r.getDouble("longitude");
                    return latLng;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            db.returnConnection(conn);
        }
    }
    
    /**
     * Gets the node at the intersection of two ways, or null if no such node.
     * 
     * @param way1 name of first way
     * @param way2 name of second way
     * @return node at intersection
     */
    public static Node atIntersection(String way1, String way2) {
        List<String> way1Ids = WayProxy.idsForName(way1);
        List<String> way2Ids = WayProxy.idsForName(way2);
        Set<String> way1Nodes = new HashSet<>();
        Set<String> way2Nodes = new HashSet<>();
        // Get all the nodes on the two ways
        Connection conn = db.getConnection();
        try (PreparedStatement prep = conn.prepareStatement(
            "SELECT start,end FROM way WHERE id=?;")) {
            for (String way1Id : way1Ids) {
                prep.setString(1, way1Id);
                db.query(prep, rs -> {
                    try {
                        rs.next();
                        way1Nodes.add(rs.getString(1));
                        way1Nodes.add(rs.getString(2));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            for (String way2Id : way2Ids) {
                prep.setString(1, way2Id);
                db.query(prep, rs -> {
                    try {
                        rs.next();
                        way2Nodes.add(rs.getString(1));
                        way2Nodes.add(rs.getString(2));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            // Find the intersection of the two sets
            way1Nodes.retainAll(way2Nodes);
            if (way1Nodes.isEmpty()) {
                return null;
            }
            return Node.of(way1Nodes.iterator().next());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            db.returnConnection(conn);
        }
    }

    /**
     * Uses the provided database for node information.
     *
     * @param db database
     */
    public static void setDB(Database db) {
        NodeProxy.db = db;
    }

    /**
     * Fills edges from database.
     */
    private Node fillEdges() {
        if (internal.edges == null) {
            // Get all ways starting from this node
            Connection conn = db.getConnection();
            try (PreparedStatement prep = conn.prepareStatement(
                "SELECT id FROM way WHERE start=?;")) {
                prep.setString(1, getId());
                List<String> ways = db.query(prep);
                // Add edges corresponding to ways
                internal.setDWEdges(new ArrayList<>());
                ways.forEach(w -> {
                    Way way = Way.of(w);
                    internal.addDWEdges(new MutableDWEdge<>(
                        Optional.of(way), this, Node.of(way.getEnd()), way.length()));
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                db.returnConnection(conn);
            }
        } else {
            // Make sure ways have updated traffic
            for (DWEdge<Node, Way> edge : internal.getDWEdges()) {
                edge.setWeight(edge.getValue().get().length());
            }
        }
        return internal;
    }

    @Override
    public List<? extends DWEdge<Node, Way>> getDWEdges() {
        return fillEdges().getDWEdges();
    }
}
