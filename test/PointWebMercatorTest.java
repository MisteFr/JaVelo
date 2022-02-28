import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class PointWebMercatorTest {

    @Test
    void PointWebMercatorTest(){
        assertThrows(IllegalArgumentException.class, () -> {
            PointWebMercator a = new PointWebMercator(3, 0.5);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            PointWebMercator a = new PointWebMercator(0.2, 6);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            PointWebMercator a = new PointWebMercator(5, 5);
        });

        PointWebMercator a = new PointWebMercator(1, 1);
    }

    @Test
    void ofTest(){
        assertEquals((new PointWebMercator(0.09765625,0).x()), PointWebMercator.of(0, 25, 0).x());
    }

    @Test
    void xAtZoomLevelTest(){
        assertEquals(25, (new PointWebMercator(0.09765625,0)).xAtZoomLevel(0));
        assertEquals(1600, (new PointWebMercator(0.09765625,0)).xAtZoomLevel(6));
        assertEquals(56623104, (new PointWebMercator(0.2109375,0)).xAtZoomLevel(20));
    }

    @Test
    void yAtZoomLevelTest(){
        assertEquals(25, (new PointWebMercator(0, 0.09765625)).yAtZoomLevel(0));
        assertEquals(1600, (new PointWebMercator(0, 0.09765625)).yAtZoomLevel(6));
        assertEquals(56623104, (new PointWebMercator(0, 0.2109375)).yAtZoomLevel(20));
    }

    @Test
    void ofPointChTest(){
        PointCh a = new PointCh(2695000,1175000);
        assertTrue(new PointWebMercator((WebMercator.x(Ch1903.lon(2695000, 1175000))), WebMercator.y(Ch1903.lat(2695000, 1175000))).equals(PointWebMercator.ofPointCh(a)));
    }

    @Test
    void lonTest(){
        PointWebMercator a = new PointWebMercator(0.09765625,0);
        assertEquals(WebMercator.lon(0.09765625), a.lon());
    }

    @Test
    void latTest(){
        PointWebMercator a = new PointWebMercator(0,0.09765625);
        assertEquals(WebMercator.lat(0.09765625), a.lat());
    }

    @Test
    void toPointChTest(){
        PointWebMercator b = new PointWebMercator(0.3, 0.6);
        assertTrue(b.toPointCh() == null);
    }
}