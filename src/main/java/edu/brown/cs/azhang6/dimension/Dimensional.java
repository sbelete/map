package edu.brown.cs.azhang6.dimension;

/**
 * An object with indexed coordinates.
 *
 * @author aaronzhang
 */
public interface Dimensional {

  /**
   * Gets the specified coordinate.
   *
   * @param coordinate index of coordinate to get
   * @return coordinate at specified index
   */
  double getCoordinate(int coordinate);

  /**
   * Returns number of dimensions.
   *
   * @return number of dimensions
   */
  int numDimensions();

  /**
   * Returns distance to another dimensional object.
   *
   * @param other dimensional object to find distance to
   * @return distance to other object
   */
  double distanceTo(Dimensional other);

  /**
   * Returns a new dimensional object with the specified coordinate set.
   *
   * @param coordinate coordinate
   * @param value new value of coordinate
   * @return new dimensional object
   */
  Dimensional withCoordinate(int coordinate, double value);
}
