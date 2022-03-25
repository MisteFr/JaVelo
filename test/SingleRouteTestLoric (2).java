/*package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.RoutePoint;
import ch.epfl.javelo.routing.SingleRoute;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static ch.epfl.javelo.routing.TestUtile.*;

public class SingleRouteTest {

    public Graph lausanne(){
        Graph graph=null;
        Path basePath = Path.of(Graph.LAUSANNE_PATH);
        try {
            graph = Graph.loadFrom(basePath);
        }catch (IOException e) {
            e.printStackTrace();
            fail();
        }

        return graph;
    }

    final Graph lsn = lausanne();

    // Data from Antoine
    PointCh knownPoint = new PointCh(2530000, 1155000);
    PointCh randomPoint = new PointCh(eLaus(), nLaus());
    PointCh fromPoint = randomPoint;

    float[] samples = new float[]{22, 10, 5, 12, 5, 6};
    float[] samples2 = new float[]{6, 7, 8, 9, 10, 11, 12};
    float[] samples3 = new float[]{12, 8, 4, 2, -10, 15, Float.NaN};
    DoubleUnaryOperator profile = Functions.sampled(samples, 10);
    DoubleUnaryOperator profile2 = Functions.sampled(samples2, 100);
    DoubleUnaryOperator profile3 = Functions.sampled(samples3, 1000);

    // Data from example
    PointCh point0 = lsn.nodePoint(0);
    PointCh point1 = lsn.nodePoint(1);
    PointCh point2 = lsn.nodePoint(2);
    PointCh point3 = lsn.nodePoint(3);
    PointCh point4 = lsn.nodePoint(4);
    PointCh point5 = lsn.nodePoint(5);
    final Edge exEdge0 = new Edge(0, 1, point0, point1, 5800, profile);
    final Edge exEdge1 = new Edge(1, 2, point1, point2, 2300, profile);
    final Edge exEdge2 = new Edge(2, 3, point2, point3, 1100, profile);
    final Edge exEdge3 = new Edge(3, 4, point3, point4, 2200, profile);
    final Edge exEdge4 = new Edge(4, 5, point4, point5, 1700, profile);
    final double exArray[] = new double[] {
            0, 5800, 8100, 9200, 11400, 13100
    };

    // Older data
    final PointCh Bern = new PointCh(2600000, 1200000);
    final PointCh Basel = new PointCh(2608395, 1267465);
    final PointCh inf = new PointCh(2532864.1, 1152298.5);

    DoubleUnaryOperator baseProfile = Functions.constant(9);

    Edge edgeDiscord = new Edge(0, 1, lsn.nodePoint(0), lsn.nodePoint(1), 95, Functions.constant(9));

    Edge baseEdge = new Edge(0, 1, inf, Bern, 10, baseProfile);

    // TODO how do I make an edge manually?
    List<Edge> emptyEdgesList = new ArrayList<>();
    List<Edge> edgeListOfOne = List.of(baseEdge);
    // Edge from0To1 = new Edge(0, 1, inf, Bern, 10000, PROFILE);

    @Test
    void constructorCantHaveEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> {
            SingleRoute empty = new SingleRoute(emptyEdgesList);
        });
    }

    @Test
    void constructorWorksWithArrayOfOne() {
        var expected = 1;
        var actual = new SingleRoute(edgeListOfOne).length();
        System.out.println(actual);
    }

    @Test
    void constructorInitializesCorrectArray() {
        List<Edge> exEdges = new ArrayList<>();
        exEdges.add(exEdge0);
        exEdges.add(exEdge1);
        exEdges.add(exEdge2);
        exEdges.add(exEdge3);
        exEdges.add(exEdge4);

        SingleRoute sr = new SingleRoute(exEdges);
        var expected0 = exArray[0];
        var actual0 = sr.length() - 13100;
        var expected1 = exArray[1];
        var actual1 = sr.length() - 2300 - 1100 - 2200 - 1700;
        var expected2 = exArray[2];
        var actual2 = sr.length() - 1100 - 2200 - 1700;
        var expected3 = exArray[3];
        var actual3 = sr.length() - 2200 - 1700;
        var expected4 = exArray[4];
        var actual4 = sr.length() - 1700;
        var expected5 = 13100;
        var actual5 = sr.length();

        assertEquals(expected0, actual0);
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
        assertEquals(expected3, actual3);
        assertEquals(expected4, actual4);
        assertEquals(expected5, actual5);
    }

    @Test
    void indexOfSegmentAtWorksOnKnownValues() {
        List<Edge> exEdges = new ArrayList<>();
        exEdges.add(exEdge0);
        exEdges.add(exEdge1);
        exEdges.add(exEdge2);
        exEdges.add(exEdge3);
        exEdges.add(exEdge4);

        SingleRoute sr = new SingleRoute(exEdges);
        var expected0 = 0;
        var actual0 = sr.indexOfSegmentAt(10);
        var expected1 = 0;
        var actual1 = sr.indexOfSegmentAt(5801);
        var expected2 = 0;
        var actual2 = sr.indexOfSegmentAt(8100);
        var expected3 = 0;
        var actual3 = sr.indexOfSegmentAt(9300);
        var actual4 = 0;
        var expected4 = sr.indexOfSegmentAt(14069);

        assertEquals(expected0, actual0);
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
        assertEquals(expected3, actual3);
        assertEquals(expected4, actual4);
    }

    @Test
    void indexOfSegmentAtRandomized() {
        var rng = newRandom();
        List<Edge> exEdges = new ArrayList<>();
        exEdges.add(exEdge0);
        exEdges.add(exEdge1);
        exEdges.add(exEdge2);
        exEdges.add(exEdge3);
        exEdges.add(exEdge4);

        SingleRoute sr = new SingleRoute(exEdges);
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            var actual = sr.indexOfSegmentAt(rng.nextDouble(-100000, 100000));
            var expected = 0;
            assertEquals(expected, actual);
        }
    }

    @Test
    void lengthWorksOnKnownValue() {
        List<Edge> exEdges = new ArrayList<>();
        exEdges.add(exEdge0);
        exEdges.add(exEdge1);
        exEdges.add(exEdge2);
        exEdges.add(exEdge3);
        exEdges.add(exEdge4);

        SingleRoute sr = new SingleRoute(exEdges);
        var expected = 13100;
        var actual = sr.length();
        assertEquals(expected, actual);
    }

    @RepeatedTest(5)
    void lengthWorksRandomized() {
        var rng = newRandom();
        List<Edge> exEdges = new ArrayList<>();
        exEdges.add(exEdge0);
        exEdges.add(exEdge1);
        exEdges.add(exEdge2);
        exEdges.add(exEdge3);
        exEdges.add(exEdge4);

        var rdmLength = newRandom().nextDouble(13100.0, Double.MAX_VALUE);
        Edge randomEdge = new Edge(0, 5, point0, point5, rdmLength, profile);
        exEdges.add(randomEdge);
        SingleRoute sr = new SingleRoute(exEdges);

        var expected = rdmLength;
        var actual = sr.length();
        assertEquals(expected, actual);
    }

    @Test
    void edgesWorksOnKnownData() {
        List<Edge> exEdges = new ArrayList<>();
        exEdges.add(exEdge0);
        exEdges.add(exEdge1);
        exEdges.add(exEdge2);
        exEdges.add(exEdge3);
        exEdges.add(exEdge4);

        SingleRoute sr = new SingleRoute(exEdges);
        var expected = exEdges;
        var actual = sr.edges();
        assertEquals(expected, actual);
    }

    @Test
    void pointsWorkOnKnownData() {
        List<Edge> exEdges = new ArrayList<>();
        exEdges.add(exEdge0);
        exEdges.add(exEdge1);
        exEdges.add(exEdge2);
        exEdges.add(exEdge3);
        exEdges.add(exEdge4);

        SingleRoute sr = new SingleRoute(exEdges);
        var expected = new ArrayList<PointCh>();
        expected.add(point0);
        expected.add(point1);
        expected.add(point2);
        expected.add(point3);
        expected.add(point4);
        expected.add(point5);
        var actual = sr.points();

        assertEquals(expected, actual);
    }

    @Test
    void pointAtWorksOnKnownData() {
        List<Edge> exEdges = new ArrayList<>();
        exEdges.add(exEdge0);
        exEdges.add(exEdge1);
        exEdges.add(exEdge2);
        exEdges.add(exEdge3);
        exEdges.add(exEdge4);

        SingleRoute sr = new SingleRoute(exEdges);

        var expected0 = point0;
        var actual0 = sr.pointAt(0);
        // System.out.println("expected0: " + expected0);
        // System.out.println("actual0: " + actual0);
        assertEquals(expected0, actual0);

        var expected1 = point1;
        var actual1 = sr.pointAt(5800.0);
        // System.out.println("expected1: " + expected1);
        // System.out.println("actual1: " + actual1);
        assertEquals(expected1, actual1);

        var expected2 = point2;
        var actual2 = sr.pointAt(8100.0);
        // System.out.println("expected2: " + expected2);
        // System.out.println("actual2: " + actual2);
        assertEquals(expected2, actual2);

        var expected3 = point3;
        var actual3 = sr.pointAt(9200.0);
        // System.out.println("expected3: " + expected3);
        // System.out.println("actual3: " + actual3);
        assertEquals(expected3, actual3);

        var expected4 = point4;
        var actual4 = sr.pointAt(11400);
        // System.out.println("expected4: " + expected4);
        // System.out.println("actual4: " + actual4);
        assertEquals(expected4, actual4);

        var expected5 = point5;
        var actual5 = sr.pointAt(100000);
        System.out.println("expected5: " + expected5);
        System.out.println("actual5: " + actual5);
        assertEquals(expected5, actual5);
    }

    @Test
    void elevationAtWorksOnKnownData() {
        List<Edge> exEdges = new ArrayList<>();
        exEdges.add(exEdge0);
        exEdges.add(exEdge1);
        exEdges.add(exEdge2);
        exEdges.add(exEdge3);
        exEdges.add(exEdge4);

        SingleRoute sr = new SingleRoute(exEdges);

        // Testing extremes of the array.
        var expected0 = samples[0];
        var actual0 = sr.elevationAt(0);
        assertEquals(expected0, actual0);

        var expected5 = samples[samples.length - 1];
        var actual5 = sr.elevationAt(131000);
        assertEquals(expected5, actual5);

        // Creating specific ones for this test
        float[] eleSamples0 = new float[]{22, 10, 5, 12, 5, 6};
        float[] eleSamples1 = new float[]{6, 7, 8, 9, 10, 11, 12};
        float[] eleSamples2 = new float[]{12, 8, 4, 2, -10, 15, Float.NaN};
        float[] eleSamples3 = new float[]{1, 2, 3, 4, 5, 6};
        float[] eleSamples4 = new float[]{1, 10, 100, 1000, 1e4F, 1e5F, 1e6F};
        DoubleUnaryOperator profile0 = Functions.sampled(eleSamples0, 5800);
        DoubleUnaryOperator profile1 = Functions.sampled(eleSamples1, 2300);
        DoubleUnaryOperator profile2 = Functions.sampled(eleSamples2, 1100);
        DoubleUnaryOperator profile3 = Functions.sampled(eleSamples3, 2022);
        DoubleUnaryOperator profile4 = Functions.sampled(eleSamples4, 1700);
        Edge eleEdge0 = new Edge(0, 1, point0, point1, 5800, profile0);
        Edge eleEdge1 = new Edge(1, 2, point1, point2, 2300, profile1);
        Edge eleEdge2 = new Edge(2, 3, point2, point3, 1100, profile2);
        Edge eleEdge3 = new Edge(3, 4, point3, point4, 2200, profile3);
        Edge eleEdge4 = new Edge(4, 5, point4, point5, 1700, profile4);
        List<Edge> eleEdges = new ArrayList<>();
        eleEdges.add(eleEdge0);
        eleEdges.add(eleEdge1);
        eleEdges.add(eleEdge2);
        eleEdges.add(eleEdge3);
        eleEdges.add(eleEdge4);
        SingleRoute eleSr = new SingleRoute(eleEdges);

        // Printing of data
        boolean print = false;
        if (print) {
            for (int i = 0; i < eleSr.length(); ++i) {
                System.out.println("Elevation at position " + i + ": " + eleSr.elevationAt(i));
            }
        }

        // Testing middle of the array
        var expected1 = 6.;
        var actual1 = eleSr.elevationAt(5800);
        var expected2 = 12.;
        var actual2 = eleSr.elevationAt(8100);
        var expected3 = 1.;
        var actual3 = eleSr.elevationAt(9200);
        var expected4 = 1.;
        var actual4 = eleSr.elevationAt(11400);

        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
        assertEquals(expected3, actual3);
        assertEquals(expected4, actual4);
    }

    @Test
    void nodeClosestToWorksOnKnownData() {
        List<Edge> exEdges = new ArrayList<>();
        exEdges.add(exEdge0);
        exEdges.add(exEdge1);
        exEdges.add(exEdge2);
        exEdges.add(exEdge3);
        exEdges.add(exEdge4);

        SingleRoute sr = new SingleRoute(exEdges);
        var expected0 = 0;
        var actual0 = sr.nodeClosestTo(0);
        assertEquals(expected0, actual0);

        var expected1 = 1;
        var actual1 = sr.nodeClosestTo(5800);
        assertEquals(expected1, actual1);

        var expected2 = 2;
        var actual2 = sr.nodeClosestTo(8100);
        assertEquals(expected2, actual2);

        var expected3 = 3;
        var actual3 = sr.nodeClosestTo(9200);
        assertEquals(expected3, actual3);

        var expected4 = 4;
        var actual4 = sr.nodeClosestTo(11400);
        assertEquals(expected4, actual4);

        var expected5 = 5;
        var actual5 = sr.nodeClosestTo(131000);
        assertEquals(expected5, actual5);
    }

    @Test
    void pointClosestToWorksOnKnownData() {
        // Creating specific ones for this test
        float[] eleSamples0 = new float[]{22, 10, 5, 12, 5, 6};
        float[] eleSamples1 = new float[]{6, 7, 8, 9, 10, 11, 12};
        float[] eleSamples2 = new float[]{12, 8, 4, 2, -10, 15, Float.NaN};
        float[] eleSamples3 = new float[]{1, 2, 3, 4, 5, 6};
        float[] eleSamples4 = new float[]{1, 10, 100, 1000, 1e4F, 1e5F, 1e6F};
        DoubleUnaryOperator profile0 = Functions.sampled(eleSamples0, 5800);
        DoubleUnaryOperator profile1 = Functions.sampled(eleSamples1, 2300);
        DoubleUnaryOperator profile2 = Functions.sampled(eleSamples2, 1100);
        DoubleUnaryOperator profile3 = Functions.sampled(eleSamples3, 2022);
        DoubleUnaryOperator profile4 = Functions.sampled(eleSamples4, 1700);
        Edge eleEdge0 = new Edge(0, 1, point0, point1, 5800, profile0);
        Edge eleEdge1 = new Edge(1, 2, point1, point2, 2300, profile1);
        Edge eleEdge2 = new Edge(2, 3, point2, point3, 1100, profile2);
        Edge eleEdge3 = new Edge(3, 4, point3, point4, 2200, profile3);
        Edge eleEdge4 = new Edge(4, 5, point4, point5, 1700, profile4);
        Edge eleEdge5 = new Edge(5, 0, point5, point0, 1700, profile0);
        List<Edge> eleEdges = new ArrayList<>();
        eleEdges.add(eleEdge0);
        eleEdges.add(eleEdge1);
        eleEdges.add(eleEdge2);
        eleEdges.add(eleEdge3);
        eleEdges.add(eleEdge4);
        //eleEdges.add(eleEdge5);
        SingleRoute eleSr = new SingleRoute(eleEdges);

        // Printing data
        boolean print = true;
        boolean points = true;
        if (print) {
            if (points) {
                System.out.println("point0: " + point0);
                System.out.println("point1: " + point1);
                System.out.println("point2: " + point2);
                System.out.println("point3: " + point3);
                System.out.println("point4: " + point4);
                System.out.println("point5: " + point5);
            }
        }

        // Before the route.
        PointCh rightOfPoint0 = new PointCh(2549278.75, 1166252.3125);
        var actual0 = eleSr.pointClosestTo(rightOfPoint0);
        var expected0 = new RoutePoint(point0, 0, 0);
        assertEquals(expected0, actual0);

        // End of the route.
        PointCh underPoint5 = new PointCh(2549396.75, 1165999);
        var actual5 = eleSr.pointClosestTo(underPoint5);
        var expected5 = new RoutePoint(point5, 0, 0.1875);

        /**
         * LORIC ET ADRIAN, chez vous aussi รงa marche pas ?
         */
