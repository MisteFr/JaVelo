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
     * TODO: DOC
     * @param route
     * @param maxStepLength
     * @return
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength){

        Preconditions.checkArgument(maxStepLength > 0);

        //TODO Math2.ceilDiv?
        final int SAMPLE_NUMBER = (int) Math.ceil((route.length()/maxStepLength)) + 1;
        final double STEP_LENGTH = (route.length()/(SAMPLE_NUMBER - 1)); //La distance qui correspond à un step basée sur la longueur totale de l'itinéraire (il y a un step de moins qu'il n'y a de points)
        float[] samples = new float[SAMPLE_NUMBER];

        for (int i = 0; i < SAMPLE_NUMBER; ++i){ //TODO pas utile pour l'instant comme on travaille avec des itinéraires simples (une arête)
            /*if(route.points().contains(route.pointAt(i*STEP_LENGTH)) && Float.isNaN((float) route.elevationAt(i*STEP_LENGTH))){ //If we are on the extremity of an
                //locate previous edge
                Edge previousEdge = route.edges().get(route.indexOfSegmentAt(i*STEP_LENGTH) - 1);
                //Take its last sample
                samples[i] = (float) previousEdge.elevationAt(previousEdge.length());
            }else{*/
                samples[i] = (float) route.elevationAt(i*STEP_LENGTH);
            //}
        }

        // We correct the beginning of the sample array and treat the case where it is all NaN.
        //TODO only use one loop ?
        int i = 0;
        while((i != samples.length) && Float.isNaN(samples[i])){
            i += 1;
        }
        if(i != samples.length){
            Arrays.fill(samples, 0, i, samples[i]);
        }else{
            Arrays.fill(samples, 0, samples.length, 0F);
        }

        //We correct the end of the sample array.
        i = 0;
        while(Float.isNaN(samples[(samples.length - 1) - i])){
            i += 1;
        }
        Arrays.fill(samples, samples.length - i, samples.length, samples[(samples.length - 1) - i]);


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

        return new ElevationProfile(route.length(), samples);
    }
}
