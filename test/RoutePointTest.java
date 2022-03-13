import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.RoutePoint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoutePointTest {

    @Test
    public void withPositionShiftedByTest(){
        PointCh p = new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 1000);
        RoutePoint r = new RoutePoint(p, 900, 430);
        assertEquals(new RoutePoint(p, 1100, 430), r.withPositionShiftedBy(200));
        assertEquals(new RoutePoint(p, -100, 430), r.withPositionShiftedBy(-1000));
    }

    @Test
    public void minTest(){
        PointCh p = new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 1000); //  TODO it even works with negative distances to reference...
        RoutePoint r0 = new RoutePoint(p, 900, -100);
        RoutePoint r1 = new RoutePoint(p, 900, 100);
        RoutePoint r2 = new RoutePoint(p, 900, 200);
        RoutePoint r3 = new RoutePoint(p, 900, 300);

        assertEquals(r0, r1.min(r0));
        assertEquals(r0, r0.min(r1));
        assertEquals(r2, r3.min(r2));
        assertEquals(r1, r3.min(r1));
        assertEquals(r3, r3.min(r3));
    }

    @Test
    public void minTest2(){
        PointCh p = new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 1000);
        RoutePoint r400 = new RoutePoint(p, 900, 400);
        RoutePoint r670 = new RoutePoint(p, 900, 670);

        assertEquals(r400, r670.min(p, 900, 400));
    }
}