//        assertEquals(expected5.point().e(), actual5.point().e(), 1e-1);
//        assertEquals(expected5.point().n(), actual5.point().n(), 1e-1);
//        assertEquals(expected5.distanceToReference(), actual5.distanceToReference());
        /**
         * FIN du commentaire.
         */
/*
        // Middle of route
        var actual2 = eleSr.pointClosestTo(new PointCh(2549151, 1166107.375));
        var expected2 = new RoutePoint(point2, 0, 2549151 - 2549150.375);
        assertEquals(expected2.point().e(), actual2.point().e(), 1e-2);
        assertEquals(expected2.point().n(), actual2.point().n(), 1e-2);
        assertEquals(expected2.distanceToReference(), actual2.distanceToReference());

        var actual4 = eleSr.pointClosestTo(new PointCh(2549418.0625, 1166011.25));
        var expected4 = new RoutePoint(point4, 0, 2);
        assertEquals(expected4.point().e(), actual4.point().e(), 1e-1);
        assertEquals(expected4.point().n(), actual4.point().n(), 1e-1);
        assertEquals(expected4.distanceToReference(), actual4.distanceToReference());

        //assertEquals(expected1, actual1);
        //assertEquals(expected1, actual11);
        //System.out.println("actual1: " + actual11);
        //System.out.println("expected1: " + expected1);
        //System.out.println(point1 + "" + point2);
    }

}*/
