import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.RoutePoint;
import ch.epfl.javelo.routing.SingleRoute;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SingleRouteTest {

    @Test
    void testInitializeSingleRoute(){
        List<Edge> l = new ArrayList<Edge>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        l.add(edge1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab2 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab, 5);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 5, profile2);

        l.add(edge2);

        SingleRoute s = new SingleRoute(l);

        assertThrows(IllegalArgumentException.class, () -> {
            new SingleRoute(new ArrayList<Edge>());
        });
    }

    @Test
    void testLengthAndEdges(){
        List<Edge> l = new ArrayList<Edge>();
        List<PointCh> pointsList = new ArrayList<PointCh>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        //on ajoute pas le toPoint car l'edge2 fromPoint est ce point
        pointsList.add(fromPoint);
        l.add(edge1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab2 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab, 5);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 5, profile2);

        l.add(edge2);

        pointsList.add(fromPoint2);
        pointsList.add(toPoint2);

        SingleRoute s = new SingleRoute(l);
        assertEquals(10, s.length());
        assertEquals(l, s.edges());
        assertEquals(pointsList, s.points());


        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 15, SwissBounds.MIN_N);
        float[] profileTab3 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile3 = Functions.sampled(profileTab, 5);
        Edge edge3 = new Edge(2, 1, fromPoint3, toPoint3, 5, profile3);

        l.add(edge3);
        //le fromPoint a deja été ajouté du coup (endPoint de l'edge 2)
        //pointsList.add(fromPoint3);
        pointsList.add(toPoint3);

        //length of s shouldn't have changed even if l was modified
        assertEquals(10, s.length());

        SingleRoute s2 = new SingleRoute(l);
        assertEquals(15, s2.length());
        assertEquals(l, s2.edges());
        assertEquals(pointsList, s2.points());


        //Test pointAt()

        for(int i = 0; i < 50; i++){
            assertEquals(new PointCh(SwissBounds.MIN_E + i, SwissBounds.MIN_N), s2.pointAt(i));
        }

        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), s2.pointAt(-2));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), s2.pointAt(-1));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), s2.pointAt(0));
    }


    @Test
    void testElevationAt(){
        List<Edge> l = new ArrayList<Edge>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 515f, 520f, 525f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        l.add(edge1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab2 = {525f, 530f, 535f, 540f, 545f, 550f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 5, profile2);

        l.add(edge2);

        SingleRoute s = new SingleRoute(l);

        //TODO: quand plus grand que la length

        for(int i = 0; i < 10; i++){
            assertEquals(500 + 5*i, s.elevationAt(i));
        }

        assertEquals(500, s.elevationAt(0));
        assertEquals(500, s.elevationAt(-2));
        assertEquals(500, s.elevationAt(-10));
        assertEquals(550, s.elevationAt(10));
        assertEquals(550, s.elevationAt(11));


        assertEquals(550, s.elevationAt(14));
    }

    @Test
    void testNodeClosestTo(){
        List<Edge> l = new ArrayList<Edge>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 515f, 520f, 525f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        l.add(edge1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab2 = {525f, 530f, 535f, 540f, 545f, 550f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 5, profile2);

        l.add(edge2);

        SingleRoute s = new SingleRoute(l);

        assertEquals(2, s.nodeClosestTo(10));
        assertEquals(2, s.nodeClosestTo(10.1));
        assertEquals(2, s.nodeClosestTo(12));
        assertEquals(2, s.nodeClosestTo(20));
        assertEquals(2, s.nodeClosestTo(100));

        for(double i = 0; i < 10.0; i += 0.1){
            //System.out.println("i=" + i + " closest=" + s.nodeClosestTo(i));
            if(i <= 2.5){
                assertEquals(0, s.nodeClosestTo(i));
            }else if(i <= 7.5){
                assertEquals(1, s.nodeClosestTo(i));
            }else{
                assertEquals(2, s.nodeClosestTo(i));
            }
        }

        //according to the assistant, when we are at half way we should chose left node
        assertEquals(0, s.nodeClosestTo(2.5));
        assertEquals(1, s.nodeClosestTo(7.5));

        assertEquals(0, s.nodeClosestTo(-1));
        assertEquals(0, s.nodeClosestTo(0));
        assertEquals(0, s.nodeClosestTo(-6));
    }

    @Test
    void testNearestPoint(){
        List<Edge> l = new ArrayList<Edge>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 515f, 520f, 525f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        l.add(edge1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab2 = {525f, 530f, 535f, 540f, 545f, 550f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 5, profile2);

        l.add(edge2);

        SingleRoute s = new SingleRoute(l);

        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N), 1, 1), s.pointClosestTo(new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N + 1)));

        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 2, SwissBounds.MIN_N), 2, 10), s.pointClosestTo(new PointCh(SwissBounds.MIN_E + 2, SwissBounds.MIN_N + 10)));

        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10, 1), s.pointClosestTo(new PointCh(SwissBounds.MIN_E + 12, SwissBounds.MIN_N + 10)));
    }
}
