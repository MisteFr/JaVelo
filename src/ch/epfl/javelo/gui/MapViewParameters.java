package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;


/**
 * MapViewParameters Record
 * Represents the parameters of the background map presented in the gui.
 *
 * @param zoomLevel     zoom level
 * @param indexTopLeftX x-coordinate of the top-left corner of the displayed map portion
 * @param indexTopLeftY y-coordinate of the top-left corner of the displayed map portion
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public record MapViewParameters(int zoomLevel, double indexTopLeftX, double indexTopLeftY) {

    /**
     * Returns the coordinates of the top-left corner as a Point2D object
     *
     * @return coordinate of the top-left corner
     */

    public Point2D topLeft() {
        return new Point2D(indexTopLeftX, indexTopLeftY);
    }

    /**
     * Returns an instance of MapViewParameters with the new coordinates of the top left corner
     *
     * @param newIndexTopLeftX new x-coordinate for the top-left corner
     * @param newIndexTopLeftY new y-coordinate for the top-left corner
     * @return new MapViewParameters instance with the next coordinates for the top left corner
     */

    public MapViewParameters withMinXY(double newIndexTopLeftX, double newIndexTopLeftY) {
        return new MapViewParameters(zoomLevel, newIndexTopLeftX, newIndexTopLeftY);
    }

    /**
     * Returns a new PointWebMercator point based on the x and y coordinates of a point in the map portion.
     *
     * @param xCoordinate x-coordinate of the point in the map portion
     * @param yCoordinate y-coordinate of the point in the map portion
     * @return PointWebMercator point
     * @throws IllegalArgumentException if the coordinate aren't in the map (PointWebMercator handle the case
     *                                  when the coordinates are incorrect)
     */

    public PointWebMercator pointAt(double xCoordinate, double yCoordinate) {
        return PointWebMercator.of(zoomLevel, xCoordinate + indexTopLeftX, yCoordinate + indexTopLeftY);
    }

    /**
     * Returns the corresponding x coordinate to a PointWebMercator point in the map portion
     *
     * @param point instance of the PointWebMercator
     * @return corresponding coordinate in the map portion
     */

    public double viewX(PointWebMercator point) {
        return (point.xAtZoomLevel(zoomLevel) - indexTopLeftX);
    }

    /**
     * Returns the corresponding y coordinate to a PointWebMercator point in the map portion
     *
     * @param point instance of the PointWebMercator
     * @return corresponding coordinate in the map portion
     */

    public double viewY(PointWebMercator point) {
        return (point.yAtZoomLevel(zoomLevel) - indexTopLeftY);
    }
}
