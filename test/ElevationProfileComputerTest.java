import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.ElevationProfileComputer;
import ch.epfl.javelo.routing.SingleRoute;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileComputerTest {



    @Test
    public void throwsExceptionsCheck(){

        PointCh from1 = new PointCh(SwissBounds.MIN_E + 1000,SwissBounds.MIN_N + 1000);
        PointCh to1 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1000);
        double length1 = 5;
        float[] samples1 = {0F, 1, 2, 3, 4, 5};
        DoubleUnaryOperator doubleUnaryOperator1 = Functions.sampled(samples1, length1);
        //TODO I assume the node id has no impact whatsoever
        Edge edge1 = new Edge(0, 1, from1, to1, length1, doubleUnaryOperator1);


        PointCh from2 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1000);
        PointCh to2 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1005);
        double length2 = 5;
        float[] samples2 = {Float.NaN, Float.NaN};
        DoubleUnaryOperator doubleUnaryOperator2 = Functions.sampled(samples2, length2);
        Edge edge2 = new Edge(0, 1, from2, to2, length2, doubleUnaryOperator2);


        PointCh from3 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1005);
        PointCh to3 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1011);
        double length3 = 6;
        float[] samples3 = {6, 6};
        DoubleUnaryOperator doubleUnaryOperator3 = Functions.sampled(samples3, length3);
        Edge edge3 = new Edge(0, 1, from3, to3, length3, doubleUnaryOperator3);

        ArrayList<Edge> e = new ArrayList<>();
        e.add(edge1);
        e.add(edge2);
        e.add(edge3);
        SingleRoute s1 = new SingleRoute(e); //s1 is a route with one tunnel in the middle.

        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile el = ElevationProfileComputer.elevationProfile(s1, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile el = ElevationProfileComputer.elevationProfile(s1, -1);
        });
    }

    @Test
    public void fillsBeginningTest(){

        PointCh from1 = new PointCh(SwissBounds.MIN_E + 1000,SwissBounds.MIN_N + 1000);
        PointCh to1 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1000);
        double length1 = 5;
        float[] samples1 = {7, 1, 2, 3, 4, 5};
        DoubleUnaryOperator doubleUnaryOperator1 = Functions.sampled(samples1, length1);
        Edge edge1 = new Edge(0, 1, from1, to1, length1, doubleUnaryOperator1);


        PointCh from2 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1000);
        PointCh to2 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1005);
        double length2 = 5;
        float[] samples2 = {Float.NaN, Float.NaN};
        DoubleUnaryOperator doubleUnaryOperator2 = Functions.sampled(samples2, length2);
        Edge edge2 = new Edge(0, 1, from2, to2, length2, doubleUnaryOperator2);


        PointCh from3 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1005);
        PointCh to3 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1011);
        double length3 = 6;
        float[] samples3 = {6, 6};
        DoubleUnaryOperator doubleUnaryOperator3 = Functions.sampled(samples3, length3);
        Edge edge3 = new Edge(0, 1, from3, to3, length3, doubleUnaryOperator3);

        ArrayList<Edge> e = new ArrayList<>();
        e.add(edge2);
        e.add(edge1);
        e.add(edge3);
        SingleRoute s2 = new SingleRoute(e); //s2 is a route with one tunnel at the beginning.

        ElevationProfile elevationTest = ElevationProfileComputer.elevationProfile(s2, 1);

        assertEquals(7,elevationTest.elevationAt(0));
        assertEquals(7,elevationTest.elevationAt(3));
        assertEquals(4,elevationTest.elevationAt(9));
        assertEquals(6,elevationTest.elevationAt(13));

    }

    @Test
    public void fillsEndTest(){

        PointCh from1 = new PointCh(SwissBounds.MIN_E + 1000,SwissBounds.MIN_N + 1000);
        PointCh to1 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1000);
        double length1 = 5;
        float[] samples1 = {0F, 1, 2, 3, 4, 5};
        DoubleUnaryOperator doubleUnaryOperator1 = Functions.sampled(samples1, length1);
        Edge edge1 = new Edge(0, 1, from1, to1, length1, doubleUnaryOperator1);


        PointCh from2 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1000);
        PointCh to2 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1005);
        double length2 = 5;
        float[] samples2 = {Float.NaN, Float.NaN};
        DoubleUnaryOperator doubleUnaryOperator2 = Functions.sampled(samples2, length2);
        Edge edge2 = new Edge(0, 1, from2, to2, length2, doubleUnaryOperator2);


        PointCh from3 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1005);
        PointCh to3 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1011);
        double length3 = 6;
        float[] samples3 = {6, 6};
        DoubleUnaryOperator doubleUnaryOperator3 = Functions.sampled(samples3, length3);
        Edge edge3 = new Edge(0, 1, from3, to3, length3, doubleUnaryOperator3);

        ArrayList<Edge> e = new ArrayList<>();
        e.add(edge1);
        e.add(edge3);
        e.add(edge2);
        SingleRoute s3 = new SingleRoute(e); //s3 is a route with one tunnel at the end.

        ElevationProfile elevationTest = ElevationProfileComputer.elevationProfile(s3, 1);

        assertEquals(6, elevationTest.elevationAt(13));
    }


    @Test
    public void fillsMiddleTest(){
        PointCh from1 = new PointCh(SwissBounds.MIN_E + 1000,SwissBounds.MIN_N + 1000);
        PointCh to1 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1000);
        double length1 = 5;
        float[] samples1 = {0F, 1, 2, 3, 4, 5};
        DoubleUnaryOperator doubleUnaryOperator1 = Functions.sampled(samples1, length1);
        Edge edge1 = new Edge(0, 1, from1, to1, length1, doubleUnaryOperator1);


        PointCh from2 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1000);
        PointCh to2 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1005);
        double length2 = 5;
        float[] samples2 = {Float.NaN, Float.NaN};
        DoubleUnaryOperator doubleUnaryOperator2 = Functions.sampled(samples2, length2);
        Edge edge2 = new Edge(0, 1, from2, to2, length2, doubleUnaryOperator2);


        PointCh from3 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1005);
        PointCh to3 = new PointCh(SwissBounds.MIN_E + 1005,SwissBounds.MIN_N + 1011);
        double length3 = 6;
        float[] samples3 = {5, 6};
        DoubleUnaryOperator doubleUnaryOperator3 = Functions.sampled(samples3, length3);
        Edge edge3 = new Edge(0, 1, from3, to3, length3, doubleUnaryOperator3);

        ArrayList<Edge> e = new ArrayList<>();
        e.add(edge1);
        e.add(edge2);
        e.add(edge3);
        SingleRoute s1 = new SingleRoute(e); //s1 is a route with one tunnel in the middle.

        ElevationProfile elevationTest = ElevationProfileComputer.elevationProfile(s1, 1);

        for (int i = 0; i < 17; i++) {
            System.out.println(elevationTest.elevationAt(i));

        }/*
        assertEquals(4.166666507720947, elevationTest.elevationAt(5));
        assertEquals(4.666666507720947, elevationTest.elevationAt(8));
        assertEquals(4.833333492279053, elevationTest.elevationAt(9));
        assertEquals(5.5, elevationTest.elevationAt(13));
        */
    }
}