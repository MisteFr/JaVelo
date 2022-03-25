
import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;
import ch.epfl.javelo.routing.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;
import ch.epfl.javelo.routing.ElevationProfileComputer;


public class ElevationProfileComputerTestAlice {

    @Test
    void elevationProfileExceptionTest(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 100;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, 0);
        } );

        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, -1);
        } );
    }

    @Test
    void OnlyNaNTest(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 100;
        float[] samples = {Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile NaNProfile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(0, NaNProfile.minElevation());
        assertEquals(0, NaNProfile.maxElevation());
        assertEquals(0, NaNProfile.totalAscent());
        assertEquals(0, NaNProfile.totalDescent());
    }

    @Test
    void BeginingNaNTest(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 100;
        float[] samples = {Float.NaN, Float.NaN, Float.NaN, 500, 502, 505, 510, 500, 520, 510};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(500.54544, profile.minElevation(), 0.00001);
    }

    @Test
    void EndNaNTest(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 100;
        float[] samples = {500, 502, 505, 510, 500, 520, 510, Float.NaN, Float.NaN, Float.NaN, };
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(500, profile.minElevation());
    }

    @Test
    void MiddleNaNTest(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 100;
        float[] samples = {500, 500, 500, 500, 500, Float.NaN, Float.NaN, Float.NaN, 500, 500 };
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(500, profile.minElevation());

    }

    @Test
    void multipleTunnelsTest(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 100;
        float[] samples = {Float.NaN, Float.NaN, 500, 500, Float.NaN, Float.NaN, Float.NaN, 500, 500, 500, Float.NaN, Float.NaN, Float.NaN, 500, 500, Float.NaN, Float.NaN };
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(500, profile.minElevation());

    }
}
