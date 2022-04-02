package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Graph class
 * Represents the JaVelo graph.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class Graph {

    private final GraphNodes NODES;
    private final GraphSectors SECTORS;
    private final GraphEdges EDGES;
    private final List<AttributeSet> ATTRIBUTE_SETS;

    /**
     * Graph constructor, we use List.copyOf() to make sure it stays immuable.
     *
     * @param nodes         GraphNodes
     * @param sectors       GraphSectors
     * @param edges         GraphEdges
     * @param attributeSets List<AttributeSet>
     */

    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        NODES = nodes;
        SECTORS = sectors;
        EDGES = edges;
        ATTRIBUTE_SETS = List.copyOf(attributeSets);
    }

    /**
     * Returns the data in the file 'filename' as a MappedByteBuffer
     *
     * @param basePath path where data files are stored
     * @param filename name of the file to load
     * @return a buffer with data from the file
     * @throws IOException if something went wrong while trying to load data from the file 'filename'
     */

    private static MappedByteBuffer loadData(Path basePath, String filename) throws IOException {
        Path filePath = basePath.resolve(filename);
        try (FileChannel channel = FileChannel.open(filePath)) {
            return channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
    }

    /**
     * Returns the JaVelo Graph obtained from the files in the basePath folder
     *
     * @param basePath path where data files are stored
     * @return Graph instance
     * @throws IOException if something went wrong while loading data from resources
     */

    public static Graph loadFrom(Path basePath) throws IOException {
        //initialize buffers
        IntBuffer nodesBuffer = loadData(basePath, "nodes.bin").asIntBuffer();
        ByteBuffer edgesBuffer = loadData(basePath, "edges.bin");
        IntBuffer profilesBuffer = loadData(basePath, "profile_ids.bin").asIntBuffer();
        ShortBuffer elevationsBuffer = loadData(basePath, "elevations.bin").asShortBuffer();
        ByteBuffer sectorsBuffer = loadData(basePath, "sectors.bin");

        //initialize attributes
        LongBuffer sectorsAttributesBuffer = loadData(basePath, "attributes.bin").asLongBuffer();
        ArrayList<AttributeSet> attributeSets = new ArrayList<>();
        while (sectorsAttributesBuffer.hasRemaining()) {
            attributeSets.add(new AttributeSet(sectorsAttributesBuffer.get()));
        }

        return new Graph(new GraphNodes(nodesBuffer), new GraphSectors(sectorsBuffer), new GraphEdges(edgesBuffer, profilesBuffer, elevationsBuffer), attributeSets);
    }

    /**
     * Get the total number of nodes in the graph
     *
     * @return int the total number of nodes in the graph
     */

    public int nodeCount() {
        return NODES.count();
    }

    /**
     * Get the position of a given node of identity 'nodeId'
     *
     * @param nodeId id of the node
     * @return PointCh the coordinate of given node
     */

    public PointCh nodePoint(int nodeId) {
        return new PointCh(NODES.nodeE(nodeId), NODES.nodeN(nodeId));
    }

    /**
     * Get the number of edges coming out of the node of given identity
     *
     * @param nodeId id of the node
     * @return int the number of edges coming out of the node
     */

    public int nodeOutDegree(int nodeId) {
        return NODES.outDegree(nodeId);
    }

    /**
     * Get the identity of the edgeIndex-th edge exiting the node of identity 'nodeId'
     *
     * @param nodeId    id of the node
     * @param edgeIndex index of the edge
     * @return int the identity of the edgeIndex-th edge exiting the node
     */

    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return NODES.edgeId(nodeId, edgeIndex);
    }

    /**
     * Get the identity of the closest node to a given point, at the given maximum distance (in meters)
     * or -1 if no node matches these criteria
     *
     * @param point          given point
     * @param searchDistance maximum distance to search around the point
     * @return int identity of the closest point to the given point in the search distance radius
     */

    public int nodeClosestTo(PointCh point, double searchDistance) {
        //we get all the sectors in the area
        List<GraphSectors.Sector> sectorsInArea = SECTORS.sectorsInArea(point, searchDistance);

        double lowestDistance = searchDistance > 0 ? Math.pow(searchDistance, 2) : 0;
        int nearestNodeId = -1;

        //we loop through all the nodes of every sector to find out which one is the nearest
        for (GraphSectors.Sector s : sectorsInArea) {
            for (int nodeToCheck = s.startNodeId(); nodeToCheck < s.endNodeId(); nodeToCheck++) {
                double distance = point.squaredDistanceTo(nodePoint(nodeToCheck));
                if (distance < lowestDistance) {
                    lowestDistance = distance;
                    nearestNodeId = nodeToCheck;
                }
            }
        }

        return nearestNodeId;
    }

    /**
     * Get the identity of the destination node of the given edge
     *
     * @param edgeId id of the edge
     * @return int identity of the destination node of the given edge
     */

    public int edgeTargetNodeId(int edgeId) {
        return EDGES.targetNodeId(edgeId);
    }

    /**
     * Check if the edge of identity 'edgeId' goes in the opposite direction of the OSM path from which it comes
     *
     * @param edgeId id of the edge
     * @return boolean true if inverted, false otherwise
     */

    public boolean edgeIsInverted(int edgeId) {
        return EDGES.isInverted(edgeId);
    }

    /**
     * Get the AttributeSet of OSM attributes attached to an edge of identity 'edgeId'
     *
     * @param edgeId id of the edge
     * @return AttributeSet
     */

    public AttributeSet edgeAttributes(int edgeId) {
        return ATTRIBUTE_SETS.get(EDGES.attributesIndex(edgeId));
    }

    /**
     * Get the length of the edge of identity 'edgeId'
     *
     * @param edgeId id of the edge
     * @return double length of the edge
     */

    public double edgeLength(int edgeId) {
        return EDGES.length(edgeId);
    }

    /**
     * Get the positive elevation of the edge of identity 'edgeId'
     *
     * @param edgeId id of the edge
     * @return double elevation gain of the edge
     */

    public double edgeElevationGain(int edgeId) {
        return EDGES.elevationGain(edgeId);
    }

    /**
     * Get the profile of the edge of identity 'edgeId'
     * If the edge doesn't have a profile, returns a constant Function of value Double.NaN
     *
     * @param edgeId id of the edge
     * @return DoubleUnaryOperator Function: profile of the given identity edge
     */

    public DoubleUnaryOperator edgeProfile(int edgeId) {
        if (EDGES.hasProfile(edgeId)) {
            return Functions.sampled(EDGES.profileSamples(edgeId), EDGES.length(edgeId));
        } else {
            return Functions.constant(Double.NaN);
        }
    }
}
