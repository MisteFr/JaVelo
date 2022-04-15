package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.function.Consumer;

public final class WaypointsManager {

    private final Graph graph;
    private final ObjectProperty<MapViewParameters> mapViewParametersWrapped;
    private final ObservableList<Waypoint> transitPointsList;
    private final Consumer<String> errorReporter;

    private final Pane pane;

    public WaypointsManager(Graph g, ObjectProperty<MapViewParameters> jProperty, ObservableList<Waypoint> tPList, Consumer<String> errorR){
        graph = g;
        mapViewParametersWrapped = jProperty;
        transitPointsList = tPList;
        errorReporter = errorR;
        pane = new Pane();
    }

    public Pane pane() {
        return pane;
    }

    public void draw(){
        pane.getChildren().clear();
        MapViewParameters mapViewParameters = mapViewParametersWrapped.get();

        for(Waypoint w: transitPointsList){
            SVGPath svgpath1 = new SVGPath();
            SVGPath svgpath2 = new SVGPath();

            svgpath1.getStyleClass().add("pin_outside");
            svgpath2.getStyleClass().add("pin_insides");

            Group g = new Group(svgpath1, svgpath2);
            g.setId("pin");
            g.setLayoutX(mapViewParameters.viewX(PointWebMercator.ofPointCh(w.point())));
            g.setLayoutY(mapViewParameters.viewY(PointWebMercator.ofPointCh(w.point())));
            pane.getChildren().add(g);
        }
    }

    public void addWaypoint(int x, int y){
        PointCh waypointLocalisation = new PointCh(x, y);
        transitPointsList.add(new Waypoint(waypointLocalisation, graph.nodeClosestTo(waypointLocalisation, 100)));
        draw();
    }
}
