package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

public final class ElevationProfileComputer {

    //Non-instantiable class
    private ElevationProfileComputer(){}

    public static ElevationProfile elevationProfile(Route route, double maxStepLength){

        Preconditions.checkArgument(maxStepLength > 0);

        final int SAMPLE_NUMBER = (int) Math.ceil((route.length()/maxStepLength)) + 1;
        final double STEP_LENGTH = (route.length()/(SAMPLE_NUMBER - 1)); //La distance qui correspond à un step basée sur la longueur totale de l'itinéraire (il y a un step de moins qu'il n'y a de points) TODO vérifier.
        float[] samples = new float[SAMPLE_NUMBER];

        for (int i = 0; i < SAMPLE_NUMBER; ++i){ //Est-ce que ça peut juster renvoyer NaN ?
            samples[i] = (float) route.elevationAt(i*STEP_LENGTH);
        }

        // We correct the beginning of the sample array and treat the case where it is all NaN.
        int i = 0;
        while(Float.isNaN(samples[i]) && (i != samples.length)){
            i += 1;
        }
        if(i != samples.length){ //TODO utiliser arrays.fill
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
        for(int j = 0; j < i; ++j){

            samples[(samples.length - 1) - j] = samples[(samples.length - 1) - i];
        }

        int beginIndex = -1;
        int xMax = 0;
        //We fill the NaN gaps. First sample cannot be NaN.
        for(int j = 0; i < samples.length; ++i){
            if(Float.isNaN(samples[j])){
                beginIndex = j - 1; //TODO rajouter while ?
            }else if(beginIndex != -1){
                xMax = j - beginIndex;
                DoubleUnaryOperator f = Functions.sampled(new float[]{samples[beginIndex], samples[j]}, xMax);
                // We initialize an array based on the two borders of the non-NaN values of the sample array.
                // We choose an xMax so that we can define each samples elements with an int value as x argument in f.
                for (int k = 1; k < xMax ; ++k) {
                    samples[beginIndex + k] = (float) f.applyAsDouble(k);
                }
                beginIndex = -1;
            }
        }

        return new ElevationProfile(route.length(), samples);
    }
}
