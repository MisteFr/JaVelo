package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * Functions Class
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class Functions {

    //Non-instantiable class
    private Functions() {}

    /**
     * Method used to instantiate a constant function.
     * @param y value of the constant
     * @return Proper Constant object
     */
    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }

    /**
     * Method used to instantiate a sampled function (see Sampled inner-class)
     * @param samples samples that will serve as a basis for our function, must contain at least two elements.
     * @param xMax must be > 0
     * @return Proper Sampled function.
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        Preconditions.checkArgument((samples.length >= 2) && (xMax > 0));
        return new Sampled(samples, xMax);
    }


    /**
     * Constant function record
     *
     * @author Arthur Bigot (324366)
     * @author Léo Paoletti (342165)
     */


    private static final record Constant(double y) implements DoubleUnaryOperator {

        /**
         * TODO: Verifier assistant si la bonne valeur est retournée.
         * the constant function returns the value it was given at construction.
         * @param z is not used
         * @return
         */

        @Override
        public double applyAsDouble(double z) {
            return y();
        }
    }


    /**
     * Sampled function class
     *
     * @author Arthur Bigot (324366)
     * @author Léo Paoletti (342165)
     */

    private static final class Sampled implements DoubleUnaryOperator {

        // Fields
        private float[] samples;
        private double xSteps;
        private double xMax;

        /**
         * Constructor for Sampled DoubleUnaryOperator. Initiates samples, xMax and xSteps variables that are used in
         * applyAsDouble method.
         *
         * @param s samples
         * @param x xMax
         */

        private Sampled(float[] s, double x) {

            samples = new float[s.length];
            for (int i = 0; i < s.length; ++i) {
                samples[i] = s[i];
            }

            xMax = x;

            xSteps = xMax / (s.length - 1);
        }


        /**
         * Approximates a continuous function on [0;xMax] based on the samples array using interpolation.
         * If x < 0, the first sample is returned.
         * If x > xMax, the last sample is returned.
         * @param x the antecedent of the returned result
         * @return image of the function
         */

        @Override
        public double applyAsDouble(double x) {

            if(x < 0){
                return samples[0];
            }else if(x > xMax){
                return samples[samples.length - 1];
            }

            double xOnUnitSteps = x / xSteps;
            int lowerBound = (int) Math.floor(xOnUnitSteps);
            int upperBound = (int) Math.ceil(xOnUnitSteps);

            if (lowerBound != upperBound) {
                return Math2.interpolate((double) samples[lowerBound], (double) samples[upperBound], xOnUnitSteps - Math.floor(xOnUnitSteps));
            } else {
                return samples[(int) xOnUnitSteps];
            }
        }
    }
}
