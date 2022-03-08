import ch.epfl.javelo.data.GraphSectors;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.ByteBuffer;



class GraphSectorsTest {

    @Test
    public void sectorsInAreaTest(){

        //Initialization of ByteBuffer.
        ByteBuffer b = ByteBuffer.allocate(98304);
        for (int i = 0; i < 16384; ++i){
            b.putInt(i);
            b.putShort((short)0);
        }
        GraphSectors gs = new GraphSectors(b);
        // Every sector's first node's value is the index of the sector in the buffer of sectors. Every sector contains exactly 0 nodes.


        //First test: we want to get only the sector #8256, located in the middle.
        //For that we use the fact that the equality between record objects was modified so that each attribute gets compared instead of the references.
        assertEquals(new GraphSectors.Sector(8256, 8256),gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 64*(2.7265625 * 1000),
                SwissBounds.MIN_N + 64*(1.7265625 * 1000)), 0).get(0));


        //Second test: we want to get only the sector in the top right corner (last one according to its index).
        assertEquals(new GraphSectors.Sector(16383, 16383),gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 127.5*(2.7265625 * 1000),
                SwissBounds.MIN_N + 127.5*(1.7265625 * 1000)), 0).get(0));


        //Third test: multiple sectors in the area.
        assertEquals(new GraphSectors.Sector(8127, 8127),gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 64*(2.7265625 * 1000),
                SwissBounds.MIN_N + 64*(1.7265625 * 1000)), 300).get(0));
        assertEquals(new GraphSectors.Sector(8128, 8128),gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 64*(2.7265625 * 1000),
                SwissBounds.MIN_N + 64*(1.7265625 * 1000)), 300).get(1));
        assertEquals(new GraphSectors.Sector(8255, 8255),gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 64*(2.7265625 * 1000),
                SwissBounds.MIN_N + 64*(1.7265625 * 1000)), 300).get(2));
        assertEquals(new GraphSectors.Sector(8256, 8256),gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 64*(2.7265625 * 1000),
                SwissBounds.MIN_N + 64*(1.7265625 * 1000)), 300).get(3));


        //Fourth test: what if the drawn square gets passed the borders defined by the class Swissbound ?
            //If no error is thrown while using an absurdly high distance value (1000000000 meters), this may indicate that you treat this case appropriately.
        gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 64*(2.7265625 * 1000), SwissBounds.MIN_N + 64*(1.7265625 * 1000)), 1000000000);
            // We draw a square from the bottom left corner of the grid. We are supposed to list the sectors 0, 1, 128 and 129 without errors.
        assertEquals(new GraphSectors.Sector(0, 0),gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 0.1*(2.7265625 * 1000),
                SwissBounds.MIN_N + 0.1*(1.7265625 * 1000)), 3000).get(0));
        assertEquals(new GraphSectors.Sector(1, 1),gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 0.1*(2.7265625 * 1000),
                SwissBounds.MIN_N + 0.1*(1.7265625 * 1000)), 3000).get(1));
        assertEquals(new GraphSectors.Sector(128, 128),gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 0.1*(2.7265625 * 1000),
                SwissBounds.MIN_N + 0.1*(1.7265625 * 1000)), 3000).get(2));
        assertEquals(new GraphSectors.Sector(129, 129),gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 0.1*(2.7265625 * 1000),
                SwissBounds.MIN_N + 0.1*(1.7265625 * 1000)), 3000).get(3));

        // Hope you found this test useful ! - Léo.


        /* Additional test that is not based on the instructions. I wanted to verify that we could not input a negative value as a distance.
        assertThrows(AssertionError.class, () -> {
            gs.sectorsInArea(new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N),-1);
        });
         */
    }
}