
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.ElevationProfileComputer;
import ch.epfl.javelo.routing.SingleRoute;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyElevationProfileComputerTest {
    PointCh A = new PointCh(2600123, 1200456);
    PointCh B = new PointCh(2600456, 1200789);
    PointCh C = new PointCh(2600789, 1200123);
    PointCh D = new PointCh(2601000, 1201000);
    PointCh E = new PointCh(2601283, 1201110);
    PointCh F = new PointCh(2602000, 1201999);
    PointCh G = new PointCh(2602500, 1201010);
    PointCh H = new PointCh(2602877, 1200829);
    PointCh I = new PointCh(2603000, 1201086);
    PointCh J = new PointCh(2603124, 1198878);

    Edge edge1 = new Edge(1, 2, A, B, A.distanceTo(B), x -> 4);
    Edge edge2 = new Edge(3, 4, B, C, B.distanceTo(C), x -> Double.NaN);
    Edge edge3 = new Edge(4, 5, C, D, C.distanceTo(D), x -> 6.);
    Edge edge4 = new Edge(5, 6, D, E, D.distanceTo(E), x -> 9.5);
    Edge edge5 = new Edge(7, 8, E, F, E.distanceTo(F), x -> Double.NaN);
    Edge edge6 = new Edge(9, 10, F, G, F.distanceTo(G), x -> Double.NaN);
    Edge edge7 = new Edge(10, 11, G, H, G.distanceTo(H), x -> 15);
    Edge edge8 = new Edge(11, 12, H, I, H.distanceTo(I), x -> 283.2987492);
    Edge edge9 = new Edge(12, 13, I, J, I.distanceTo(J), x -> Double.NaN);

    public SingleRoute ourRoute() {

        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);edges.add(edge2);edges.add(edge3);edges.add(edge4);
        edges.add(edge5);edges.add(edge6);edges.add(edge7);edges.add(edge8);edges.add(edge9);

        return new SingleRoute(edges);
    }

    @Test
    void test1(){
        float[] tab = { 4f, 5f, 6f, 9.5f, 11.5f, 13f, 15, 283.2987492f, 283.2987492f };

        ElevationProfile expected = new ElevationProfile(ourRoute().length(), tab );

        assertEquals(expected, ElevationProfileComputer.elevationProfile(ourRoute(), ourRoute().length()/tab.length));
    }



    /*@Test
    void testAlex(){
        float[] tab = { 4f, 5f, 6f, 9.5f, 11.5f, 13f, 15, 283.2987492f, 283.2987492f };

        ElevationProfile expected = new ElevationProfile(ourRoute().length(), tab );

        assertEquals(expected, ASupp.elevationProfile(ourRoute(), ourRoute().length()/tab.length));
    }*/

}