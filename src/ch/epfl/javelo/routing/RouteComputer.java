package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

//todo vérifier si 2.Noeuds visité fonctionne correctement
/**
 * TODO descriptions, documentation
 */
public final class RouteComputer {

    private float[] distance;
    private int[] predecesseur;
    private ArrayList<Integer> en_exploration;
    private final Graph GRAPH;
    private final CostFunction COST_FUNCTION;


    public RouteComputer(Graph graph, CostFunction costFunction) {
        GRAPH = graph;
        COST_FUNCTION = costFunction;
        distance = new float[graph.nodeCount()];
        predecesseur = new int[graph.nodeCount()];
    }


    public Route bestRouteBetween(int startNodeId, int endNodeId) {

        record WeightedNode(int nodeId, float distance, float crowDistance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance + this.crowDistance, that.distance + that.crowDistance);
            }
        }

        Preconditions.checkArgument(startNodeId != endNodeId);
        Arrays.fill(distance, 0, distance.length, Float.POSITIVE_INFINITY);
        Arrays.fill(predecesseur, 0, predecesseur.length, 0);
        PriorityQueue<WeightedNode> enExploration = new PriorityQueue<>();

        distance[startNodeId] = 0;
        float crowDistanceFromStartNodeToEndNode = (float) Math2.norm(GRAPH.nodePoint(endNodeId).e()
                - GRAPH.nodePoint(startNodeId).e(), GRAPH.nodePoint(endNodeId).n() - GRAPH.nodePoint(startNodeId).n());
        enExploration.add(new WeightedNode(startNodeId, 0F, crowDistanceFromStartNodeToEndNode));

        //todo verify if it is good practice to declare outside of loop.
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
                    tracker = predecesseur[tracker];
                }
                nodesOfRoute.add(startNodeId); // the ArrayList has all the nodes of the route in reversed order.
                return new SingleRoute(fromNodesReturnEdges(nodesOfRoute));
            }

            identityOfFirstEdge = GRAPH.nodeOutEdgeId(n, 0);
            identityOfLastEdge = GRAPH.nodeOutEdgeId(n, GRAPH.nodeOutDegree(n) - 1);
            for (int edgeId = identityOfFirstEdge; edgeId <= identityOfLastEdge; ++edgeId) {
                nPrime = GRAPH.edgeTargetNodeId(edgeId);
                crowDistanceOfNPrimeToEndNode = (float) Math2.norm(GRAPH.nodePoint(endNodeId).e() -
                        GRAPH.nodePoint(nPrime).e(), GRAPH.nodePoint(endNodeId).n() - GRAPH.nodePoint(nPrime).n());
                d = (float) (distance[n] + COST_FUNCTION.costFactor(n, edgeId) * GRAPH.edgeLength(edgeId));
                if (d < distance[nPrime]) {
                    distance[nPrime] = d;
                    predecesseur[nPrime] = n;
                    enExploration.add(new WeightedNode(nPrime, distance[nPrime], crowDistanceOfNPrimeToEndNode));
                }
            }
            distance[n] = Float.NEGATIVE_INFINITY;
        }
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
