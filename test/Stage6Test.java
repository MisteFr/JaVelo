import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;

import java.io.IOException;
import java.nio.file.Path;

public final class Stage6Test {
    public static void main(String[] args) throws IOException {
        /*Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        for (int i = 0; i < 20; i++) {
            long t0 = System.nanoTime();
            Route r = rc.bestRouteBetween(159049, 117669);
            System.out.printf("Itinéraire calculé en %d ms\n",
                    (System.nanoTime() - t0) / 1_000_000);
            KmlPrinter.write("javelo.kml", r);
            System.out.println(r.length() + " " + r.edges().size());
        }*/
        Graph g1 = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf1 = new CityBikeCF(g1);
        RouteComputer rc1 = new RouteComputer(g1, cf1);
        for (int i = 0; i < 20; i++) {
            long t0 = System.nanoTime();
            Route r1 = rc1.bestRouteBetween(2046055, 2694240);
            System.out.printf("Itinéraire calculé en %d ms\n",
                    (System.nanoTime() - t0) / 1_000_000);
            KmlPrinter.write("javelo.kml", r1);
            System.out.println(r1.length() + " " + r1.edges().size());
        }
    }
}
