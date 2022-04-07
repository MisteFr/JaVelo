package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * PointCh Record
 * Represents a point in the Swiss coordinate system (CH1903+).
 *
 * @param e e-coordinate of the point in the swiss coordinate system
 * @param n n-coordinate of the point in the swiss coordinate system
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public record PointCh(double e, double n) {

    /**
     * @throws IllegalArgumentException if the point is out of the Swiss Bounds
     */

    public PointCh {
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     * Computes the squared distance between this and that.
     *
     * @param that instance of the point to calculate the squared distance with
     * @return the squared distance between this and that
     */

    public double squaredDistanceTo(PointCh that){
        return Math.pow(distanceTo(that), 2);
    }

    /**
     * Computes the distance between this and that.
     *
     * @param that instance of the point to calculate the distance with
     * @return the distance between this and that
     */

    public double distanceTo(PointCh that){
        return Math2.norm((e - that.e), (n - that.n));
    }

    /**
     * Returns the longitude in WGS84 coordinates of the point in radians.
     *
     * @return longitude in WGS84 coordinates of the point in radians
     */

    public double lon(){
        return Ch1903.lon(e, n);
    }

    /**
     * Returns the latitude in WGS84 coordinates of the point in radians.
     *
     * @return latitude in WGS84 coordinates of the point in radians
     */

    public double lat(){
        return Ch1903.lat(e, n);
    }
}
