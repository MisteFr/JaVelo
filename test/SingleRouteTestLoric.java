
import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.SingleRoute;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

public class SingleRouteTestLoric {

    @Test
    void SingleRouteThrowsErrorOnEmptyEdges(){

        List<Edge> edges = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> {
            SingleRoute sg = new SingleRoute(edges);
        });

    }

    @Test
    void SingleRouteCreationWorking(){

        List<Edge> edges = new ArrayList<Edge>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};
        Edge edge1 = new Edge(0,1, new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N), 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N), 2300, Functions.sampled(typicalYasuoPlayer,2300));
        Edge edge3 = new Edge(2,3, new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N), 1100, Functions.sampled(typicalYasuoPlayer,1100));
        Edge edge4 = new Edge(3,4, new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N), 2200, Functions.sampled(typicalYasuoPlayer,2200));
        Edge edge5 = new Edge(4,5, new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-13100,SwissBounds.MIN_N), 1700, Functions.sampled(typicalYasuoPlayer,1700));

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        SingleRoute sr = new SingleRoute(edges);

    }

    @Test
    void SingleRouteNodesPositionWorking(){

        List<Edge> edges = new ArrayList<Edge>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};
        Edge edge1 = new Edge(0,1, new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N), 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N), 2300, Functions.sampled(typicalYasuoPlayer,2300));
        Edge edge3 = new Edge(2,3, new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N), 1100, Functions.sampled(typicalYasuoPlayer,1100));
        Edge edge4 = new Edge(3,4, new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N), 2200, Functions.sampled(typicalYasuoPlayer,2200));
        Edge edge5 = new Edge(4,5, new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-13100,SwissBounds.MIN_N), 1700, Functions.sampled(typicalYasuoPlayer,1700));

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        SingleRoute sr = new SingleRoute(edges);

        double[] nodesPositions = {0,5800,8100,9200,11400,13100};

        for (int i = 0; i < nodesPositions.length; i++) {
           // assertEquals(nodesPositions[i],sr.nodesPositions()[i]);
        }

    }

    @Test
    void SingleRouteIndexOfPositionAtWorkingAnyValues(){

        List<Edge> edges = new ArrayList<Edge>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};
        Edge edge1 = new Edge(0,1, new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N), 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N), 2300, Functions.sampled(typicalYasuoPlayer,2300));
        Edge edge3 = new Edge(2,3, new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N), 1100, Functions.sampled(typicalYasuoPlayer,1100));
        Edge edge4 = new Edge(3,4, new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N), 2200, Functions.sampled(typicalYasuoPlayer,2200));
        Edge edge5 = new Edge(4,5, new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-13100,SwissBounds.MIN_N), 1700, Functions.sampled(typicalYasuoPlayer,1700));

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        SingleRoute sr = new SingleRoute(edges);

        assertEquals(0,sr.indexOfSegmentAt(-12500));
        assertEquals(0,sr.indexOfSegmentAt(5799));
        assertEquals(1,sr.indexOfSegmentAt(5801));
        assertEquals(3,sr.indexOfSegmentAt(9400));
        assertEquals(1,sr.indexOfSegmentAt(5801));
        assertEquals(4,sr.indexOfSegmentAt(13000));
        assertEquals(4,sr.indexOfSegmentAt(15000));

    }

    @Test
    void SingleRouteLengthWorking(){

        List<Edge> edges = new ArrayList<Edge>();

        List<Edge> singleEdge = new ArrayList<Edge>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};
        Edge edge1 = new Edge(0,1, new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N), 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N), 2300, Functions.sampled(typicalYasuoPlayer,2300));
        Edge edge3 = new Edge(2,3, new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N), 1100, Functions.sampled(typicalYasuoPlayer,1100));
        Edge edge4 = new Edge(3,4, new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N), 2200, Functions.sampled(typicalYasuoPlayer,2200));
        Edge edge5 = new Edge(4,5, new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-13100,SwissBounds.MIN_N), 1700, Functions.sampled(typicalYasuoPlayer,1700));

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        singleEdge.add(edge1);

        SingleRoute sr = new SingleRoute(edges);

        SingleRoute sr2 = new SingleRoute(singleEdge);

        assertEquals(13100,sr.length());
        assertEquals(5800,sr2.length());

    }

    @Test
    void SingleRouteGetterWorking(){

        List<Edge> edges = new ArrayList<Edge>();
        List<PointCh> points = new ArrayList<PointCh>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};

        PointCh pt1 = new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N);
        PointCh pt2 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt3 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt4 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt5 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt6 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt7 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt8 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt9 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt10 = new PointCh(SwissBounds.MAX_E-13100,SwissBounds.MIN_N);

        Edge edge1 = new Edge(0,1, pt1,pt2, 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, pt3, pt4, 2300, Functions.sampled(typicalYasuoPlayer,2300));
        Edge edge3 = new Edge(2,3, pt5, pt6, 1100, Functions.sampled(typicalYasuoPlayer,1100));
        Edge edge4 = new Edge(3,4, pt7, pt8, 2200, Functions.sampled(typicalYasuoPlayer,2200));
        Edge edge5 = new Edge(4,5, pt9, pt10, 1700, Functions.sampled(typicalYasuoPlayer,1700));

        points.add(pt1);
        points.add(pt2);
        points.add(pt4);
        points.add(pt6);
        points.add(pt8);
        points.add(pt10);

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        SingleRoute sr = new SingleRoute(edges);

        assertTrue(edges.equals(sr.edges()));

        assertTrue(points.equals(sr.points()));

    }

    @Test
    void SinglePointAtWorking(){

        List<Edge> edges = new ArrayList<Edge>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};
        Edge edge1 = new Edge(0,1, new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N), 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N), 2300, Functions.sampled(typicalYasuoPlayer,2300));
        Edge edge3 = new Edge(2,3, new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N), 1100, Functions.sampled(typicalYasuoPlayer,1100));
        Edge edge4 = new Edge(3,4, new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N), 2200, Functions.sampled(typicalYasuoPlayer,2200));
        Edge edge5 = new Edge(4,5, new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-13100,SwissBounds.MIN_N), 1700, Functions.sampled(typicalYasuoPlayer,1700));

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        SingleRoute sr = new SingleRoute(edges);

        PointCh expectedPoint1 = edge1.pointAt(5700);
        PointCh expectedPoint2 = edge2.pointAt(100);
        PointCh expectedPoint3 = edge3.pointAt(100);
        PointCh expectedPoint4 = edge5.pointAt(1700);
        PointCh expectedPoint5 = edge1.pointAt(0);

        assertEquals(expectedPoint1,sr.pointAt(5700));
        assertEquals(expectedPoint2,sr.pointAt(5900));
        assertEquals(expectedPoint3,sr.pointAt(8200));
        assertEquals(expectedPoint4,sr.pointAt(13100));
        assertEquals(expectedPoint4,sr.pointAt(165445));
        assertEquals(expectedPoint5,sr.pointAt(-154645));

    }

    @Test
    void SingleNodeClosestToWorking(){

        List<Edge> edges = new ArrayList<Edge>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};
        Edge edge1 = new Edge(0,1, new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N), 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N), 2300, Functions.sampled(typicalYasuoPlayer,2300));
        Edge edge3 = new Edge(2,3, new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N), 1100, Functions.sampled(typicalYasuoPlayer,1100));
        Edge edge4 = new Edge(3,4, new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N), 2200, Functions.sampled(typicalYasuoPlayer,2200));
        Edge edge5 = new Edge(4,5, new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-13100,SwissBounds.MIN_N), 1700, Functions.sampled(typicalYasuoPlayer,1700));

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        SingleRoute sr = new SingleRoute(edges);

        assertEquals(1,sr.nodeClosestTo(5700));
        assertEquals(1,sr.nodeClosestTo(5900));
        assertEquals(0,sr.nodeClosestTo(-455));
        assertEquals(0,sr.nodeClosestTo(2900));
        assertEquals(4,sr.nodeClosestTo(11400));
        assertEquals(3,sr.nodeClosestTo(9500));

    }

    @Test
    void SingleElevationAtWorking(){

        List<Edge> edges = new ArrayList<Edge>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};
        Edge edge1 = new Edge(0,1, new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N), 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N), 2300, Functions.sampled(typicalYasuoPlayer,2300));
        Edge edge3 = new Edge(2,3, new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N), 1100, Functions.sampled(typicalYasuoPlayer,1100));
        Edge edge4 = new Edge(3,4, new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N), 2200, Functions.sampled(typicalYasuoPlayer,2200));
        Edge edge5 = new Edge(4,5, new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-13100,SwissBounds.MIN_N), 1700, Functions.sampled(typicalYasuoPlayer,1700));

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        SingleRoute sr = new SingleRoute(edges);

        assertEquals(edge1.elevationAt(2500),sr.elevationAt(2500));
        assertEquals(edge2.elevationAt(100),sr.elevationAt(5900));
        assertEquals(edge5.elevationAt(300),sr.elevationAt(11700));
        assertEquals(edge1.elevationAt(0),sr.elevationAt(-154));
        assertEquals(edge5.elevationAt(1700),sr.elevationAt(15656));

    }

    @Test
    void SingleElevationAtSendNaN(){

        List<Edge> edges = new ArrayList<Edge>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};
        float[] verreDeauPapillonant = {Float.NaN,Float.NaN};

        Edge edge1 = new Edge(0,1, new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N), 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N),new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N), 2300, Functions.sampled(verreDeauPapillonant,14));

        edges.add(edge1);
        edges.add(edge2);

        SingleRoute sr = new SingleRoute(edges);

        assertEquals(edge1.elevationAt(2500),sr.elevationAt(2500));
        assertEquals(Double.NaN,sr.elevationAt(5900));

    }


}
