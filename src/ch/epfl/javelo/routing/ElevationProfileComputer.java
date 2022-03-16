/*package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

public final class ElevationProfileComputer {

    //Non-instantiable class
    private ElevationProfileComputer(){}

    public static ElevationProfile elevationProfile(Route route, double maxStepLength){//TODO: s'arranger pour utiliser ceilDiv de Math2 ?

        Preconditions.checkArgument(maxStepLength > 0);

        final int SPACING_NUMBER = (int) Math.ceil((route.length()/maxStepLength)); //TODO: Est ce que je récupère la bonne valeur ? Il veut vraiment un int ?



    }
}
*/