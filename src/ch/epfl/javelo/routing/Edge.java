package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;


//TODO: pourquoi est ce que le profil en long de l'arêt est de type DoubleUnaryOperator et pas ElevationProfile alors qu'on a spécifiquement défini ça ?

/**
 * Graph class
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile) {

    /**
     * Creates an Edge objet using a Graph object.
     * @param graph
     * @param edgeId
     * @param fromNodeId
     * @param toNodeId
     * @return
     */
    //TODO relire, fait à la va vite.
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId){
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId), graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     * We use projectionLength from Math2, that returns the length of the projection of a vector AP on a vector AB. //TODO: verify if my comprehension of the method is right. (that is to say I identify A,B and P points to their right PointCh)
     * Here, A is the fromPoint PointCh, B is the toPoint PointCh, and P is the point argument PointCh.
     * @param point The pointCh to project on this edge.
     * @return the position on the edge, ranging from 0 to the length of the edge, on which the point parameter is projected.
     */
    public double positionClosestTo(PointCh point){
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n(), point.e(), point.n());
    }

    /**
     * We can find the coordinates of the point at the position param on this edge by adding to the fromPoint
     * coordinates (position / Edge's length) times the vector that links the fromPoint to the toPoint.
     * @param position in meters on the edge of the searched PointCh
     * @return
     */
    //TODO: Verify if it works fine with fromPoint beeing higher and more on the right than toPoint. (negative values for deltas)
    public PointCh pointAt(double position){
        double proportions = position / length;
        double deltaE = toPoint.e() - fromPoint.e();
        double deltaN = toPoint.n() - fromPoint.n();
        double e = fromPoint.e() + proportions * deltaE;
        double n = fromPoint.n() + proportions * deltaN;
        return new PointCh(e,n);
    }

    /**
     * Returns the height on the edge at a given position (TODO ranging from 0 to length ?)
     * @param position
     * @return
     */
    public double elevationAt(double position){
        return profile.applyAsDouble(position);
    }
}
