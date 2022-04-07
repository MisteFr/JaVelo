package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;


/**
 * RouteComputer class
 * A class that is used to compute the best route between two nodes
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class RouteComputer {

    //All fields are initialized in the constructor
    private float[] distance;
    private int[] predecessor;
    private final Graph GRAPH;
    private final CostFunction COST_FUNCTION;


    /**
     * Constructor for RouteComputer function
     * @param graph the Graph in which the Route needs to be found
     * @param costFunction the CostFunction associated to the edges of the graph
     */

    public RouteComputer(Graph graph, CostFunction costFunction) {
        GRAPH = graph;
        COST_FUNCTION = costFunction;
        distance = new float[graph.nodeCount()];
        predecessor = new int[graph.nodeCount()];
    }


    /**
     * Computes the Route which has the minimal total length between startNodeId and endNodeId nodes using A* algorithm
     * If no route is found, returns null.
     * @param startNodeId start of the route
     * @param endNodeId end of the route
     * @return Route object that represents the route which has the minimal total length between startNodeId and endNodeId
     * @throws IllegalArgumentException TODO
     */

    public Route bestRouteBetween(int startNodeId, int endNodeId) {

        // A type of node that is used to select the next node to explore by searching the minimal distance value of the
        // weighted node.
        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        //fills the distance and predecessor arrays before computing the best route
        Preconditions.checkArgument(startNodeId != endNodeId);
        Arrays.fill(distance, 0, distance.length, Float.POSITIVE_INFINITY);
        Arrays.fill(predecessor, 0, predecessor.length, 0);
        PriorityQueue<WeightedNode> enExploration = new PriorityQueue<>();

        //sets the values of the startNodeId
        distance[startNodeId] = 0;
        float crowDistanceFromStartNodeToEndNode = (float) GRAPH.nodePoint(endNodeId).distanceTo(GRAPH.nodePoint(startNodeId));
        enExploration.add(new WeightedNode(startNodeId, 0F));

        int n;
        int nPrime;
        float d;
        int identityOfFirstEdge;
        int identityOfLastEdge;
        float crowDistanceOfNPrimeToEndNode;

        while (!(enExploration.isEmpty())) {
            n = enExploration.remove().nodeId;
            if(distance[n] == Float.NEGATIVE_INFINITY){
                continue;
            }

            if (n == endNodeId) {
                ArrayList<Integer> nodesOfRoute = new ArrayList<>();
                int tracker = endNodeId; // variable which will have the values of the various nodes that compose the route
                while (tracker != startNodeId) {
                    nodesOfRoute.add(tracker);
                    tracker = predecessor[tracker];
                }
                nodesOfRoute.add(startNodeId); // the ArrayList has all the nodes of the route in reversed order.
                return new SingleRoute(fromNodesReturnEdges(nodesOfRoute));
            }

            identityOfFirstEdge = GRAPH.nodeOutEdgeId(n, 0);
            identityOfLastEdge = GRAPH.nodeOutEdgeId(n, GRAPH.nodeOutDegree(n) - 1);
            //Browse the nodes at the end of the edges coming from the n node.
            for (int edgeId = identityOfFirstEdge; edgeId <= identityOfLastEdge; ++edgeId) {
                nPrime = GRAPH.edgeTargetNodeId(edgeId);
                crowDistanceOfNPrimeToEndNode = (float) GRAPH.nodePoint(endNodeId).distanceTo(GRAPH.nodePoint(nPrime));
                d = (float) (distance[n] + COST_FUNCTION.costFactor(n, edgeId) * GRAPH.edgeLength(edgeId));
                // If the distance found for the nPrime node is lower than the distance already stored,
                // Update the distance array. In all cases, all nPrime nodes are added as WeightedNode to
                // the enExploration priority queue.
                if (d < distance[nPrime]) {
                    distance[nPrime] = d;
                    predecessor[nPrime] = n;
                    enExploration.add(new WeightedNode(nPrime, distance[nPrime]+crowDistanceOfNPrimeToEndNode));
                }
            }
            distance[n] = Float.NEGATIVE_INFINITY;
        }
        // If no route is found, return null.
        return null;
    }



    //takes a list of point in a reverse order to output a list of edges in the right order (starting from the beginning).
    private List<Edge> fromNodesReturnEdges(List<Integer> nodeIds) {
        ArrayList<Edge> edgeIds = new ArrayList<>();
        int identityOfStartingNode;
        int identityOfEndNode;
        int tempEdgeId;

        for (int i = 1; i < nodeIds.size(); ++i) {

            identityOfStartingNode = nodeIds.get(nodeIds.size() - i);
            identityOfEndNode = nodeIds.get(nodeIds.size() - i - 1);

            for (int j = 0; j < GRAPH.nodeOutDegree(identityOfStartingNode); ++j) {

                tempEdgeId = GRAPH.nodeOutEdgeId(identityOfStartingNode, j);
                if (GRAPH.edgeTargetNodeId(tempEdgeId) == identityOfEndNode) {
                    edgeIds.add(Edge.of(GRAPH, tempEdgeId, identityOfStartingNode, identityOfEndNode));
                    break;
                }

            }

        }

        return edgeIds;
    }
}
