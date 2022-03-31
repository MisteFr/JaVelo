package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * Route interface
 * Represents a route.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public interface Route {


    /**
     * Returns the index of the segment at the given position parameter
     *
     * @param position double
     * @return int index of the segment at the given position parameter
     */

    int indexOfSegmentAt(double position);

    /**
     * Returns the length of the Route in meters
     *
     * @return the length of the Route in meters
     */

    double length();

    /**
     * Returns all the Edges of the Route.
     *
     * @return List<Edge> a list of the edges that make up the path
     */

    List<Edge> edges();

    /**
     * Returns all the points that are on an extremity of an edge that belongs to the Route.
     *
     * @return A List<PointCh> of those points
     */

    List<PointCh> points();

    /**
     * Returns the point that is located at the (position param) position on the Route.
     *
     * @param position the position of the point.
     * @return PointCh object that is located at the (position param) position on the Route.
     */

    PointCh pointAt(double position);

    /**
     * Returns the elevation at the given position on the Route.
     *
     * @param position the position on the Route.
     * @return double the elevation at the given position.
     */

    double elevationAt(double position);

    /**
     * Returns the identity of the node belonging to the Route that is the closest to the position param in the track.
     *
     * @param position the position of the reference to approach.
     * @return int id of the node belonging to the Route that is the closest to the position param in the track.
     */

    int nodeClosestTo(double position);

    /**
     * Returns the RoutePoint closest to the point PointCh argument
     *
     * @param point the PointCh object of reference
     * @return RoutePoint object that is the closest to the point PointCh argument
     */

    RoutePoint pointClosestTo(PointCh point);
}
