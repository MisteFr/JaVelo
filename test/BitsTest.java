import ch.epfl.javelo.Bits;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BitsTest {

    @Test
    void testRangeFunction(){
        //should be out of range as start+length > 31
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0b11001010111111101011101010111110, 31, 4);
        });
    }

    @Test
    void testRangeFunction2(){
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0b11001010111111101011101010111110, -2, 4);
        });
    }

    @Test
    void testRangeFunction3(){
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0b11001010111111101011101010111110, 0, 12);
        });
    }

    @Test
    void testExtractSigned() {
        //assertEquals(37, Bits.extractSigned(0b00000000000000000000000000100101, 0, 32));
        System.out.println(Bits.extractSigned(0b11001010111111101011101010111110, 8, 4));
    }
}
