package ch.epfl.javelo;

/**
 * Bits class
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class Bits {

    // Non-instantiable class
    private Bits(){}

    /**
     * Extract from the 32 bits vector the range of 'length' bits starting at the bit of index 'start' that
     * it interprets as a signed value.
     * @param value the 32 bits vector
     * @param start start bit
     * @param length length of the range
     * @throws IllegalArgumentException if the range is invalid
     * @return the signed value extracted
     */
    static int extractSigned(int value, int start, int length){
        Preconditions.checkArgument(!(start < 0 || start > 31 || (start + length) > 31 || length < 0));


        return 1;
    }

    /**
     * Extract from the 32 bits vector the range of 'length' bits starting at the bit of index 'start' that
     * it interprets as a unsigned value.
     * @param value the 32 bits vector
     * @param start start bit
     * @param length length of the range
     * @return the unsigned value extracted
     */
    static int extractUnsigned(int value, int start, int length){
        Preconditions.checkArgument(!(start < 0 || start > 31 || (start + length) > 31 || length < 0));

        return 1;
    }
}
