import ch.epfl.javelo.projection.WebMercator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebMercatorTest {

    @Test
    void testConversionCheneNapoleon() {
        assertEquals(6.5790772, Math.toDegrees(WebMercator.lon(0.518275214444)), 1e-7);
    }

    @Test
    void testConversionCheneNapoleon2() {
        assertEquals(46.5218976, Math.toDegrees(WebMercator.lat(0.353664894749)), 1e-7);
    }

    @Test
    void testConversionCheneNapoleon3() {
        assertEquals(0.518275214444, WebMercator.x(Math.toRadians(6.5790772)), 1e-7);
    }

    @Test
    void testConversionCheneNapoleon4() {
        assertEquals(0.353664894749, WebMercator.y(Math.toRadians(46.5218976)), 1e-7);
    }
}
