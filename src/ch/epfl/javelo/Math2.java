package ch.epfl.javelo;

/**
 * Math2 Class
 * Offer methods to perform some mathematical calculations.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class Math2 {

    // Non-instantiable class
    private Math2() {}

    /**
     * Returns the integral part of the division of x by y
     *
     * @param x, x value
     * @param y, y value
     * @return ceil of the division
     * @throws IllegalArgumentException if y value is zero or x is negative or null.
     */

    public static int ceilDiv(int x, int y) {
        Preconditions.checkArgument(y > 0 && x >= 0);
        return (int) Math.floor((x + y - 1) / y);
    }

    /**
     * Interpolate the y coordinate of the point of coordinate x using the coordinates of the point (x0, y0)
     *
     * @param y0 y0 value
     * @param y1 y1 value
     * @param x  x value
     * @return The y coordinate of the point of coordinate x
     */

    public static double interpolate(double y0, double y1, double x) {
        double m = y1 - y0; //slope of the line
        double b = y1 - m;

        return Math.fma(m, x, b);
    }

    /**
     * Limits the v-value to the interval from min to max.
     *
     * @param min min value
     * @param v   v value
     * @param max max value
     * @return a value in the interval from min to max included.
     * @throws IllegalArgumentException if max is inferior to min value
     */

    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(min <= max);
        if (v < min) {
            return min;
        } else return Math.min(v, max);
    }

    /**
     * Limit the v-value to the interval from min to max.
     *
     * @param min min value
     * @param v   v value
     * @param max max value
     * @return a value in the interval from min to max included.
     * @throws IllegalArgumentException if max is inferior to min value
     */

    public static double clamp(double min, double v, double max) {
        Preconditions.checkArgument(min <= max);
        return v < min ? min : Math.min(v, max);
    }

    /**
     * Computes the inverse hyperbolic sine of its argument x
     *
     * @param x x value
     * @return asinh
     */

    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + x * x));
    }

    /**
     * Computes the dot product of two vectors of coordinates (uX, uY); (vX, vY)
     *
     * @param uX uX value
     * @param uY uY value
     * @param vX vX value
     * @param vY vY value
     * @return the dot product of u and v
     */

    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return uX * vX + uY * vY;
    }

    /**
     * Computes the squared form of a vector of coordinates (uX, uY)
     *
     * @param uX uX value
     * @param uY uY value
     * @return the squared form of the vector
     */

    public static double squaredNorm(double uX, double uY) {
        return dotProduct(uX, uY, uX, uY);
    }

    /**
     * Computes the norm of a vector of coordinates (uX, uY)
     *
     * @param uX uX value
     * @param uY uY value
     * @return the norm of the vectors
     */

    public static double norm(double uX, double uY) {
        return Math.sqrt(dotProduct(uX, uY, uX, uY));
    }

    /**
     * Computes the length of the projection of a vector AP on a vector AB
     *
     * @param aX aX value
     * @param aY aY value
     * @param bX bX value
     * @param bY bY value
     * @param pX pX value
     * @param pY pY value
     * @return the length of the projection of vector AP on AB
     */

    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY) {
        double uX = pX - aX;
        double uY = pY - aY;
        double vX = bX - aX;
        double vY = bY - aY;

        return (uX * vX + uY * vY) / norm(vX, vY);
    }
}
