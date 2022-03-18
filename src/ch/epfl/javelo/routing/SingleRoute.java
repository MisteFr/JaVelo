package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SingleRoute class
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */


public final class SingleRoute implements Route {

    private final List<Edge> edgesList;
    private final double[] nodesPositionList;

    /**
     * SingleRoute Constructor from a list of edges
     *
     * @param edges List of edges that are part of the simple itinerary
     */
    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!edges.isEmpty());

        edgesList = List.copyOf(edges);

        //we initialize an array containing the position of each node on the SingeRoute for methods pointAt(), elevationAt() and nodeClosestTo()
        double[] tempNodesPositionList = new double[edgesList.size() + 1];

        double lengthRoute = 0.0;
        for (int i = 0; i < edgesList.size(); ++i) {
            lengthRoute += edgesList.get(i).length();
            tempNodesPositionList[(i + 1)] = lengthRoute;
        }

        nodesPositionList = tempNodesPositionList;
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
        for (Edge e : edgesList) {
            length += e.length();
        }
        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Edge> edges() {
        return edgesList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> pointsList = new ArrayList<PointCh>();

        //we first add every starting point of each edge
        for (Edge e : edgesList) {
            pointsList.add(e.fromPoint());
        }
        //we add the end point of the last edge
        pointsList.add(edgesList.get(edgesList.size() - 1).toPoint());
        return pointsList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PointCh pointAt(double position) {
        position = Math.max(position, 0.0);

        int binarySearchResult = Arrays.binarySearch(nodesPositionList, position);

        if (binarySearchResult >= 0) {
            if (binarySearchResult < edgesList.size()) {
                return edgesList.get(binarySearchResult).fromPoint();
            } else {
                return edgesList.get(binarySearchResult - 1).toPoint();
            }
        } else {
            int indexEdge = -(binarySearchResult + 2);
            if (indexEdge < edgesList.size()) {
                return edgesList.get(indexEdge).pointAt(position - nodesPositionList[indexEdge]);
            } else {
                return edgesList.get(edgesList.size() - 1).pointAt(position - nodesPositionList[edgesList.size() - 1]);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double elevationAt(double position) {
        position = Math.max(position, 0.0);

        int binarySearchResult = Arrays.binarySearch(nodesPositionList, position);
        System.out.println("binary" + binarySearchResult);

        if (binarySearchResult >= 0) {
            if (binarySearchResult < edgesList.size()) {
                return edgesList.get(binarySearchResult).elevationAt(0);
            } else {
                return edgesList.get(binarySearchResult - 1).elevationAt(edgesList.get(binarySearchResult - 1).length());
            }
        } else {
            int indexEdge = -(binarySearchResult + 2);
            if (indexEdge < edgesList.size()) {
                return edgesList.get(indexEdge).elevationAt(position - nodesPositionList[indexEdge]);
            } else {
                //indexEdge - 1 so to do not remove the length of the latest edge
                return edgesList.get(edgesList.size() - 1).elevationAt(position - nodesPositionList[indexEdge - 1]);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nodeClosestTo(double position) {
        position = Math.max(position, 0.0);

        int binarySearchResult = Arrays.binarySearch(nodesPositionList, position);

        if (binarySearchResult >= 0) {
            if (binarySearchResult < edgesList.size()) {
                return edgesList.get(binarySearchResult).fromNodeId();
            } else {
                return edgesList.get(edgesList.size() - 1).toNodeId();
            }
        } else {
            int indexEdge = -(binarySearchResult + 2);

            if (indexEdge < edgesList.size()) {
                if (position - nodesPositionList[indexEdge] > edgesList.get(indexEdge).length() / 2) {
                    return edgesList.get(indexEdge).toNodeId();
                } else {
                    return edgesList.get(indexEdge).fromNodeId();
                }
            } else {
                //it means that the nearest node is the last one
                return edgesList.get(edgesList.size() - 1).toNodeId();
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

        for (Edge e : edgesList) {
            double lengthOfProjection = e.positionClosestTo(point);
            PointCh nearestPointOnEdge = e.pointAt(e.positionClosestTo(point));
            double distance = nearestPointOnEdge.distanceTo(point);

            nearestPoint = nearestPoint.min(nearestPointOnEdge, cumulatedLength + lengthOfProjection, distance);


            System.out.println(nearestPoint);
            cumulatedLength += e.length();
        }

        return nearestPoint;
    }
}
