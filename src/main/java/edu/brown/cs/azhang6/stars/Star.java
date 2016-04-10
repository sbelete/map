package edu.brown.cs.azhang6.stars;

import edu.brown.cs.azhang6.dimension.Dimensional;

import java.util.Objects;

/**
 * A star in three-dimensional space.
 *
 * @author aaronzhang
 */
public class Star implements Dimensional {

    /**
     * Number of dimensions.
     */
    private static final int NUM_DIMENSIONS = 3;

    /**
     * Star ID.
     */
    private final int ID;

    /**
     * Proper name. Can be an empty string, but not {@code null}.
     */
    private final String name;

    /**
     * x, y, and z coordinates of star.
     */
    private final double[] coordinates = new double[NUM_DIMENSIONS];

    /**
     * Constructs a new {@code Star} with the given star ID, proper name, and
     * coordinates. If the proper name is {@code null}, the empty string will be
     * used instead.
     *
     * @param ID star ID
     * @param name proper name, can be empty
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     */
    public Star(int ID, String name, double x, double y, double z) {
        this.ID = ID;
        this.name = name == null ? "" : name;
        coordinates[0] = x;
        coordinates[1] = y;
        coordinates[2] = z;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int numDimensions() {
        return NUM_DIMENSIONS;
    }

    /**
     * Calculates distance to another three-dimensional object.
     *
     * @param other another three-dimensional object
     * @return distance to other other
     * @throws IllegalArgumentException if argument is not three-dimensional
     */
    @Override
    public double distanceTo(Dimensional other) {
        if (this.numDimensions() != other.numDimensions()) {
            throw new IllegalArgumentException(
                "Dimension mismatch in distanceTo()");
        }
        double squaredSum = 0;
        for (int i = 0; i < NUM_DIMENSIONS; i++) {
            squaredSum
                += Math.pow(this.getCoordinate(i) - other.getCoordinate(i), 2);
        }
        return Math.sqrt(squaredSum);
    }

    /**
     * @return star ID
     */
    public int getID() {
        return ID;
    }

    /**
     * @return proper name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the specified coordinate, which can be 0, 1, or 2. The indices
     * corresponding to coordinates: 0: x 1: y 2: z
     *
     * @param coordinate which coordinate to get
     * @return value of the coordinate
     * @throws IllegalArgumentException if coordinate out of range
     */
    @Override
    public double getCoordinate(int coordinate) {
        try {
            return coordinates[coordinate];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                String.format("Coordinate out of range: %d", coordinate));
        }
    }

    /**
     * @return x-coordinate
     */
    public double getX() {
        return getCoordinate(0);
    }

    /**
     * @return y-coordinate
     */
    public double getY() {
        return getCoordinate(1);
    }

    /**
     * @return z-coordinate
     */
    public double getZ() {
        return getCoordinate(2);
    }

    /**
     * @return string representation of this {@code Star}
     */
    @Override
    public String toString() {
        return String.format(
            "Star with ID %d, name %s, and coordinates (%f, %f, %f)",
            ID, name, getX(), getY(), getZ());
    }

    /**
     * Compares this star to another object for equality. If the other object is
     * also a star, the ID, name, and coordinates fields are compared.
     *
     * @param o another object
     * @return whether this star should be considered equal to the other object
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Star)) {
            return false;
        }
        Star other = (Star) o;
        return this.ID == other.ID
            && this.name.equals(other.name)
            && this.getX() == other.getX()
            && this.getY() == other.getY()
            && this.getZ() == other.getZ();
    }

    /**
     * Computes a hash code using the ID, name, and coordinates.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(ID, name, getX(), getY(), getZ());
    }

    @Override
    public Dimensional withCoordinate(int coordinate, double value) {
        switch (coordinate) {
            case 0:
                return new Star(ID, name, value, getY(), getZ());
            case 1:
                return new Star(ID, name, getX(), value, getZ());
            case 2:
                return new Star(ID, name, getX(), getY(), value);
            default:
                throw new IllegalArgumentException("coordinate must be 0, 1, or 2");
        }
    }
}
