import ch.epfl.javelo.Bits;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BitsTest {

    @Test
    void testRangeFunction(){
        //out of range as start+length > 31
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0b11001010111111101011101010111110, 31, 4);
        });
    }

    @Test
    void testRangeFunction2(){
        //start is < 0
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0b11001010111111101011101010111110, -2, 4);
        });
    }

    @Test
    void testRangeFunction3(){
        //length+start > 32
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0b11001010111111101011101010111110, 0, 33);
        });
    }

    @Test
    void testRangeFunction4(){
        //length < 0
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0b11001010111111101011101010111110, 8, -3);
        });
    }

    @Test
    void testRangeFunction5(){
        //start > 31
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0b11001010111111101011101010111110, 32, 4);
        });
    }

    @Test
    void testExtractSigned() {
        //1010 -> 10
        assertEquals(-6, Bits.extractSigned(0b11001010111111101011101010111110, 8, 4));
        //11011 -> -5 -16 + 11 = -5
        assertEquals(-5, Bits.extractSigned(0b11011111111111101011101010111110, 27, 5));
        //01011 -> 11 = 11
        assertEquals(11, Bits.extractSigned(0b01011111111111101011101010111110, 27, 5));
    }

    @Test
    void testExtractUnsigned() {
        //1010 -> 10
        assertEquals(10, Bits.extractUnsigned(0b11001010111111101011101010111110, 8, 4));
        //1100 -> 12
        assertEquals(12, Bits.extractUnsigned(0b11001010111111101011101010111110, 28, 4));
        //001-> 1
        assertEquals(1, Bits.extractUnsigned(0b00000000000000000000000000100101, 2, 3));
        //1111 -> 15
        assertEquals(15, Bits.extractUnsigned(0b00000000000000000000000000101111, 0, 4));
    }
}
