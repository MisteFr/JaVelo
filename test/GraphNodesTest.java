import ch.epfl.javelo.data.GraphNodes;
import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.*;

class GraphNodesTest {

    @Test
    public void givenTests(){
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

    // Inutile de tester les cas limites liés à la limite de stockage de Q28.4, E et N ne dépassant jamais 3_000_000 < 2^22.
    @Test
    public void nodeEAndNodeNTests(){

        int[] bPrime = {0b00000000000000000000000000000100,
                0b00000000000000000000000000010000,
                0b01000000000000000000000000001101,
                0b00000000000000000000000000000000,
                0b00000000000000000000000001000000,
                0b10000000000000000000000000000111};

        IntBuffer b = IntBuffer.wrap(bPrime);
        GraphNodes gn = new GraphNodes(b);

        assertEquals(0.25, gn.nodeE(0));
        assertEquals(0, gn.nodeE(1));

        assertEquals(1, gn.nodeN(0));
        assertEquals(4, gn.nodeN(1));

    }

    @Test
    public void countTest(){
        int[] bPrime = {0b00000000000000000000000000000100,
                0b00000000000000000000000000010000,
                0b01000000000000000000000000001101,
                0b00000000000000000000000000000000,
                0b00000000000000000000000001000000,
                0b10000000000000000000000000000111};

        IntBuffer b = IntBuffer.wrap(bPrime);
        GraphNodes gn = new GraphNodes(b);

        assertEquals(2, gn.count());

    }

    @Test
    public void outDegreeTest(){

        int[] bPrime = {0b00000000000000000000000000000100,
                0b00000000000000000000000000010000,
                0b01000000000000000000000000001101,
                0b00000000000000000000000000000000,
                0b00000000000000000000000001000000,
                0b10000000000000000000000000000111};

        IntBuffer b = IntBuffer.wrap(bPrime);
        GraphNodes gn = new GraphNodes(b);

        assertEquals(4, gn.outDegree(0));
        assertEquals(8, gn.outDegree(1));
    }


    @Test
    public void edgeIdTest(){

        //Normal case
        int[] bPrime = {0b00000000000000000000000000000100,
                0b00000000000000000000000000010000,
                0b01000000000000000000000000001101,
                0b00000000000000000000000000000000,
                0b00000000000000000000000001000000,
                0b10000000000000000000000000000111};

        IntBuffer b = IntBuffer.wrap(bPrime);
        GraphNodes gn = new GraphNodes(b);

        assertEquals(13, gn.edgeId(0, 0));
        assertEquals(15, gn.edgeId(0, 2));
        assertEquals(12, gn.edgeId(1, 5));

        //What if the edgeIndex isn't valid ? Works fine.

        assertThrows(AssertionError.class, () -> {
           gn.edgeId(0, 4);
        });

        assertThrows(AssertionError.class, () -> {
            gn.edgeId(0, -8);
        });


    }
}