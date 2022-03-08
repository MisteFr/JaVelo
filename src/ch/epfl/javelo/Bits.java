package ch.epfl.javelo;

/**
 * Bits class
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
        Preconditions.checkArgument(!(start < 0 || start > 31 || (start + length) > 32 || length < 0));
        //left arithmetic shift
        int leftArithmeticShift = value << (32 - (start + length));
        //right arithmetic shift
        return leftArithmeticShift >> (32 - length);
    }

    /**
     * Extract from the 32 bits vector the range of 'length' bits starting at the bit of index 'start' that
     * it interprets as a unsigned value.
     *
     * @param value  the 32 bits vector
     * @param start  start bit
     * @param length length of the range
     * @return the unsigned value extracted
     */
    public static int extractUnsigned(int value, int start, int length) {
        Preconditions.checkArgument(!(start < 0 || start > 31 || (start + length) > 32 || length < 0 || length > 31 ));
        //left arithmetic shift
        int leftArithmeticShifted = value << (32 - (start + length));
        //right logical shift
        return leftArithmeticShifted >>> (32 - length);
    }
}
