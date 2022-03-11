package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

//TODO Terminer doc
//TODO considérer des paramètres finaux pour rendre RoutePoint immuable ?
public record RoutePoint(PointCh point, double position, double distanceToReference) {
    public static final RoutePoint NONE = new RoutePoint(null, Double.NaN, Double.POSITIVE_INFINITY);
    //TODO verify if I'm doing this correctly (numerous abstract concepts, what would be the behaviour of this NONE RoutePoint ?)

    //TODO: Vérifier que j'utilise bien NONE ?

    /**
     * Returns a new RoutePoint that has the same reference and position but is located with
     * its position attribute according to another chunk of the path.
     * @param positionDifference to be added to the current position argument.
     * @return
     */
    public RoutePoint withPositionShiftedBy(double positionDifference){
        return new RoutePoint(point, position+positionDifference, distanceToReference);
    }

    /**
     * Returns the point between this and that that has the lower distanceToReference.
     * @param that
     * @return
     */
    public RoutePoint min(RoutePoint that){
        if(distanceToReference <= that.distanceToReference()){
            return this;
        }else{
            return that;
        }
    }

    /**
     * Overload of min
     * @param thatPoint
     * @param thatPosition
     * @param thatDistanceToReference
     * @return
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        if(distanceToReference <= thatDistanceToReference){
            return this;
        }else{
            return new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
        }
    }
}
