package ch.epfl.javelo.routing;

public interface CostFunction {

    /**
     * Returns the factor by which the length of the edge of identity edgeId, starting from the identity nodeId,
     * must be multiplied. This factor must be greater or equal to 1.
     *
     * NB: Cost factor can be infinite (Double.POSITIVE_INFINITY).
     * This is equivalent to considering that the edge does not exist.
     *
     * @param nodeId identity of the starting node
     * @param edgeId identity of the edge (WARNING: not the index in the list of edges leaving the node).
     * @return factor by which the length of the edge must be multiplied
     */
    abstract double costFactor(int nodeId, int edgeId);

}
