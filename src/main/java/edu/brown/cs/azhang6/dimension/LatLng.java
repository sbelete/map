package edu.brown.cs.azhang6.dimension;

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
     * Instantiates with given latitude and longitude.
     * 
     * @param lat latitude
     * @param lng longitude
     */
    public LatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
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
     * Great-circle distance to another LatLng.  Uses Haversine formula.
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
        double dlon = other.lng - this.lng;
        double dlat = other.lat - this.lat;
        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(this.lat)
                * Math.cos(other.lat) * Math.pow(Math.sin(dlon / 2), 2);
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
}