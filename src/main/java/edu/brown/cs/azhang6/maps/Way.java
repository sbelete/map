package edu.brown.cs.azhang6.maps;

import java.util.HashMap;
import java.util.Objects;

/**
 * A way has an ID, start and end nodes, and possibly name and type.
 *
 * @author aaronzhang
 */
public class Way {

    /**
     * Cache of ways. Map from ID to way.
     */
    private static HashMap<String, Way> cache = new HashMap<>();

    /**
     * ID.
     */
    protected final String id;

    /**
     * ID of start node.
     */
    protected String start;

    /**
     * ID of end node.
     */
    protected String end;

    /**
     * Name.
     */
    protected String name;

    /**
     * Type.
     */
    protected String type;

    /**
     * New way with ID, start, end, name, and type.
     *
     * @param id id
     * @param start start
     * @param end end
     * @param name name
     * @param type type
     */
    public Way(String id, String start, String end, String name, String type) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.name = name;
        this.type = type;
    }

    /**
     * New way with given ID.
     *
     * @param id id
     */
    public Way(String id) {
        this(id, null, null, null, null);
    }

    /**
     * Returns a way with the given ID. If a way in the cache has the given ID,
     * it will be returned.
     *
     * @param id id
     * @return way with id
     */
    public static Way of(String id) {
        return cache.computeIfAbsent(id, i -> new WayProxy(i));
    }

    /**
     * Caches the way.
     *
     * @param way way
     */
    public static void cache(Way way) {
        cache.put(way.id, way);
    }

    /**
     * Whether the cache has a way with the given ID.
     *
     * @param id id
     * @return whether way with id is in cache
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
     * Gets length of way.
     * 
     * @return length
     */
    public double length() {
        return Node.of(getStart()).distanceTo(Node.of(getEnd()));
    }

    /**
     * Compares two ways by ID.
     *
     * @param o other object
     * @return equality between this way and other object
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Way)) {
            return false;
        }
        Way other = (Way) o;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[Way: id=%s, start=%s, end=%s, name=%s, type=%s]",
            id, start, end, name, type);
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the start
     */
    public String getStart() {
        return start;
    }

    /**
     * @return the end
     */
    public String getEnd() {
        return end;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
}
