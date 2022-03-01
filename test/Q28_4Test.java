import ch.epfl.javelo.Q28_4;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Q28_4Test {

    @Test
    void ofIntTest(){

        /*
        int byte values associated with the largest values that will be formatted in Q28.4 in this project:
        - MAX_E: 2834000 --> 0b00000000001010110011111001010000
        - MAX_N: 1296000 --> 0b00000000000100111100011010000000
         */

        assertEquals(0b00000000000000000000000000010000, Q28_4.ofInt(1));
        assertEquals(0b00000000000000000000000100010000, Q28_4.ofInt(17));
        assertEquals(0b00000010101100111110010100000000, Q28_4.ofInt((int)SwissBounds.MAX_E));
        assertEquals(0b00000001001111000110100000000000, Q28_4.ofInt((int)SwissBounds.MAX_N));
    }

    @Test
    void asDoubleTest(){
        assertEquals((double) 1, Q28_4.asDouble(0b00000000000000000000000000010000));
        assertEquals((double) 0.25, Q28_4.asDouble(0b00000000000000000000000000000100));
    }

    @Test
    void asFloatTest(){
        assertEquals((float) 16, Q28_4.asFloat(0b00000000000000000000000100000000));
        assertEquals((float) 0.25, Q28_4.asDouble(0b00000000000000000000000000000100));
    }
}