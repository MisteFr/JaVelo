package ch.epfl.javelo.projection;

public final class SwissBounds {

    // Non-instantiable class
    private SwissBounds(){}

    // FIELDS
    public static final double MIN_E = 2485000;
    public static final double MAX_E =  2834000;
    public static final double MIN_N = 1075000;
    public static final double MAX_N = 1296000;
    public static final double WIDTH = MAX_E - MIN_E;
    public static final double HEIGHT = MAX_N - MIN_N;

    /**
     * Should we search for a more elegant way to write the condition ?
     * @param e
     *      first coordination E of the point.
     * @param n
     *      second coordinate N of the point.
     * @return true if the point is between the borders defined by the constants (see FIELDS)
     */
    public static boolean containsEN(double e, double n){
        return ((e >= MIN_E) && (e <= MAX_E) && (n >= MIN_N) && (n <= MAX_N));
    }

}
