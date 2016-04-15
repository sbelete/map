package edu.brown.cs.azhang6.dimension;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a point with latitude and longitude.
 *
 * @author aaronzhang
 */
public class LatLng implements Dimensional {

  /**
   * Minimum latitude.
   */
  public static final double MIN_LAT = -90;

  /**
   * Maximum latitude.
   */
  public static final double MAX_LAT = 90;

  /**
   * Minimum longitude.
   */
  public static final double MIN_LNG = -180;

  /**
   * Maximum longitude.
   */
  public static final double MAX_LNG = 180;

  /**
   * Radius of earth in km.
   */
  public static final double RADIUS = 6371;

  /**
   * Latitude.
   */
  private final double lat;

  /**
   * Longitude.
   */
  private final double lng;

  /**
   * Latitude in radians.
   */
  private final double latRadians;

  /**
   * Longitude in radians.
   */
  private final double lngRadians;

  /**
   * xyz-coordinates. Empty until requested.
   */
  private Optional<double[]> xyz = Optional.empty();

  /**
   * Instantiates with given latitude and longitude.
   *
   * @param lat latitude
   * @param lng longitude
   */
  public LatLng(double lat, double lng) {
    this.lat = lat;
    this.latRadians = Math.toRadians(lat);
    this.lng = lng;
    this.lngRadians = Math.toRadians(lng);
  }

  /**
   * Instantiates with array of latitude and longitude. Array must have at least
   * two elements.
   *
   * @param coordinates latitude and longitude
   */
  public LatLng(double[] coordinates) {
    this(coordinates[0], coordinates[1]);
  }

  /**
   * Calculates xyz-coordinates.
   */
  private void fillXYZ() {
    if (!xyz.isPresent()) {
      double x = RADIUS * Math.cos(latRadians) * Math.cos(lngRadians);
      double y = RADIUS * Math.cos(latRadians) * Math.sin(lngRadians);
      double z = RADIUS * Math.sin(latRadians);
      xyz = Optional.of(new double[]{x, y, z});
    }
  }

  /**
   * @return latitude
   */
  public double getLat() {
    return lat;
  }

  /**
   * @return longitude
   */
  public double getLng() {
    return lng;
  }

  /**
   * @return latitude in radians
   */
  public double getLatRadians() {
    return latRadians;
  }

  /**
   * @return longitude in radians
   */
  public double getLngRadians() {
    return lngRadians;
  }

  /**
   * @return array of xyz-coordinates
   */
  public double[] getXYZ() {
    fillXYZ();
    return Arrays.copyOf(xyz.get(), xyz.get().length);
  }

  /**
   * Calculates tunnel (straight-line) distance to another latitude and
   * longitude.
   *
   * @param other other latitude and longitude
   * @return tunnel distance
   */
  public double tunnelDistanceTo(LatLng other) {
    double[] thisXYZ = this.getXYZ();
    double[] otherXYZ = other.getXYZ();
    double sumSquares = 0;
    for (int i = 0; i < thisXYZ.length; i++) {
      sumSquares += Math.pow(thisXYZ[i] - otherXYZ[i], 2);
    }
    return Math.sqrt(sumSquares);
  }

  /**
   * Gets the coordinate: 0 for latitude, 1 for longitude.
   *
   * @param coordinate index of coordinate
   * @return value of coordinate
   */
  @Override
  public double getCoordinate(int coordinate) {
    switch (coordinate) {
      case 0:
        return lat;
      case 1:
        return lng;
      default:
        throw new IllegalArgumentException(
          "invalid coordinate for LatLng: must be 0 or 1");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int numDimensions() {
    return 2;
  }

  /**
   * Great-circle distance to another LatLng. Uses Haversine formula.
   *
   * @param o LatLng
   * @return great-circle distance
   */
  @Override
  public double distanceTo(Dimensional o) {
    if (!(o instanceof LatLng)) {
      throw new IllegalArgumentException("distance undefined");
    }
    LatLng other = (LatLng) o;

    // Haversine formula
    double dlon = other.lngRadians - this.lngRadians;
    double dlat = other.latRadians - this.latRadians;
    double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(this.latRadians)
      * Math.cos(other.latRadians) * Math.pow(Math.sin(dlon / 2), 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double d = RADIUS * c;
    return d;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Dimensional withCoordinate(int coordinate, double value) {
    switch (coordinate) {
      case 0:
        return new LatLng(value, lng);
      case 1:
        return new LatLng(lat, value);
      default:
        throw new IllegalArgumentException(
          "invalid coordinate for LatLng: must be 0 or 1");
    }
  }

  /**
   * Compares two LatLng objects by latitude and longitude.
   *
   * @param o other LatLng
   * @return equality between this object and other object
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof LatLng)) {
      return false;
    }
    LatLng other = (LatLng) o;
    return this.lat == other.lat && this.lng == other.lng;
  }

  @Override
  public int hashCode() {
    return Objects.hash(lat, lng);
  }

  @Override
  public String toString() {
    return String.format("[LatLng: lat=%f, lng=%f]", lat, lng);
  }
}
