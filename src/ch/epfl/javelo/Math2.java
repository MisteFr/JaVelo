package ch.epfl.javelo;

/**
 * Math 2 Class
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class Math2 {

    private Math2() {
    }

    /**
     * Return the integral part of the division of x by y
     *
     * @param x, x value
     * @param y, y value
     * @throws IllegalArgumentException if y value is zero or x is negative or null.
     * @return ceil of the division
     */
    public static int ceilDiv(int x, int y) {
        if (y == 0) {
            throw new IllegalArgumentException("Division by zero error.");
        }
        if (x < 0) {
            throw new IllegalArgumentException("X should be positive or null.");
        }
        return (int) Math.floor((x + y - 1) / y);
    }

    /**
     * Interpolate the y coordinate of the point of coordinate x using the coordinates of the point (x0, y0)
     *
     * @param y0 y0 value
     * @param y1 y1 value
     * @param x x value
     * @return The y coordinate of the point of coordinate x
     */
    public static double interpolate(double y0, double y1, double x) {
        double m = y1 - y0; //slope of the line
        double b = y1 - m;

        return Math.fma(m, x, b);
    }

    /**
     * Limit the v-value to the interval from min to max.
     *
     * @param min min value
     * @param v v value
     * @param max max value
     * @throws IllegalArgumentException if max is inferior to min value
     * @return a value in the interval from min to max included.
     */
    public static int clamp(int min, int v, int max) {
        if (min > max) {
            throw new IllegalArgumentException("max is inferior to min value in int clamp();");
        }
        if (v < min) {
            return min;
        } else return Math.min(v, max);
    }

    /**
     * Limit the v-value to the interval from min to max.
     *
     * @param min min value
     * @param v v value
     * @param max max value
     * @throws IllegalArgumentException if max is inferior to min value
     * @return a value in the interval from min to max included.
     */
    public static double clamp(double min, double v, double max) {
        if (min > max) {
            throw new IllegalArgumentException("max is inferior to min value in double clamp();");
        }
        if (v < min) {
            return min;
        } else return Math.min(v, max);
    }

    /**
     * Calculate the inverse hyperbolic sine of its argument x
     *
     * @param x x value
     * @return asinh
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + x * x));
    }

    /**
     * Calculate the dot product of two vectors of coordinates (uX, uY); (vX, vY)
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
     * Calculate the squared form of a vector of coordinates (uX, uY)
     *
     * @param uX uX value
     * @param uY uY value
     * @return the squared form of the vector
     */
    public static double squaredNorm(double uX, double uY) {
        return (uX * uX + uY * uY);
    }

    /**
     * Calculate the norm of a vector of coordinates (uX, uY)
     *
     * @param uX uX value
     * @param uY uY value
     * @return the norm of the vectors
     */
    public static double norm(double uX, double uY) {
        return Math.sqrt((uX * uX + uY * uY));
    }

    /**
     * Calculate the length of the projection of a vector AP on a vector AB
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
