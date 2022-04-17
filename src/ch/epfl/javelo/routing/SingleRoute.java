package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SingleRoute class
 * Represents a simple route, connecting a starting point to an ending point, without intermediate points.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class SingleRoute implements Route {

    private final List<Edge> EDGES_LIST;
    private final double[] NODES_POSITION_LIST;

    /**
     * SingleRoute Constructor from a list of edges
     *
     * @param edges List of edges that are part of the simple itinerary
     * @throws IllegalArgumentException if the list of edges is empty
     */

    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!edges.isEmpty());

        EDGES_LIST = List.copyOf(edges);

        //Initialize an array containing the position of each node on the SingeRoute for methods pointAt(), elevationAt() and nodeClosestTo()
        double[] tempNodesPositionList = new double[EDGES_LIST.size() + 1];

        double lengthRoute = 0.0;
        for (int i = 0; i < EDGES_LIST.size(); ++i) {
            lengthRoute += EDGES_LIST.get(i).length();
            tempNodesPositionList[(i + 1)] = lengthRoute;
        }

        NODES_POSITION_LIST = tempNodesPositionList;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public double length() {
        double length = 0.0;
        for (Edge e : EDGES_LIST) {
            length += e.length();
        }
        return length;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public List<Edge> edges() {
        return EDGES_LIST;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public List<PointCh> points() {
        List<PointCh> pointsList = new ArrayList<>();

        //we first add every starting point of each edge
        for (Edge e : EDGES_LIST) {
            pointsList.add(e.fromPoint());
        }
        //we add the end point of the last edge
        pointsList.add(EDGES_LIST.get(EDGES_LIST.size() - 1).toPoint());
        return pointsList;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0.0, position, length());

        int binarySearchResult = Arrays.binarySearch(NODES_POSITION_LIST, position);

        if (binarySearchResult >= 0) {
            if (binarySearchResult < EDGES_LIST.size()) {
                return EDGES_LIST.get(binarySearchResult).fromPoint();
            } else {
                return EDGES_LIST.get(binarySearchResult - 1).toPoint();
            }
        } else {
            int indexEdge = -(binarySearchResult + 2);
            return EDGES_LIST.get(indexEdge).pointAt(position - NODES_POSITION_LIST[indexEdge]);
        }
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0.0, position, length());

        int binarySearchResult = Arrays.binarySearch(NODES_POSITION_LIST, position);

        if (binarySearchResult >= 0) {
            if (binarySearchResult < EDGES_LIST.size()) {
                return EDGES_LIST.get(binarySearchResult).elevationAt(0);
            } else {
                return EDGES_LIST.get(binarySearchResult - 1).elevationAt(EDGES_LIST.get(binarySearchResult - 1).length());
            }
        } else {
            int indexEdge = -(binarySearchResult + 2);
            return EDGES_LIST.get(indexEdge).elevationAt(position - NODES_POSITION_LIST[indexEdge]);
        }
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0.0, position, length());

        int binarySearchResult = Arrays.binarySearch(NODES_POSITION_LIST, position);

        if (binarySearchResult >= 0) {
            if (binarySearchResult < EDGES_LIST.size()) {
                return EDGES_LIST.get(binarySearchResult).fromNodeId();
            } else {
                return EDGES_LIST.get(EDGES_LIST.size() - 1).toNodeId();
            }
        } else {
            int indexEdge = -(binarySearchResult + 2);

            if (position - NODES_POSITION_LIST[indexEdge] > EDGES_LIST.get(indexEdge).length() / 2) {
                return EDGES_LIST.get(indexEdge).toNodeId();
            } else {
                return EDGES_LIST.get(indexEdge).fromNodeId();
            }
        }
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint nearestPoint = RoutePoint.NONE;
        double cumulatedLength = 0.0;

        for (Edge e : EDGES_LIST) {
            double lengthOfProjection = Math2.clamp(0.0, e.positionClosestTo(point), e.length());
            PointCh nearestPointOnEdge = e.pointAt(lengthOfProjection);
            double distance = nearestPointOnEdge.distanceTo(point);

            nearestPoint = nearestPoint.min(nearestPointOnEdge, cumulatedLength + Math2.clamp(0.0, lengthOfProjection, e.length()), distance);

            cumulatedLength += e.length();
        }

        return nearestPoint;
    }
}
