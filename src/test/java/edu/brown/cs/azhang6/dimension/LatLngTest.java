package edu.brown.cs.azhang6.dimension;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link LatLng}.
 * 
 * @author aaronzhang
 */
public class LatLngTest {
    
    /**
     * Used to check equality between doubles.
     */
    private static final double EPSILON = 0.001;
    
    /**
     * Used to check equality between distances.  Necessary since the formulas
     * defined in LatLng are approximations assuming the earth is a sphere.
     */
    private static final double EPSILON2 = 100;
    
    /**
     * Constructor tests.
     */
    @Test
    public void testConstructor() {
        /*
        System.out.println(new LatLng(0, 0));
        System.out.println(new LatLng(30, 31.5));
        System.out.println(new LatLng(10.1, -5.001));
        */
    }
    
    /**
     * Unit tests for {@link LatLng#getLatRadians()}.
     */
    @Test
    public void testGetLatRadians() {
        assertEquals(new LatLng(0, 0).getLatRadians(), 0, EPSILON);
        assertEquals(new LatLng(32.01, -15).getLatRadians(), 0.5587, EPSILON);
        assertEquals(new LatLng(90, -101.5).getLatRadians(), 1.5708, EPSILON);
        assertEquals(new LatLng(-30, 0).getLatRadians(), -0.5236, EPSILON);
        assertEquals(new LatLng(-90, 0).getLatRadians(), -1.5708, EPSILON);
    }

    /**
     * Unit tests for {@link LatLng#getLngRadians()}.
     */
    @Test
    public void testGetLngRadians() {
        assertEquals(new LatLng(0, 0).getLngRadians(), 0, EPSILON);
        assertEquals(new LatLng(0, 67.43).getLngRadians(), 1.1769, EPSILON);
        assertEquals(new LatLng(15, 121).getLngRadians(), 2.1118, EPSILON);
        assertEquals(new LatLng(0, 180).getLngRadians(), Math.PI, EPSILON);
        assertEquals(new LatLng(0, -31.1).getLngRadians(), -0.5428, EPSILON);
        assertEquals(new LatLng(0, -90).getLngRadians(), -Math.PI/2, EPSILON);
        assertEquals(new LatLng(0, -180).getLngRadians(), -Math.PI, EPSILON);
    }

    /**
     * Unit tests for {@link LatLng#getXYZ()}.
     */
    @Test
    public void testGetXYZ() {
        /*
        Expected results calculated with:
        http://www.oc.nps.edu/oc2902w/coord/llhxyz.htm
        */
        // Latitude/longitude is positive/negative
        double[] test1 = new LatLng(30, 30).getXYZ();
        assertEquals(test1[0], 4788, EPSILON2);
        assertEquals(test1[1], 2764, EPSILON2);
        assertEquals(test1[2], 3170, EPSILON2);
        double[] test2 = new LatLng(54.1, -18.5).getXYZ();
        assertEquals(test2[0], 3555, EPSILON2);
        assertEquals(test2[1], -1189, EPSILON2);
        assertEquals(test2[2], 5143, EPSILON2);
        double[] test3 = new LatLng(-80, 100).getXYZ();
        assertEquals(test3[0], -193, EPSILON2);
        assertEquals(test3[1], 1094, EPSILON2);
        assertEquals(test3[2], -6260, EPSILON2);
        double[] test4 = new LatLng(-37.1, -158.3).getXYZ();
        assertEquals(test4[0], -4732, EPSILON2);
        assertEquals(test4[1], -1883, EPSILON2);
        assertEquals(test4[2], -3826, EPSILON2);
        // Some edge cases
        double[] test5 = new LatLng(0, 0).getXYZ();
        assertEquals(test5[0], 6378, EPSILON2);
        assertEquals(test5[1], 0, EPSILON2);
        assertEquals(test5[2], 0, EPSILON2);
        double[] test6 = new LatLng(-90, 0).getXYZ();
        assertEquals(test6[0], 0, EPSILON2);
        assertEquals(test6[1], 0, EPSILON2);
        assertEquals(test6[2], -6357, EPSILON2);
        double[] test7 = new LatLng(90, 0).getXYZ();
        assertEquals(test7[0], 0, EPSILON2);
        assertEquals(test7[1], 0, EPSILON2);
        assertEquals(test7[2], 6357, EPSILON2);
        double[] test8 = new LatLng(19.5, -180).getXYZ();
        assertEquals(test8[0], -6014, EPSILON2);
        assertEquals(test8[1], 0, EPSILON2);
        assertEquals(test8[2], 2116, EPSILON2);
        double[] test9 = new LatLng(-70, 180).getXYZ();
        assertEquals(test9[0], -2188, EPSILON2);
        assertEquals(test9[1], 0, EPSILON2);
        assertEquals(test9[2], -5971, EPSILON2);
    }

