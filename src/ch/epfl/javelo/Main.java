package ch.epfl.javelo;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

public class Main {

    public static void main(String[] args) throws IOException {

        //on crée une route avec routeComputer

        Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route r = rc.bestRouteBetween(159049, 117669);
        ElevationProfile e = ElevationProfileComputer.elevationProfile(r, 1);
        //on écrit le fichier gpx

        System.out.println(e.totalAscent());
        System.out.println(e.totalDescent());
        GpxGenerator.writeGpx("output.gpx",r, e);


    }

    private static String roundToFifthDecimal(double doubleToRound){

        int sixthDigit = (int) (doubleToRound * Math.pow(10, 6)) % 10;
        int fiveFirstDecimals = ((int) (doubleToRound * Math.pow(10, 5))) % (int) Math.pow(10, 5);
        //
        if(sixthDigit >= 5){
            return Integer.toString((int) doubleToRound) + "." + Integer.toString(fiveFirstDecimals + 1);
        }else{
            return Integer.toString((int) doubleToRound) + "." + Integer.toString(fiveFirstDecimals);
        }
    }
}
