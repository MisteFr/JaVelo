package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 * MultiRoute class
 * Represents a multiple route
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class MultiRoute implements Route {

    private final List<Route> segmentsList;

    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!segments.isEmpty());

        segmentsList = List.copyOf(segments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOfSegmentAt(double position) {
        position = Math2.clamp(0.0, position, length());
        int indexOfSegmentAt = 0;

        for (Route r : segmentsList) {
            //the position isn't on the Route
            if (position > r.length()) {
                //we get the index of the last segment of this Route and add + 1 to get the number of segments
                indexOfSegmentAt += r.indexOfSegmentAt(r.length()) + 1;
            } else {
                //the position is on the route
                indexOfSegmentAt += r.indexOfSegmentAt(position);
            }
            position -= r.length();
        }

        return indexOfSegmentAt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double length() {
        double cumulatedLength = 0.0;
        for (Route r : segmentsList) {
            cumulatedLength += r.length();
        }
        return cumulatedLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Edge> edges() {
        List<Edge> edgesList = new ArrayList<>();
        for (Route r : segmentsList) {
            edgesList.addAll(r.edges());
        }
        return edgesList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> pointsList = new ArrayList<>();
        for (Route r : segmentsList) {
            pointsList.addAll(r.points());
        }

        //remove duplicates
        for (int i = 0; i < pointsList.size(); i++) {
            if ((i + 1) != pointsList.size()) {
                if (pointsList.get(i + 1).e() == pointsList.get(i).e() && pointsList.get(i + 1).n() == pointsList.get(i).n()) {
                    pointsList.remove(pointsList.get(i));
                }
            }
        }

        return pointsList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PointCh pointAt(double position) {
        //we first handle cases where the position is negative
        if (position <= 0) {
            Route firstSegment = segmentsList.get(0);
            return firstSegment.pointAt(0);
        }

        double cumulatedLength = 0.0;
        for (Route r : segmentsList) {
            if (cumulatedLength <= position && (cumulatedLength + r.length()) >= position) {
                return r.pointAt(position - cumulatedLength);
            }
            cumulatedLength += r.length();
        }

        //if we still didn't find the sub route on which the position is, it means it is out of the global route
        //we return the pointAt the last position of the global route.
        Route lastSegment = segmentsList.get(segmentsList.size() - 1);
        return lastSegment.pointAt(lastSegment.length());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double elevationAt(double position) {
        //we first handle cases where the position is negative
        if (position <= 0) {
            Route firstSegment = segmentsList.get(0);
            return firstSegment.elevationAt(0);
        }

        double cumulatedLength = 0.0;
        for (Route r : segmentsList) {
            if (cumulatedLength <= position && (cumulatedLength + r.length()) >= position) {
                return r.elevationAt(position - cumulatedLength);
            }
            cumulatedLength += r.length();
        }

        //if we still didn't find the sub route on which the position is, it means it is out of the global route
        //we return the elevationAt the last position of the global route.
        Route lastSegment = segmentsList.get(segmentsList.size() - 1);
        return lastSegment.elevationAt(lastSegment.length());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nodeClosestTo(double position) {
        //we first handle cases where the position is negative
        if (position <= 0) {
            Route firstSegment = segmentsList.get(0);
            return firstSegment.nodeClosestTo(0);
        }

        double cumulatedLength = 0.0;
        for (Route r : segmentsList) {
            if (cumulatedLength <= position && (cumulatedLength + r.length()) >= position) {
                return r.nodeClosestTo(position - cumulatedLength);
            }
            cumulatedLength += r.length();
        }

        //if we still didn't find the sub route on which the position is, it means it is out of the global route
        //we return the nodeClosestTo the last position of the global route.
        Route lastSegment = segmentsList.get(segmentsList.size() - 1);
        return lastSegment.nodeClosestTo(lastSegment.length());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint nearestPoint = RoutePoint.NONE;
        double cumulatedLength = 0.0;

        //we find the nearestPoint to each segment
        for (Route r : segmentsList) {
            //if we found a new nearestPoint we adjust the Position with cumulatedLength to have position according the global Route
            //and not only the segment
            if (nearestPoint.min(r.pointClosestTo(point)) != nearestPoint) {
                nearestPoint = nearestPoint.min(r.pointClosestTo(point)).withPositionShiftedBy(cumulatedLength);
            }
            cumulatedLength += r.length();
        }

        return nearestPoint;
    }
}
