package ch.epfl.javelo;

/**
 * Math 2 Class
 *
 * @author Arthur Bigot
 * @author Leo Paoletti
 */

public final class Math2 {

    private Math2() {}

    /**
     * Return the integral part of the division of x by y
     * @param x
     * @param y
     * @return ceil of the division
     */
    public static int ceilDiv(int x, int y){
        if(y == 0){
            throw new IllegalArgumentException("Division by zero error.");
        }
        if(x < 0){
            throw new IllegalArgumentException("X should be positive or null.");
        }
        return (int) Math.floor((x + y - 1) / y);
    }

    /**
     * Interpolate (à vérifier)
     * @param y0
     * @param y1
     * @param x
     * @return The y coordinate of the point of coordinate x
     */
    public static double interpolate(double y0, double y1, double x){
        double m = y1 - y0; //pente de la droite
        double b = y1 - m;
        //droite de la forme y = mx + b;
        return Math.fma(m, x, b);
    }

    /**
     * Limit the v-value to the interval from min to max.
     * @param min
     * @param v
     * @param max
     * @return a value in the interval from min to max included.
     */
    public static int clamp(int min, int v, int max){
        if(min > max){
            throw new IllegalArgumentException("max is inferior to min in int clamp();");
        }
        if(v < min){
            return min;
        }else if(v > max){
            return max;
        }else{
            return v;
        }
    }

    /**
     * Limit the v-value to the interval from min to max.
     * @param min
     * @param v
     * @param max
     * @return a value in the interval from min to max included.
     */
    public static double clamp(double min, double v, double max){
        if(min > max){
            throw new IllegalArgumentException("max is inferior to min in double clamp();");
        }
        if(v < min){
            return min;
        }else if(v > max){
            return max;
        }else{
            return v;
        }
    }

    /**
     * Calculate the inverse hyperbolic sine of its argument x
     * @param x
     * @return asinh
     */
    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1 + x*x));
    }

    /**
     * Calculate the dot product of two vectors of coordinates (uX, uY); (vX, vY)
     * @param uX
     * @param uY
     * @param vX
     * @param vY
     * @return the dot product of u and v
     */
    public static double dotProduct(double uX, double uY, double vX, double vY){
        return uX*vX + uY*vY;
    }

    /**
     * Calculate the squared form of vector of coordinates (uX, uY)
     * @param uX
     * @param uY
     * @return
     */
    public static double squaredNorm(double uX, double uY){
        return (uX*uX + uY*uY);
    }

    /**
     * Calculate the norm of the vector of coordinates (uX, uY)
     * @param uX
     * @param uY
     * @return
     */
    public static double norm(double uX, double uY){
        return Math.sqrt((uX*uX + uY*uY));
    }

    /**
     * Calculate the length of the projection of vector AP on AB
     * @param aX
     * @param aY
     * @param bX
     * @param bY
     * @param pX
     * @param pY
     * @return the length of the projection of vector AP on AB
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY){
        double uX = pX - aX;
        double uY = pY - aY;
        double vX = bX - aX;
        double vY = bY - aY;

        return (uX*vX + uY*vY) / norm(vX, vY);
    }
}
