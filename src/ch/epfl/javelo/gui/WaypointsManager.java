package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * WaypointsManager class
 * Manages the display and interaction with the waypoints.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class WaypointsManager {

    private final Graph graph;
    private final ObjectProperty<MapViewParameters> mapViewParametersWrapped;
    private final ObservableList<Waypoint> transitPointsList;
    private final Consumer<String> errorReporter;

    //pane containing the waypoints
    private final Pane pane;

    //search distance for the nearestNode function when adding a waypoint
    private final int SEARCH_DISTANCE = 500;

    //constants for the waypoints style class + svg paths
    private final String OUTSIDE_PIN_PATH = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private final String INSIDE_PIN_PATH = "M0-23A1 1 0 000-29 1 1 0 000-23";
    private final String OUTSIDE_PIN_STYLE_CLASS = "pin_outside";
    private final String INSIDE_PIN_STYLE_CLASS = "pin_inside";
    private final String PIN_STYLE_CLASS = "pin";
    private final String PIN_STYLE_CLASS_FIRST = "first";
    private final String PIN_STYLE_CLASS_MIDDLE = "middle";
    private final String PIN_STYLE_CLASS_LAST = "last";


    /**
     * WaypointsManager constructor.
     * @param g JaVelo Graph instance
     * @param jProperty MapViewParameters wrapped into a JavaFx property
     * @param tPList ObservableList of Waypoint(s)
     * @param errorR Object for reporting errors
     */
    public WaypointsManager(Graph g, ObjectProperty<MapViewParameters> jProperty, ObservableList<Waypoint> tPList, Consumer<String> errorR){
        graph = g;
        mapViewParametersWrapped = jProperty;
        transitPointsList = tPList;
        errorReporter = errorR;
        pane = new Pane();
    }

    /**
     * Returns the pane containing the waypoints
     * @return Pane the pane containing the waypoints
     */
    public Pane pane() {
        draw();
        return pane;
    }

    /**
     * Method that draws the waypoints on the pane
     */

    public void draw(){
        pane.getChildren().clear();
        MapViewParameters mapViewParameters = mapViewParametersWrapped.get();

        Iterator<Waypoint> itr = transitPointsList.iterator();
        boolean firstElementSeen = false;

        while(itr.hasNext()) {
            Waypoint w = itr.next();

            SVGPath svgpath_outside = new SVGPath();
            SVGPath svgpath_inside = new SVGPath();


            svgpath_outside.getStyleClass().add(OUTSIDE_PIN_STYLE_CLASS);
            svgpath_outside.setContent(OUTSIDE_PIN_PATH);
            svgpath_inside.getStyleClass().add(INSIDE_PIN_STYLE_CLASS);
            svgpath_inside.setContent(INSIDE_PIN_PATH);

            Group group = new Group(svgpath_outside, svgpath_inside);
            group.setLayoutX(mapViewParameters.viewX(PointWebMercator.ofPointCh(w.point())));
            group.setLayoutY(mapViewParameters.viewY(PointWebMercator.ofPointCh(w.point())));

            group.getStyleClass().add(PIN_STYLE_CLASS);

            if(!firstElementSeen){
                firstElementSeen = true;
                group.getStyleClass().add(PIN_STYLE_CLASS_FIRST);
            }else if(!itr.hasNext()){
                group.getStyleClass().add(PIN_STYLE_CLASS_LAST);
            }else{
                group.getStyleClass().add(PIN_STYLE_CLASS_MIDDLE);
            }

            pane.getChildren().add(group);
        }
    }

    /**
     * Adds a new waypoint at the given position in the map's coordinate system
     * @param x x position in the map coordinate system
     * @param y y position in the map coordinate system
     */

    public void addWaypoint(int x, int y){
        PointCh waypointLocalisation = mapViewParametersWrapped.get().pointAt(x, y).toPointCh();
        transitPointsList.add(new Waypoint(waypointLocalisation, graph.nodeClosestTo(waypointLocalisation, SEARCH_DISTANCE)));
        draw();
    }
}
