package ch.epfl.javelo;

/**
 * Bits class
 * Contains methods to extract a sequence of bits from a 32 bits vector.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class Bits {

    // Non-instantiable class
    private Bits() {}

    /**
     * Extract from the 32 bits vector the range of 'length' bits starting at the bit of index 'start' that
     * it interprets as a signed value.
     *
     * @param value  the 32 bits vector
     * @param start  start bit
     * @param length length of the range
     * @return the signed value extracted
     * @throws IllegalArgumentException if the range is invalid
     */

    public static int extractSigned(int value, int start, int length) {
        Preconditions.checkArgument(!(start < 0 || start > 31 || (start + length) > Integer.SIZE || length < 0));
        //left arithmetic shift
        int leftArithmeticShift = value << (Integer.SIZE - (start + length));
        //right arithmetic shift
        return leftArithmeticShift >> (Integer.SIZE - length);
    }

    /**
     * Extract from the 32 bits vector the range of 'length' bits starting at the bit of index 'start' that
     * it interprets as an unsigned value.
     *
     * @param value  the 32 bits vector
     * @param start  start bit
     * @param length length of the range
     * @return the unsigned value extracted
     * @throws IllegalArgumentException if the range is invalid
     */

    public static int extractUnsigned(int value, int start, int length) {
        Preconditions.checkArgument(!(start < 0 || start > 31 || (start + length) > Integer.SIZE || length < 0 || length > 31));
        //left arithmetic shift
        int leftArithmeticShifted = value << (Integer.SIZE - (start + length));
        //right logical shift
        return leftArithmeticShifted >>> (Integer.SIZE - length);
    }
}
