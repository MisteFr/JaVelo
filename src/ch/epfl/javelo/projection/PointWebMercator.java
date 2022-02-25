package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * PointWebMercator Class
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public record PointWebMercator(double x, double y){

    /**
     * Compact constructor that checks for the validity of x and y
     * @param x x coordinate
     * @param y y coordinate
     */
    public PointWebMercator{
        Preconditions.checkArgument((x <= 1) && (x >= 0));
        Preconditions.checkArgument((y <= 1) && (y >= 0));
    }

    /**
     * @param zoomLevel zoom Level of the new Mercator point
     * @param x x coordinate
     * @param y y coordinate
     * @return PointWebMercator object with the desired zoom Level
     */
    public static PointWebMercator of(int zoomLevel, double x, double y){
        return new PointWebMercator((x * Math.pow(2, 8 + zoomLevel)), (y * Math.pow(2, 8 + zoomLevel)));
    }

    public static PointWebMercator ofPointCh(PointCh pointCh){

    }

}