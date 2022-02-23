package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * PointCh Class
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public record PointCh(double e, double n) {
    public PointCh {
        if (!SwissBounds.containsEN(e, n)) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Calculate the squared distance between this and that
     * @param that instance of the point to calculate the squared distance with
     * @return the squared distance between this and that
     */
    public double squaredDistanceTo(PointCh that){
        return Math.pow(distanceTo(that), 2);
    }

    /**
     * Calculate the distance between this and that
     * @param that instance of the point to calculate the distance with
     * @return the distance between this and that
     */
    public double distanceTo(PointCh that){
        return Math2.norm((e - that.e), (n - that.n));
    }

    /**
     * Get the longitude in WGS84 coordinates of the point
     * @return longitude in WGS84 coordinates of the point
     */
    public double lon(){
        return Ch1903.lon(e, n);
    }

    /**
     * Get the latitude in WGS84 coordinates of the point
     * @return latitude in WGS84 coordinates of the point
     */
    public double lat(){
        return Ch1903.lat(e, n);
    }
}
