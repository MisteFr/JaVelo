package ch.epfl.javelo.projection;

/**
 * SwissBounds Class
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class SwissBounds {

    // Non-instantiable class
    private SwissBounds() {}

    // FIELDS
    /**
     * The smallest E-coordinate in Switzerland
     */
    public static final double MIN_E = 2485000;

    /**
     * The largest E-coordinate in Switzerland
     */
    public static final double MAX_E =  2834000;

    /**
     * The smallest N-coordinate in Switzerland
     */
    public static final double MIN_N = 1075000;

    /**
     * The largest N-coordinate in Switzerland
     */
    public static final double MAX_N = 1296000;

    /**
     * The width of Switzerland in meters
     */
    public static final double WIDTH = MAX_E - MIN_E;

    /**
     * The height of Switzerland in meters
     */
    public static final double HEIGHT = MAX_N - MIN_N;

    /**
     * Test that a point is within the limits of Switzerland.
     *
     * @param e first coordination E of the point.
     * @param n second coordinate N of the point.
     * @return true if the point is between the borders defined by the constants (see FIELDS)
     */
    public static boolean containsEN(double e, double n){
        return ((e >= MIN_E) && (e <= MAX_E) && (n >= MIN_N) && (n <= MAX_N));
    }

}
