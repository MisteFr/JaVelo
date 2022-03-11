package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;



/**
 * Graph class
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */


public class ElevationProfile {

    //Fields

    private final double LENGTH;
    private final double MIN_ELEVATION;
    private final double MAX_ELEVATION;
    private final double TOTAL_ASCENT;
    private final double TOTAL_DESCENT;
    private final DoubleUnaryOperator FUNC;


    //TODO Pas de problème de conversion float (le tableau en param) double (le retour) ?
    //TODO: Vérifier qu'elevation samples n'est pas compressé.
    /**
     * Constructor for ElevationProfile, initializes all the fields seen above.
     * @param length The length of the edge.
     * @param elevationSamples The array containing the samples of raw elevation (not compressed)
     */
    public ElevationProfile(double length, float[] elevationSamples){
        Preconditions.checkArgument((length > 0) && (elevationSamples.length >= 2));
        LENGTH = length;

        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        double tempTotalAscent = 0;
        double tempTotalDescent = 0;
        double diff;

        for (int i = 0; i < elevationSamples.length; ++i) {
            s.accept(elevationSamples[i]); // I initialize the DoubleSummaryStatistics with all the values of the array.

            if(i < elevationSamples.length - 2){ // For as long as I can take the elements i and i + 1 from elevationSamples...
                diff = elevationSamples[i] - elevationSamples[i+1];
                //... I verify if there is a descent or an ascent between those two elements of the array and increment the future TOTAL_ASCENT and TOTAL_DESCENT variables accordingly.
                if (diff > 0){
                    tempTotalAscent += diff;
                }else{
                    tempTotalDescent -= diff;
                }
            }

        }

        //Now that the DoubleSummaryStatistics object is filled with the elements of elevationSamples and the tempTotalAscent and tempTotalDescent are computed, we can initialize our final variables.

        MIN_ELEVATION = s.getMin();
        MAX_ELEVATION = s.getMax();
        TOTAL_ASCENT = tempTotalAscent;
        TOTAL_DESCENT = tempTotalDescent;

        //We now initialize the FUNC sampled function that maps the x coordinate ranging from 0 to length to its approximated continuous function based on the samples (see sampled in functions)
        FUNC = Functions.sampled(elevationSamples, length);
    }

    public double length(){
        return LENGTH;
    }

    public double minElevation(){
        return MIN_ELEVATION;
    }

    public double maxElevation(){
        return MAX_ELEVATION;
    }

    public double totalAscent(){
        return TOTAL_ASCENT;
    }

    public double totalDescent(){
        return TOTAL_DESCENT;
    }

    public double elevationAt(double position){
        return FUNC.applyAsDouble(position);
    }
}


