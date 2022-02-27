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
    private Functions(){}

    public static DoubleUnaryOperator constant(double y){
        return new Constant(y);
    }

    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
        Preconditions.checkArgument((samples.length >= 2) && (xMax > 0));
        return new Sampled(samples, xMax);
    }



    private static final record Constant(double y) implements DoubleUnaryOperator{
        @Override
        public double applyAsDouble(double z){
            return y();
        }
    }

    //TODO: Is there a real need for a static keyword here ?

    private static final class Sampled implements DoubleUnaryOperator{

        // Fields
        private float[] samples;
        private double xSteps;
        private double xMax;

        /**
         * Constructor for Sampled DoubleUnaryOperator. Initiates samples, xMax and xSteps variables that are used in
         * applyAsDouble method.
         * @param s samples
         * @param x xMax
         */

        //TODO: Verify validity of private keyword here and usability of function.

        private Sampled(float[] s, double x){

            samples = new float[s.length];
            for(int i = 0; i < s.length; ++i){
                samples[i] = s[i];
            }

            xMax = x;

            xSteps = xMax/s.length;
        }


        /**
         * TODO: clean up documentation
         * // I bring all xSteps cases to the case where the steps between the samples have a value of 1 (that is to say xMax = s.length)
         * @param x
         * @return
         */

        @Override
        public double applyAsDouble(double x){
            // careful with divisions and conversions.
            int lowerBound = (int) Math.ceil(x/xSteps);
            int upperBound = (int) Math.floor(x/xSteps);

            if(lowerBound != upperBound){
                return Math2.interpolate((double) samples[lowerBound], (double) samples[upperBound], x/xSteps);
            }else{
                return samples[(int)x];
            }
        }
    }
}
