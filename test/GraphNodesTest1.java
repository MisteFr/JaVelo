
import ch.epfl.javelo.data.GraphNodes;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GraphNodesTest1 {

    @Test
    void methodsGraphSectorsWorksGivenTest() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
    }

    @Test
    void methodsGraphSectorsWorksAyaTest() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0x1_803_0925
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(2, ns.count());
        assertEquals(2_536_263, ns.nodeE(0));
        assertEquals(1_215_736, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x918_1873, ns.edgeId(0, 0));
        assertEquals(Integer.valueOf(Integer.toHexString(0x918_1873 + 1),16),ns.edgeId(0, 1));
        assertEquals(1_297_183, ns.nodeE(1));
        assertEquals(2_015_772, ns.nodeN(1));
        assertEquals(1, ns.outDegree(1));
        assertEquals(0x803_0925, ns.edgeId(1, 0));
        //assertEquals(Integer.valueOf(Integer.toHexString(0x803_0925 + 1),16), ns.edgeId(1, 1));
        //assertThrows(AssertionError.class, ns.edgeId(1,1));
        //Piazza @259
    }

    @Test
    void methodsGraphSectorsWorksAyaTest2() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                8_501_018 << 4,
                9_005_704 << 4,
                0x6_000_1029,
                3_108_826 << 4,
                4_010_002 << 4,
                0x2_800_0015,
                3_108_826 << 4,
                4_010_002 << 4,
                0x9_800_0010
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(3, ns.count());
        assertEquals(8_501_018, ns.nodeE(0));
        assertEquals(9_005_704, ns.nodeN(0));
        assertEquals(6, ns.outDegree(0));
        assertEquals(0x1029, ns.edgeId(0, 0));
        assertEquals(Integer.valueOf(Integer.toHexString(0x1029 + 1), 16), ns.edgeId(0, 1));
        assertEquals(3_108_826, ns.nodeE(1));
        assertEquals(4_010_002, ns.nodeN(1));
        assertEquals(2, ns.outDegree(1));
        assertEquals(0x800_0015, ns.edgeId(1, 0));
        assertEquals(Integer.valueOf(Integer.toHexString(0x800_0015 + 1),16), ns.edgeId(1, 1));
        assertEquals(3_108_826, ns.nodeE(2));
        assertEquals(4_010_002, ns.nodeN(2));
        assertEquals(9, ns.outDegree(2));
        assertEquals(0x800_0010, ns.edgeId(2, 0));
        assertEquals(Integer.valueOf(Integer.toHexString(0x800_0010 + 1),16), ns.edgeId(2, 1));
    }
}