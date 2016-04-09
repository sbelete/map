package edu.brown.cs.azhang6.maps;

import edu.brown.cs.azhang6.db.Database;
import edu.brown.cs.azhang6.graph.DWEdge;
import edu.brown.cs.azhang6.graph.MutableDWEdge;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Node proxy that queries a database for node information when necessary.
 *
 * @author aaronzhang
 */
class NodeProxy extends Node {

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
        try (PreparedStatement prep = db.getConn().prepareStatement(
            "SELECT * FROM node WHERE id=?;")) {
            prep.setString(1, id);
            return db.query(prep, r -> {
                try {
                    r.next();
                    double[] latLng = new double[2];
                    latLng[1] = r.getDouble(1);
                    latLng[2] = r.getDouble(2);
                    return latLng;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
            try (PreparedStatement prep = db.getConn().prepareStatement(
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
            }
        }
        return internal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends DWEdge<Node, Way>> getDWEdges() {
        return fillEdges().getDWEdges();
    }
}
