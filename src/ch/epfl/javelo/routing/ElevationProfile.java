package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;


/**
 * ElevationProfile class
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


    /**
     * Constructor for ElevationProfile, initializes all the fields seen above.
     *
     * @param length           The length of the edge.
     * @param elevationSamples The array containing the samples of raw elevation (not compressed)
     */

    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument((length > 0) && (elevationSamples.length >= 2));
        LENGTH = length;

        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        double tempTotalAscent = 0;
        double tempTotalDescent = 0;
        double diff;

        for (int i = 0; i < elevationSamples.length; ++i) {
            s.accept(elevationSamples[i]); // I initialize the DoubleSummaryStatistics with all the values of the array.

            if (i < elevationSamples.length - 1) { // For as long as I can take the elements i and i + 1 from elevationSamples...
                diff = elevationSamples[i+1] - elevationSamples[i];
                //... I verify if there is a descent or an ascent between those two elements of the array and increment the future TOTAL_ASCENT and TOTAL_DESCENT variables accordingly.
                if (diff > 0) {
                    tempTotalAscent += diff;
                } else {
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

    /**
     * Returns the length of the edge given at construction
     *
     * @return double length
     */

    public double length() {
        return LENGTH;
    }

    /**
     * returns the minimum elevation of the elevation samples in the float array given at construction
     *
     * @return double MIN_ELEVATION
     */

    public double minElevation() {
        return MIN_ELEVATION;
    }

    /**
     * returns the maximum elevation of the elevation samples in the float array given at construction
     *
     * @return double MAX_ELEVATION
     */

    public double maxElevation() {
        return MAX_ELEVATION;
    }

    /**
     * returns the sum of the positive differences between each pair of consecutive samples in the float array given at construction.
     *
     * @return double TOTAL_ASCENT
     */

    public double totalAscent() {
        return TOTAL_ASCENT;
    }

    /**
     * returns the absolute value of the sum of the negative differences between each pair of consecutive samples in the float array given at construction.
     *
     * @return double TOTAL_ASCENT
     */

    public double totalDescent() {
        return TOTAL_DESCENT;
    }

    /**
     * Returns the height approximated by the sampled FUNC function initialized at construction for x = position.
     * The function is explicitly defined from 0 to LENGTH but x can take any double value.
     * If x < 0, the value of the first sample is returned.
     * If x > LENGTH, the value of the last sample is returned.
     *
     * @param position x value that will be given to the FUNC function.
     * @return double y = FUNC(x)
     */

    public double elevationAt(double position) {
        return FUNC.applyAsDouble(position);
    }
}


