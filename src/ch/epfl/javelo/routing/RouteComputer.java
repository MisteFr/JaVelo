package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * TODO descriptions, documentation
 */
public final class RouteComputer {

    private float[] distance;
    private int[] predecesseur;
    private ArrayList<Integer> en_exploration;
    private final Graph GRAPH;
    private final CostFunction COST_FUNCTION;


    public RouteComputer(Graph graph, CostFunction costFunction){
        GRAPH = graph;
        COST_FUNCTION = costFunction;
        distance = new float[graph.nodeCount()];
        predecesseur = new int[graph.nodeCount()];
    }



    public Route bestRouteBetween(int startNodeId, int endNodeId){

        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        Preconditions.checkArgument(startNodeId != endNodeId);
        Arrays.fill(distance, 0, distance.length, Float.POSITIVE_INFINITY);
        Arrays.fill(predecesseur, 0, predecesseur.length, 0);
        PriorityQueue<WeightedNode> enExploration = new PriorityQueue<>();

        distance[startNodeId] = 0;
        enExploration.add(new WeightedNode(startNodeId, 0));

        //todo verify if it is a good idea to declare outside of loop.
        int n;
        int nPrime;
        float d;
        int identityOfFirstEdge;
        int identityOfLastEdge;
        while(!(enExploration.isEmpty())){
            n = enExploration.remove().nodeId;
            System.out.println(n);

            if(n == endNodeId){
                ArrayList<Integer> nodesOfRoute = new ArrayList<>();
                int tracker = endNodeId; // variable which will have the values of the various nodes that compose the route
                while(tracker != startNodeId){
                    nodesOfRoute.add(tracker);
                    tracker = predecesseur[tracker];
                }
                nodesOfRoute.add(startNodeId); // the ArrayList has all the nodes of the route in reversed order.
                return new SingleRoute(fromNodesReturnEdges(nodesOfRoute));
            }

            //The node
            identityOfFirstEdge = GRAPH.nodeOutEdgeId(n, 0);
            identityOfLastEdge = GRAPH.nodeOutEdgeId(n, GRAPH.nodeOutDegree(n) -1);
            for(int edgeId = identityOfFirstEdge; edgeId <= identityOfLastEdge; ++edgeId){
                nPrime = GRAPH.edgeTargetNodeId(edgeId);
                //todo rajouter dernière optimisation 2.Nœuds visités
                d = (float) (distance[n] + COST_FUNCTION.costFactor(n, edgeId)*GRAPH.edgeLength(edgeId));
                if(d < distance[nPrime]){
                    distance[nPrime] = d;
                    predecesseur[nPrime] = n;
                    enExploration.add(new WeightedNode(nPrime, distance[nPrime]));
                }
            }

        }
        return null;
    }

    //takes a list of point in a reverse order to output a list of edges in the right order (starting from the beginning).
    private List<Edge> fromNodesReturnEdges(List<Integer> nodeIds){
        ArrayList<Edge> edgeIds = new ArrayList<>();
        int identityOfStartingNode;
        int identityOfEndNode;
        int tempEdgeId;

        for (int i = 1; i < nodeIds.size(); ++i){

            identityOfStartingNode = nodeIds.get(nodeIds.size() - i);
            identityOfEndNode = nodeIds.get(nodeIds.size() - i - 1);

            for (int j = 0; j < GRAPH.nodeOutDegree(identityOfStartingNode); ++j) {

                tempEdgeId = GRAPH.nodeOutEdgeId(identityOfStartingNode, j);
                if (GRAPH.edgeTargetNodeId(tempEdgeId) == identityOfEndNode){
                    edgeIds.add(Edge.of(GRAPH, tempEdgeId, identityOfStartingNode, identityOfEndNode));
                }

            }

        }

        return edgeIds;
    }
}
