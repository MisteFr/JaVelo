import ch.epfl.javelo.gui.MapViewParameters;
import ch.epfl.javelo.projection.PointWebMercator;
import org.junit.jupiter.api.Test;

public class TestMapViewParameters {

    @Test
    void testMapViewParameters(){
        PointWebMercator p = PointWebMercator.of(10, 135735, 92327);
        double lon = Math.toDegrees(p.lon()); // ~ 6.40366
        double lat = Math.toDegrees(p.lat()); // ~46.88366

        MapViewParameters mP = new MapViewParameters(10, 135735, 92327);
        System.out.println(mP.pointAt(0, 0));
        System.out.println(mP.viewX(p));
        System.out.println(mP.viewY(p));
    }
}
