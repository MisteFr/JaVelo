package ch.epfl.javelo;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        //on crée une route avec routeComputer

        Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route r = rc.bestRouteBetween(159049, 117669);
        ElevationProfile e = ElevationProfileComputer.elevationProfile(r, 1);

        //on écrit le fichier gpx

        GpxGenerator.writeGpx("andWhyNot2.gpx",r, e);
    }
}
