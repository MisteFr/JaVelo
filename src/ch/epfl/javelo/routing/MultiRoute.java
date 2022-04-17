package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 * MultiRoute class
 * Represents a multiple route, composed of a sequence of contiguous routes.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class MultiRoute implements Route {

    //List of segments composing the MultiRoute
    private final List<Route> SEGMENTS_LIST;

    /**
     * Constructs a MultiRoute composed of the given segments.
     *
     * @param segments segments for the MultiRoute
     * @throws IllegalArgumentException if the list of segments is empty.
     */

    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!segments.isEmpty());

        SEGMENTS_LIST = List.copyOf(segments);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public int indexOfSegmentAt(double position) {
        int indexOfSegmentAt = 0;

        for (Route r : SEGMENTS_LIST) {
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

        //the position was out of the full Route
        if(position > 0){
            return (indexOfSegmentAt - 1);
        }

        return indexOfSegmentAt;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public double length() {
        double cumulatedLength = 0.0;
        for (Route r : SEGMENTS_LIST) {
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
        for (Route r : SEGMENTS_LIST) {
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

        for (Route r : SEGMENTS_LIST) {
            if(!pointsList.isEmpty()){
                pointsList.remove(pointsList.size() - 1);
            }
            pointsList.addAll(r.points());
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
            Route firstSegment = SEGMENTS_LIST.get(0);
            return firstSegment.pointAt(0);
        }

        double cumulatedLength = 0.0;
        for (Route r : SEGMENTS_LIST) {
            if (cumulatedLength <= position && (cumulatedLength + r.length()) >= position) {
                return r.pointAt(position - cumulatedLength);
            }
            cumulatedLength += r.length();
        }

        //if we still didn't find the sub route on which the position is, it means it is out of the global route
        //we return the pointAt the last position of the global route.
        Route lastSegment = SEGMENTS_LIST.get(SEGMENTS_LIST.size() - 1);
        return lastSegment.pointAt(lastSegment.length());
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public double elevationAt(double position) {
        //we first handle cases where the position is negative
        if (position <= 0) {
            Route firstSegment = SEGMENTS_LIST.get(0);
            return firstSegment.elevationAt(0);
        }

        double cumulatedLength = 0.0;
        for (Route r : SEGMENTS_LIST) {
            if (cumulatedLength <= position && (cumulatedLength + r.length()) >= position) {
                return r.elevationAt(position - cumulatedLength);
            }
            cumulatedLength += r.length();
        }

        //if we still didn't find the sub route on which the position is, it means it is out of the global route
        //we return the elevationAt the last position of the global route.
        Route lastSegment = SEGMENTS_LIST.get(SEGMENTS_LIST.size() - 1);
        return lastSegment.elevationAt(lastSegment.length());
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public int nodeClosestTo(double position) {
        //we first handle cases where the position is negative
        if (position <= 0) {
            Route firstSegment = SEGMENTS_LIST.get(0);
            return firstSegment.nodeClosestTo(0);
        }

        double cumulatedLength = 0.0;
        for (Route r : SEGMENTS_LIST) {
            if (cumulatedLength <= position && (cumulatedLength + r.length()) >= position) {
                return r.nodeClosestTo(position - cumulatedLength);
            }
            cumulatedLength += r.length();
        }

        //if we still didn't find the sub route on which the position is, it means it is out of the global route
        //we return the nodeClosestTo the last position of the global route.
        Route lastSegment = SEGMENTS_LIST.get(SEGMENTS_LIST.size() - 1);
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
        for (Route r : SEGMENTS_LIST) {
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