    /**
     * Unit tests for {@link LatLng#tunnelDistanceTo(LatLng)}.
     */
    @Test
    public void testTunnelDistanceTo() {
        assertEquals(new LatLng(30, 30).tunnelDistanceTo(
            new LatLng(54.1, -18.5)), 4587, EPSILON2);
        assertEquals(new LatLng(-80, 100).tunnelDistanceTo(
            new LatLng(-37.1, -158.3)), 5949, EPSILON2);
        assertEquals(new LatLng(19.5, -180).tunnelDistanceTo(
            new LatLng(90, 0)), 7359, EPSILON2);
        assertEquals(new LatLng(30, 30).tunnelDistanceTo(
            new LatLng(30, 30)), 0, EPSILON2);
    }

    /**
     * Unit tests for {@link LatLng#distanceTo(Dimensional)}.
     */
    @Test
    public void testDistanceTo() {
        /*
        Expected results calculated with:
        http://www.movable-type.co.uk/scripts/latlong.html
        */
        // Distance to self
        assertEquals(new LatLng(19.151, -4.8).distanceTo(new LatLng(19.151, -4.8)),
            0, EPSILON2);
        // Some general tests
        assertEquals(new LatLng(30, 30).distanceTo(new LatLng(60, 60)),
            4014, EPSILON2);
        assertEquals(new LatLng(30, 30).distanceTo(new LatLng(60, -60)),
            7154, EPSILON2);
        assertEquals(new LatLng(30, 30).distanceTo(new LatLng(-60, 60)),
            10380, EPSILON2);
        assertEquals(new LatLng(30, 30).distanceTo(new LatLng(-60, -60)),
            12860, EPSILON2);
        // More tests
        assertEquals(new LatLng(-16.19, 5.1).distanceTo(
            new LatLng(80, 14.05)), 10710, EPSILON2);
        assertEquals(new LatLng(-16.19, 18.01).distanceTo(
            new LatLng(90, -120.1)), 11810, EPSILON2);
        assertEquals(new LatLng(-16.19, 18.01).distanceTo(
            new LatLng(-54.8, -120.1)), 11190, EPSILON2);
        assertEquals(new LatLng(50.5, 0).distanceTo(
            new LatLng(0, -1)), 5616, EPSILON2);
    }

    /**
     * Unit tests for {@link LatLng#withCoordinate(int, double)}.
     */
    @Test
    public void testWithCoordinate() {
        assertEquals(new LatLng(0, 0).withCoordinate(0, 30), new LatLng(30, 0));
        assertEquals(new LatLng(0, 0).withCoordinate(1, 30), new LatLng(0, 30));
        assertEquals(new LatLng(19, -10.1).withCoordinate(0, 5.2),
            new LatLng(5.2, -10.1));
        assertEquals(new LatLng(19, -10.1).withCoordinate(1, 5.2),
            new LatLng(19, 5.2));
        assertEquals(new LatLng(19, -10.1).withCoordinate(0, 19),
            new LatLng(19, -10.1));
        assertEquals(new LatLng(19, -10.1).withCoordinate(1, -10.1),
            new LatLng(19, -10.1));
    }
}
