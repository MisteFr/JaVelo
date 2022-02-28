import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class FunctionsTest {

    @Test
    void constantTest(){
        DoubleUnaryOperator c = Functions.constant(4);
        assertEquals(4, c.applyAsDouble(7));
    }

    @Test
    void sampledTest(){
        float[] samples = {12,100,6,8};
        double xMax = 12;
        DoubleUnaryOperator test = Functions.sampled(samples, xMax);
        assertEquals(Math2.interpolate(100,6, 0.25),test.applyAsDouble(5));
        assertEquals(12,test.applyAsDouble(-67));
        assertEquals(8,test.applyAsDouble(78));
    }

    @Test
    void sampledExceptionsTest(){
        assertThrows(IllegalArgumentException.class, () -> {
            float[] samples = {0};
            DoubleUnaryOperator test1 = Functions.sampled(samples, 4);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            float[] samples = {3,5};
            DoubleUnaryOperator test1 = Functions.sampled(samples, -3);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            float[] samples = {3,5};
            DoubleUnaryOperator test1 = Functions.sampled(samples, 0);
        });
    }

}