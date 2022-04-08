package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * Functions Class
 * Contains methods to create objects representing mathematical functions from real numbers to real numbers.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class Functions {

    //Non-instantiable class
    private Functions() {}

    /**
     * Method used to instantiate a constant function.
     *
     * @param y value of the constant
     * @return Proper Constant object
     */

    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }

    /**
     * Method used to instantiate a sampled function (see Sampled inner-class).
     *
     * @param samples samples that will serve as a basis for our function, must contain at least two elements.
     * @param xMax    must be > 0
     * @return Proper Sampled function.
     * @throws IllegalArgumentException if there is less than two elements in samples argument or if xMax is negative.
     */

    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        Preconditions.checkArgument((samples.length >= 2) && (xMax > 0));
        float newSamples[] = samples.clone();
        return new Sampled(newSamples, xMax);
    }


    /**
     * Constant function record
     * Represents a constant function.
     *
     * @author Arthur Bigot (324366)
     * @author Léo Paoletti (342165)
     */

    private record Constant(double y) implements DoubleUnaryOperator {

        /**
         * The constant function returns the value it was given at construction.
         *
         * @param z is not used
         * @return y value given at construction
         */

        @Override
        public double applyAsDouble(double z) {
            return y();
        }
    }


    /**
     * Sampled function class.
     * Approximates a continuous function by interpolating samples using Math2.interpolate().
     *
     * @author Arthur Bigot (324366)
     * @author Léo Paoletti (342165)
     */

    private static final class Sampled implements DoubleUnaryOperator {

        private final float[] SAMPLES;
        private final double X_STEPS;
        private final double X_MAX;

        /**
         * Constructor for Sampled DoubleUnaryOperator. Initiate samples, xMax and xSteps variables that are used in
         * applyAsDouble method.
         *
         * @param s samples
         * @param x xMax
         */

        private Sampled(float[] s, double x) {

            SAMPLES = new float[s.length];
            for (int i = 0; i < s.length; ++i) {
                SAMPLES[i] = s[i];
            }

            X_MAX = x;

            X_STEPS = X_MAX / (s.length - 1);
        }


        /**
         * Approximates a continuous function on [0;xMax] based on the samples array using interpolation.
         * If x < 0, the first sample is returned.
         * If x > xMax, the last sample is returned.
         *
         * @param x the antecedent of the returned result
         * @return image of the function
         */

        @Override
        public double applyAsDouble(double x) {

            if (x < 0) {
                return SAMPLES[0];
            } else if (x > X_MAX) {
                return SAMPLES[SAMPLES.length - 1];
            }

            double xOnUnitSteps = x / X_STEPS;
            int lowerBound = (int) Math.floor(xOnUnitSteps);
            int upperBound = (int) Math.ceil(xOnUnitSteps);

            if (lowerBound != upperBound) {
                return Math2.interpolate(SAMPLES[lowerBound], SAMPLES[upperBound], xOnUnitSteps - Math.floor(xOnUnitSteps));
            } else {
                return SAMPLES[(int) xOnUnitSteps];
            }
        }
    }
}
