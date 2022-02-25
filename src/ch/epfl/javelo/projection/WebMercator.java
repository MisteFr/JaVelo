package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * WebMercator class
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class WebMercator {

    // Non-instantiable class
    private WebMercator(){}

    /**
     * Get the x coordinate of the projection of a point with a longitude lon (WGS 84 to WebMercator).
     * @param lon
     * @return the x coordinate
     */
    static double x(double lon){
        return (1 / (2 * Math.PI)) * (lon + Math.PI);
    }

    /**
     * Get the y coordinate of the projection of a point with a latitude lat (WGS 84 to WebMercator).
     * @param lat
     * @return the y coordinate
     */
    static double y(double lat){
        return (1 / (2 * Math.PI)) * (Math.PI - Math2.asinh(Math.tan(lat)));
    }

    /**
     * Get the longitude of a point of a given x coordinate (WebMercator to WGS 84).
     * @param x
     * @return the longitude in radians
     */
    static double lon(double x){
        return 2 * Math.PI * x - Math.PI;
    }

    /**
     * Get the latitude of a point of a given y coordinate (WebMercator to WGS 84).
     * @param y
     * @return the latitude in radians
     */
    static double lat(double y){
        return Math.atan(Math.sinh(Math.PI - 2 * Math.PI * y));
    }
}
