package edu.brown.cs.azhang6.dimension;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Contains a {@link Dimensional} and a numerical distance.
 *
 * @author aaronzhang
 * @param <T> a dimensional type
 */
public class DimensionalDistance<T extends Dimensional>
    implements Comparable<DimensionalDistance<T>> {

  /**
   * A dimensional object.
   */
  private final T dimensional;

  /**
   * A numerical distance.
   */
  private final double distance;

  /**
   * Creates a new {@code DimensionalDistance} with the given dimensional object
   * and distance.
   *
   * @param dimensional dimensional object
   * @param distance distance
   */
  public DimensionalDistance(T dimensional, double distance) {
    this.dimensional = dimensional;
    this.distance = distance;
  }

  /**
   * Inserts this object into a sorted list of {@code DimensionalDistance}. The
   * list argument must be modifiable.
   *
   * @param sorted sorted list of {@code DimensionalDistance}
   */
  public void insertInto(List<DimensionalDistance<T>> sorted) {
    // Use binary search to find index to insert at
    int index = Collections.binarySearch(sorted, this);
    if (index < 0) {
      index = -index - 1;
    }
    sorted.add(index, this);
  }

  /**
   * Compares this object with another {@code DimensionalDistance} by distance
   * only. Returns -1, 0, or 1 if this object's distance is less than, equal to,
   * or greater than the distance of the other object.
   *
   * @param other another DimensionalDistance
   * @return -1, 0, or 1 if this object's distance is less than, equal to, or
   * greater than the other object's distance
   */
  @Override
  public int compareTo(DimensionalDistance<T> other) {
    if (this.distance < other.distance) {
      return -1;
    } else if (this.distance == other.distance) {
      return 0;
    } else {
      return 1;
    }
  }

  /**
   * @return the dimensional object
   */
  public T getDimensional() {
    return dimensional;
  }

  /**
   * @return the distance
   */
  public double getDistance() {
    return distance;
  }

  /**
   * @return string representation of this {@code DimensionalDistance}
   */
  @Override
  public String toString() {
    return String.format("%s with distance %f", dimensional, distance);
  }

  /**
   * Checks two {@code DimensionalDistance} objects for equality.
   *
   * @param o another object
   * @return whether this object is equal to the other object
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof DimensionalDistance)) {
      return false;
    }
    DimensionalDistance other = (DimensionalDistance) o;
    return this.dimensional.equals(other.dimensional)
        && this.distance == other.distance;
  }

  /**
   * Computes a hash code using the dimensional and distance.
   *
   * @return hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(dimensional, distance);
  }
}
