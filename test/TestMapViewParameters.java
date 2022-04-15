import ch.epfl.javelo.gui.MapViewParameters;
import ch.epfl.javelo.gui.TileManager;
import ch.epfl.javelo.projection.PointWebMercator;
import org.junit.jupiter.api.Test;

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
}
