import ch.epfl.javelo.data.Attribute;
import ch.epfl.javelo.data.AttributeSet;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.ElevationProfile;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class GraphTest {

    @Test
    void testLoadFrom() throws IOException {
        assertThrows(IOException.class, () -> {
            Graph.loadFrom(Path.of("lausanne2"));
        });
        Graph.loadFrom(Path.of("lausanne"));
    }

    @Test
    void testNodeCount() throws IOException{
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        //nodes count in the file
        assertEquals(212679, g.nodeCount());
    }

    @Test
    void testNodePoint() throws IOException {
        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
            //on va avoir l'identité OSM du noeud d'identité (index) 2022 dans JaVelo
            System.out.println(osmIdBuffer.get(2023));
        } catch (IOException e) {
            System.out.println(e);
        }

        Graph g = Graph.loadFrom(Path.of("lausanne"));
        //https://www.openstreetmap.org/node/310876657
        //Position expected: 46,63 / 6,60
        PointCh p = g.nodePoint(2022);

        assertEquals(46.6326106, Math.toDegrees(p.lat()), 1e-2);
        assertEquals(6.6013034, Math.toDegrees(p.lon()), 1e-2);

        //https://www.openstreetmap.org/node/310876661
        //Position expected: 46,63 / 6,60
        PointCh p2 = g.nodePoint(2023);

        assertEquals(46.6321522, Math.toDegrees(p2.lat()), 1e-2);
        assertEquals(6.6019493, Math.toDegrees(p2.lon()), 1e-2);
    }

    @Test
    void testNodeOutOfDegree() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        //https://www.openstreetmap.org/node/310876657
        //Node out of degree expected: 46,63 / 6,60

        System.out.println(g.nodeOutDegree(2022));
        System.out.println(g.nodeOutDegree(2023));
        System.out.println(g.nodeOutDegree(203));
        System.out.println(g.nodeOutDegree(2));
    }

    @Test
    void testNodeOutOfEdgeId() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        //https://www.openstreetmap.org/node/310876657

        System.out.println(g.nodeOutDegree(2022));

        assertEquals(4095, g.nodeOutEdgeId(2022, 0));
        assertEquals(4096, g.nodeOutEdgeId(2022, 1));

        assertThrows(AssertionError.class, () -> {
            //only two edges id edgeIndex are 0 and 1
            g.nodeOutEdgeId(2022, 2);
        });
    }

    @Test
    void testNodeClosestTo() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));

        //should be 2022 as we are searching at 2022 location
        assertEquals(2022, g.nodeClosestTo(g.nodePoint(2022), 10));

        //should be 2023 as we are searching at 2023 location
        assertEquals(2023, g.nodeClosestTo(g.nodePoint(2023), 10));

        //https://www.openstreetmap.org/node/3490836266 node position (12744 index in Javelo)
        double lat = Math.toRadians(46.5864332);
        double lon = Math.toRadians(6.5124991);

        //should be -1 as we are searching at 12744 location but there is a bit of imprecision (or negative radius)
        assertEquals(-1, g.nodeClosestTo(new PointCh(Ch1903.e(lon, lat), Ch1903.n(lon, lat)), 0));
        assertEquals(-1, g.nodeClosestTo(new PointCh(Ch1903.e(lon, lat), Ch1903.n(lon, lat)), -4));

        //should be 12744 even if we change the radius
        assertEquals(12744, g.nodeClosestTo(new PointCh(Ch1903.e(lon, lat), Ch1903.n(lon, lat)), 10));
        assertEquals(12744, g.nodeClosestTo(new PointCh(Ch1903.e(lon, lat), Ch1903.n(lon, lat)), 20));
        assertEquals(12744, g.nodeClosestTo(new PointCh(Ch1903.e(lon, lat), Ch1903.n(lon, lat)), 1000));

        //on se décale un tout petit peu
        double lat2 = Math.toRadians(46.5864332);
        double lon2 = Math.toRadians(6.5824991);

        //pas de point à cette location
        assertEquals(-1, g.nodeClosestTo(new PointCh(Ch1903.e(lon2, lat2), Ch1903.n(lon2, lat2)), 0));
        //nearest point
        assertEquals(17538, g.nodeClosestTo(new PointCh(Ch1903.e(lon2, lat2), Ch1903.n(lon2, lat2)), 100));
    }


    @Test
    void testEdgeTargetNodeId() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));

        //https://www.openstreetmap.org/way/28431021
        assertEquals(17095,  g.edgeTargetNodeId(36234));
    }

    @Test
    void testEdgeIsInverted() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));

        //https://www.openstreetmap.org/way/28431021
        assertEquals(true,  g.edgeIsInverted(36234));
    }

    @Test
    void testEdgeAttributes() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));

        assertEquals(AttributeSet.of(Attribute.HIGHWAY_UNCLASSIFIED, Attribute.SURFACE_ASPHALT), g.edgeAttributes(36234));
    }
}
