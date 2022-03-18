package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.util.function.DoubleUnaryOperator;


/**
 * Edge class, it represents an edge of an itinerary.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length,
                   DoubleUnaryOperator profile) {

    /**
     * Creates an Edge objet using a Graph object.
     *
     * @param graph      The Graph object to which the Edge belongs
     * @param edgeId     the ID of the edge.
     * @param fromNodeId ID of the first node of the edge
     * @param toNodeId   ID of the last node of the edge
     * @return new Edge object constructed according to the arguments.
     */

    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId), graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     * We use projectionLength from Math2, that returns the length of the projection of a vector AP on a vector AB.
     * Here, A is the fromPoint PointCh, B is the toPoint PointCh, and P is the point argument PointCh.
     *
     * @param point The pointCh to project on this edge.
     * @return the position on the edge, ranging from 0 to the length of the edge, on which the point parameter is projected.
     */
    public double positionClosestTo(PointCh point) {
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n(), point.e(), point.n());
    }

    /**
     * We can find the coordinates of the point at the position param on this edge by adding to the fromPoint
     * coordinates (position / Edge's length) times the vector that links the fromPoint to the toPoint.
     *
     * @param position in meters on the edge of the searched PointCh
     * @return pointCh at the position param on this edge. If it is not on the edge, it is on the straight line that extends the edge.
     */

    public PointCh pointAt(double position) {
        return new PointCh(Math2.interpolate(fromPoint.e(), toPoint.e(), position/length), Math2.interpolate(fromPoint.n(), toPoint.n(), position/length));
    }

    /**
     * Returns the height on the edge at a given position.
     * If position is negative, returns the first elevation. If position is
     *
     * @param position the position on the edge where we search for the elevation.
     * @return double elevation at position param.
     */

    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }
}
