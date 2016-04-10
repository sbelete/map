package edu.brown.cs.azhang6.dimension;

import java.util.Arrays;

/**
 * Represents a point in some dimensional space. A simple concrete class that
 * implements the {@link Dimensional} interface.
 *
 * @author aaronzhang
 */
public class Point implements Dimensional {

  /**
   * Coordinates of this point.
   */
  private final double[] coordinates;

  /**
   * String representation of this point.
   */
  private final String strRep;
  
  /**
   * Constructs a new {@code Point} with the given coordinates.
   *
   * @param coordinates coordinates
   */
  public Point(double... coordinates) {
    this.coordinates = coordinates;

    // Save string representation of this object
    StringBuilder strRepBuilder =
        new StringBuilder("Point with coordinates (");
    for (double coordinate : coordinates) {
      strRepBuilder.append(coordinate).append(",");
    }
    if (strRepBuilder.charAt(strRepBuilder.length() - 1) == ',') {
      strRepBuilder.deleteCharAt(strRepBuilder.length() - 1);
    }
    this.strRep = strRepBuilder.append(")").toString();
  }

  /**
   * Gets the specified coordinate.
   *
   * @param coordinate which coordinate to get
   * @return value of the coordinate
   * @throws IllegalArgumentException if coordinate index out of range
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
   * {@inheritDoc}
   */
  @Override
  public int numDimensions() {
    return coordinates.length;
  }

  /**
   * Calculates distance to another dimensional object. Throws an exception if
   * the number of coordinates in the two dimensional objects differ.
   *
   * @param other another dimensional object
   * @return distance to other dimensional object
   * @throws IllegalArgumentException if this object and the argument have
   * different numbers of coordinates
   * @throws NullPointerException if argument is null
   */
  @Override
  public double distanceTo(Dimensional other) {
    // Check validity of arguments
    if (other == null) {
      throw new NullPointerException("Calling distanceTo() with null");
    }
    if (this.numDimensions() != other.numDimensions()) {
      throw new IllegalArgumentException(String.format(
          "Dimension mismatch between %s and %s", this, other));
    }

    double squaredSum = 0;
    for (int i = 0; i < coordinates.length; i++) {
      squaredSum
        += Math.pow(this.getCoordinate(i) - other.getCoordinate(i), 2);
    }
    return Math.sqrt(squaredSum);
  }

  /**
   * @return string representation of this {@code Point}
   */
  @Override
  public String toString() {
    return strRep;
  }

  /**
   * Checks if this point is equal to another by comparing coordinates.
   *
   * @param o another object
   * @return whether this object is equal to the other object
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Point)) {
      return false;
    }
    Point other = (Point) o;
    if (this.numDimensions() != other.numDimensions()) {
      return false;
    }
    for (int i = 0; i < coordinates.length; i++) {
      if (this.getCoordinate(i) != other.getCoordinate(i)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Computes a hash code for this object using its coordinates.
   *
   * @return hash code
   */
  @Override
  public int hashCode() {
    return Arrays.hashCode(coordinates);
  }

    @Override
    public Dimensional withCoordinate(int coordinate, double value) {
        double[] coordinatesCopy = Arrays.copyOf(coordinates, coordinates.length);
        coordinatesCopy[coordinate] = value;
        return new Point(coordinatesCopy);
    }
}
