import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;

import java.io.IOException;
import java.nio.file.Path;

public final class Stage6Test {
    public static void main(String[] args) throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        long t0 = System.nanoTime();
        Route r = rc.bestRouteBetween(159049, 117669);
        System.out.printf("Itinéraire calculé en %d ms\n",
                (System.nanoTime() - t0) / 1_000_000);
        KmlPrinter.write("javelo.kml", r);
        System.out.println(r.length() + " " + r.edges().size());


        Graph g2 = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf2 = new CityBikeCF(g2);
        RouteComputer rc2 = new RouteComputer(g2, cf2);
        long t1 = System.nanoTime();
        Route r2 = rc2.bestRouteBetween(2046055, 2694240);
        KmlPrinter.write("javelo2.kml", r2);
        System.out.printf("Itinéraire calculé en %d ms\n",
                (System.nanoTime() - t1) / 1_000_000);
        System.out.println(r2.length() + " " + r2.edges().size());
    }
}
