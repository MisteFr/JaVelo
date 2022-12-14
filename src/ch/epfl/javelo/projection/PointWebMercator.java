package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * PointWebMercator Record
 * Represents a point in the WebMercator system.
 *
 * @param x x coordinate of the point
 * @param y y coordinate of the point
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public record PointWebMercator(double x, double y) {

    private static final int SIZE_MAP_ZOOM_ZERO = 8;

    /**
     * Compact constructor that checks for the validity of x and y.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @throws IllegalArgumentException if coordinates are not between 0 and 1.
     */

    public PointWebMercator {
        Preconditions.checkArgument((x <= 1) && (x >= 0) && (y <= 1) && (y >= 0));
    }

    /**
     * Construct a PointWebMercator with a desired zoom level.
     *
     * @param zoomLevel zoom Level of the new Mercator point
     * @param x         x coordinate
     * @param y         y coordinate
     * @return PointWebMercator object with the desired zoom Level
     */

    public static PointWebMercator of(int zoomLevel, double x, double y) {
        return new PointWebMercator(Math.scalb(x, -zoomLevel - SIZE_MAP_ZOOM_ZERO), Math.scalb(y, -zoomLevel - SIZE_MAP_ZOOM_ZERO));
    }

    /**
     * Construct a PointWebMercator based on a PointCh.
     *
     * @param pointCh pointCh object
     * @return PointWebMercator object based on pointCh param lon and lat coordinates
     */

    public static PointWebMercator ofPointCh(PointCh pointCh) {
        return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }

    /**
     * Returns the x coordinate of this point at the desired zoom level.
     *
     * @param zoomLevel desired zoom level
     * @return double x coordinates corresponding to the given zoom level
     */

    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb(x, zoomLevel + SIZE_MAP_ZOOM_ZERO);
    }

    /**
     * Returns the y coordinate of this point at the desired zoom level.
     *
     * @param zoomLevel desired zoom level
     * @return double y coordinates corresponding to the given zoom level
     */

    public double yAtZoomLevel(int zoomLevel) {
        return Math.scalb(y, zoomLevel + SIZE_MAP_ZOOM_ZERO);
    }

    /**
     * Converts Mercator's x coordinate of this point to WGS84 lon coordinate.
     *
     * @return double lon converted on the basis of the x coordinate.
     */

    public double lon() {
        return WebMercator.lon(x);
    }

    /**
     * Converts Mercator's y coordinate of this point to WGS84 lat coordinate.
     *
     * @return double lat converted on the basis of the y coordinate.
     */

    public double lat() {
        return WebMercator.lat(y);
    }

    /**
     * Establishes a correspondence between the current Mercator point and the corresponding PointCh at the same
     * position.
     *
     * @return If the current x and y coordinates refer to a point in the scope chosen in the project defined
     * in the {@link SwissBounds} class, returns the corresponding PointCh at the same position.
     */

    public PointCh toPointCh() {
        double e = Ch1903.e(lon(), lat());
        double n = Ch1903.n(lon(), lat());
        if (SwissBounds.containsEN(e, n)) {
            return new PointCh(e, n);
        } else {
            return null;
        }
    }
}