package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

/**
 * RoutePoint record
 * Represents the point on a route closest to a given reference point, which is near the route.
 *
 * @param point point in the swiss system coordinate
 * @param position position of the point along the route, in meters
 * @param distanceToReference the distance, in meters, between the point and the reference point
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public record RoutePoint(PointCh point, double position, double distanceToReference) {

    /**
     * None existing RoutePoint
     */
    public static final RoutePoint NONE = new RoutePoint(null, Double.NaN, Double.POSITIVE_INFINITY);

    /**
     * Returns a new RoutePoint that has the same reference and position but is located with
     * its position attribute according to another chunk of the path.
     *
     * @param positionDifference to be added to the current position argument.
     * @return A RoutePoint that has the same reference and position but has an updated position attribute.
     */

    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return new RoutePoint(point, position + positionDifference, distanceToReference);
    }

    /**
     * Returns the RoutePoint between this and that that has the lowest distanceToReference.
     *
     * @param that RoutePoint object with its own distanceToReference.
     * @return RoutePoint object that has the lowest distanceToReference.
     */

    public RoutePoint min(RoutePoint that) {
        if (distanceToReference <= that.distanceToReference()) {
            return this;
        } else {
            return that;
        }
    }

    /**
     * Returns the RoutePoint between this and the one that would have the attributes given in parameters that has the lowest distanceToReference.
     * Variant of min, works in a way that helps to avoid useless creations of RoutePoint objects.
     *
     * @param thatPoint               attribute of hypothetical RoutePoint object
     * @param thatPosition            attribute of hypothetical RoutePoint object
     * @param thatDistanceToReference attribute of hypothetical RoutePoint object
     * @return RoutePoint object that has the lowest distanceToReference. Creates a new one if distanceToReference > thatDistanceToReference.
     */

    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        if (distanceToReference <= thatDistanceToReference) {
            return this;
        } else {
            return new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
        }
    }
}
