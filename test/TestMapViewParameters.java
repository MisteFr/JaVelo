import ch.epfl.javelo.gui.MapViewParameters;
import ch.epfl.javelo.gui.TileManager;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;
import ch.epfl.test.TestRandomizer;
import javafx.geometry.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestMapViewParameters {

    @Test
    void testMapViewParameters(){
        PointWebMercator p = PointWebMercator.of(10, 135735, 92327);

        MapViewParameters mP = new MapViewParameters(10, 135735, 92327);

        assertEquals(p, mP.pointAt(0, 0));
        assertEquals(0.0, mP.viewX(p));
        assertEquals(0.0, mP.viewY(p));

        assertThrows(IllegalArgumentException.class, () -> {
            mP.pointAt(-100000000, -1000000000);
        });
    }

    @Test
    void topLeftTestWithRandomCoordinates() {
        var rng = TestRandomizer.newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var x = rng.nextDouble(1, 100000);
            var y = rng.nextDouble(1, 100000);
            MapViewParameters map = new MapViewParameters(5, x, y);
            Point2D point2D = new Point2D(x, y);
            assertEquals(point2D, map.topLeft());
        }
    }

    @Test
    void withXYonRandomXY() {
        var rng = TestRandomizer.newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var x = rng.nextDouble(1, 100000);
            var y = rng.nextDouble(1, 100000);
            var zoom = rng.nextInt(0, 19);
            MapViewParameters map = new MapViewParameters(zoom, 535, 72615);
            MapViewParameters expectedMap = new MapViewParameters(zoom, x, y);
            assertEquals(expectedMap, map.withMinXY(x, y));
        }
    }

    @Test
    void pointAtWorksOnNapoleon(){
        PointWebMercator expected = PointWebMercator.of(19, 69561722,47468099);
        MapViewParameters mapViewParameters = new MapViewParameters(19, 625.512, 2625.9);
        assertEquals(expected, mapViewParameters.pointAt(69561722, 47468099));
    }

    @Test
    void viewsWorkOnRandomValues(){
        var rng = TestRandomizer.newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var x1 = rng.nextDouble(0, 1);
            var y1 = rng.nextDouble(0, 1);
            PointWebMercator pointWebMercator = new PointWebMercator(x1, y1);
            var x2 = rng.nextDouble(0, 10000);
            var y2 = rng.nextDouble(0, 10000);
            var zoom = rng.nextInt(0, 19);
            MapViewParameters mapViewParameters = new MapViewParameters(zoom, x2, y2);
            double expectedX = pointWebMercator.xAtZoomLevel(zoom) - x2;
            double expectedY = pointWebMercator.yAtZoomLevel(zoom) - y2;
            assertEquals(expectedX, mapViewParameters.viewX(pointWebMercator));
            assertEquals(expectedY, mapViewParameters.viewY(pointWebMercator));
        }
    }
}
