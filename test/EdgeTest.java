import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.*;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.Edge;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {


    @Test
    void testPositionClosestTo() {
        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        assertEquals(3, edge.positionClosestTo(new PointCh(SwissBounds.MIN_E + 3, SwissBounds.MIN_N + 2)));
        assertEquals(7, edge.positionClosestTo(new PointCh(SwissBounds.MIN_E + 7, SwissBounds.MIN_N + 2)));
    }

    @Test
    void testPointAt() {
        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        assertEquals(new PointCh(SwissBounds.MIN_E + 2, SwissBounds.MIN_N), edge.pointAt(3));
        assertEquals(new PointCh(SwissBounds.MIN_E + 7, SwissBounds.MIN_N), edge.pointAt(-2));
        assertEquals(new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N), edge.pointAt(0));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), edge.pointAt(5));

        //assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), edge.pointAt(10)); On aura jamais une arête aux bord du rectangle de la Suisse.
    }

    @Test
    void testElevationAt() {
        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge = new Edge(0, 1, fromPoint, toPoint, 5, profile);


        assertEquals(502f, edge.elevationAt(0));
        assertEquals(502f, edge.elevationAt(-1));
        assertEquals(500f, edge.elevationAt(1));
        assertEquals(505f, edge.elevationAt(2));
        assertEquals(510f, edge.elevationAt(3));
        assertEquals(505f, edge.elevationAt(4));
        assertEquals(507.5f, edge.elevationAt(4.5));
        assertEquals(510f, edge.elevationAt(5));
        assertEquals(510f, edge.elevationAt(6));
    }

    @Test
    void testOf() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        Edge e = Edge.of(g, 0, 0, g.edgeTargetNodeId(0));
    }
}