package ch.epfl.javelo;

/**
 * Q28_4 Class
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class Q28_4 {

    // Non-instantiable class
    private Q28_4(){};

    /**
     * TODO:
     * Naive implementation of ofInt, I need to handle those two cases:
     *  - If negative value, avoid change on the sign. (can we realistically have a negative value as an input ?)
     *  - If i << 4 is bigger than Integer.MAX_VALUE // Is it a case that we need to worry about ? I don't think so.
     * @param i the int to format into Q28_4
     * @return The Q28_4 byte string that has the same value as i.
     */
    public static int ofInt(int i){
        return i << 4;
    }

    /**
     * Returns the double value of the q28_4 byte string given in input.
     * @param q28_4 The q28_4 byte string
     * @return double value of the {@param q28_4} byte string.
     */

    public static double asDouble(int q28_4){
        return Math.scalb((double) q28_4, -4);
    }

    /**
     * Returns the double value of the q28_4 byte string given in input.
     * @param q28_4 The q28_4 byte string
     * @return float value of the {@param q28_4} byte string.
     */

    public static float asFloat(int q28_4){
        return (float) (Math.scalb((double) q28_4, -4));
    }

}
