package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

/**
 * ElevationProfileComputer class
 * Represents a longitudinal profile calculator.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class ElevationProfileComputer {

    //Non-instantiable class
    private ElevationProfileComputer(){}

    /**
     * computes the elevation profile of the given Route route param given the double maxStepLength maximum spacing
     * Throws IllegalArgumentException if maxStepLength <= 0.
     * @param route the route of which we will compute the elevation profile
     * @param maxStepLength the maximum spacing between profile samples
     * @return ElevationProfile the elevation profile object of the given route
     * @throws IllegalArgumentException
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength){

        Preconditions.checkArgument(maxStepLength > 0);

        final int SAMPLE_NUMBER = (int) Math.ceil((route.length()/maxStepLength)) + 1;

        final double STEP_LENGTH = (route.length()/(SAMPLE_NUMBER - 1));
        // the distance of a step based on the total length of the route (there are SAMPLE_NUMBER - 1 steps)

        float[] samples = initializeSamplesArray(SAMPLE_NUMBER, route, STEP_LENGTH);


        samples = fillInBeginningOfSamplesArray(samples);

        samples = fillInEndOfSamplesArray(samples);

        samples = getsRidOfAllNanInSamplesArray(samples);

        return new ElevationProfile(route.length(), samples);
    }


    // Initialize samples array with NaN values
    private static float[] initializeSamplesArray(int SAMPLE_NUMBER, Route route, double STEP_LENGTH){
        float[] samples = new float[SAMPLE_NUMBER];

        for (int i = 0; i < SAMPLE_NUMBER; ++i){
            if(route.points().contains(route.pointAt(i*STEP_LENGTH))
                    && Float.isNaN((float) route.elevationAt(i*STEP_LENGTH))){
                //If we are on the extremity of an edge
                //locate previous edge
                Edge previousEdge = route.edges().get(route.indexOfSegmentAt(i*STEP_LENGTH) - 1);
                //Take its last sample
                samples[i] = (float) previousEdge.elevationAt(previousEdge.length());
            }else{
                samples[i] = (float) route.elevationAt(i*STEP_LENGTH);
            }
        }

        return samples;
    }

    // Method gets rid of Nan values at the beginning and treats the case of an all NaN samples array.
    private static float[] fillInBeginningOfSamplesArray(float[] samples){
        int i = 0;
        while((i != samples.length) && Float.isNaN(samples[i])){
            i += 1;
        }
        if(i != samples.length){
            Arrays.fill(samples, 0, i, samples[i]);
        }else{
            Arrays.fill(samples, 0, samples.length, 0F);
        }
        return samples;
    }

    // Method gets rid of Nan values at the end of the samples array.
    private static float[] fillInEndOfSamplesArray(float[] samples){
        int i = 0;
        while(Float.isNaN(samples[(samples.length - 1) - i])){
            i += 1;
        }
        Arrays.fill(samples, samples.length - i, samples.length, samples[(samples.length - 1) - i]);
        return samples;
    }

    // Browses the samples array a last time in order to replace intermediate NaN values by interpolated values.
    private static float[] getsRidOfAllNanInSamplesArray(float[] samples){
        int beginIndex = -1;
        int xMax = 0;
        //We fill the NaN gaps. First sample cannot be NaN.
        for(int j = 0; j < samples.length; ++j){
            if(Float.isNaN(samples[j])){
                if(beginIndex == -1){
                    beginIndex = j - 1;
                }
            }else if(beginIndex != -1){
                xMax = j - beginIndex;
                // We initialize an array based on the two borders of the non-NaN values of the sample array.
                // We choose an xMax so that we can define each samples elements with an int value as x argument in f.
                for (int k = 1; k < xMax ; ++k) {
                    samples[beginIndex + k] = (float) Math2.interpolate(samples[beginIndex], samples[j], (((double) k)/xMax));
                }
                beginIndex = -1;
            }
        }
        return samples;
    }
}
