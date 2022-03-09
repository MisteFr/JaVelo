import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class GraphTest {

    @Test
    void testLoadFrom() {
        try{
            Graph a = Graph.loadFrom(Path.of("lausanne"));
            PointCh p = a.nodePoint(310876657);
            System.out.println(p.e() + " - " + p.n());
        }catch (Exception e){

        }
    }
}
