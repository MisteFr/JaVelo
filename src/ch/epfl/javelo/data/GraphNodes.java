package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 * GraphNodes class
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;                     // difference of index compared to the first value of
    // the node (E) of the value representing the E coordinate.

    private static final int OFFSET_N = OFFSET_E + 1;          // difference of index compared to the first value of
    // the node (E) of the value representing the N coordinate.

    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;  // difference of index compared to the first value of
    // the node (E) of the value representing out edges.

    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1; // number of int values used to represent a node

    /**
     * Nodes are characterized by three values in the buffer, hence the total number of nodes is the capacity divided
     * by the number of attributes of a single node.
     *
     * @return int number of nodes in the buffer.
     */

    public int count() {
        return buffer.capacity() / NODE_INTS;
    }


    /**
     * Gets E coordinate of nodeId node in buffer IntBuffer.
     *
     * @param nodeId starts at 0
     * @return double E coordinate of nodeId node interpreted as Q28.4.
     */

    public double nodeE(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_E));
    }

    /**
     * Gets N coordinate of nodeId node in buffer IntBuffer.
     *
     * @param nodeId starts at 0
     * @return double N coordinate of nodeId node interpreted as Q28.4.
     */

    public double nodeN(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_N));
    }

    /**
     * Gets number of out edges of nodeId node in buffer IntBuffer, stored in the 4 MSB of the third int of the nodeId node.
     *
     * @param nodeId starts at 0
     * @return int number of out edges of nodeId node.
     */

    public int outDegree(int nodeId) {
        return Bits.extractUnsigned(buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES), 28, 4);
    }

    /**
     * Returns the int identity of the edgeIndex edge. The identity of the edge is its index in the buffer that lists
     * all the edges. We can compute this index by adding (the index of the first out edge of the nodeId node stored in
     * the 28 LSB of the third value associated with the nodeId node) and (the edgeIndex index).
     *
     * @param nodeId    starts at 0
     * @param edgeIndex starts at 0
     * @return the int edgeId value.
     */

    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        int firstEdgeId = Bits.extractUnsigned(buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES), 0, 28);
        return firstEdgeId + edgeIndex;
    }
}
