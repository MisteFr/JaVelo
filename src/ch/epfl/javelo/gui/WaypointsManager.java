package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
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

    private final Graph GRAPH;
    private final ObjectProperty<MapViewParameters> MAP_VIEW_PARAMETERS_WRAPPED;
    private final ObservableList<Waypoint> TRANSIT_POINTS_LIST;
    private final Consumer<String> ERROR_REPORTER;

    //pane containing the waypoints
    private final Pane PANE;

    //search distance for the nearestNode function when adding a waypoint
    private final static int SEARCH_DISTANCE = 500;

    //constants for the waypoints style class + svg paths
    private final static String OUTSIDE_PIN_PATH = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private final static String INSIDE_PIN_PATH = "M0-23A1 1 0 000-29 1 1 0 000-23";
    private final static String OUTSIDE_PIN_STYLE_CLASS = "pin_outside";
    private final static String INSIDE_PIN_STYLE_CLASS = "pin_inside";
    private final static String PIN_STYLE_CLASS = "pin";
    private final static String PIN_STYLE_CLASS_FIRST = "first";
    private final static String PIN_STYLE_CLASS_MIDDLE = "middle";
    private final static String PIN_STYLE_CLASS_LAST = "last";

    //todo fields for events
    private final ObjectProperty<Point2D> wrappedCoordinatesForMouseGliding = new SimpleObjectProperty<Point2D>(Point2D.ZERO);

    /**
     * WaypointsManager constructor.
     * @param g JaVelo Graph instance
     * @param jProperty MapViewParameters wrapped into a JavaFx property
     * @param tPList ObservableList of Waypoint(s)
     * @param errorR Object for reporting errors
     */

    public WaypointsManager(Graph g, ObjectProperty<MapViewParameters> jProperty, ObservableList<Waypoint> tPList, Consumer<String> errorR){
        GRAPH = g;
        MAP_VIEW_PARAMETERS_WRAPPED = jProperty;
        TRANSIT_POINTS_LIST = tPList;
        ERROR_REPORTER = errorR;
        PANE = new Pane();
        PANE.setPickOnBounds(false);
    }

    /**
     * Returns the pane containing the waypoints
     * @return Pane the pane containing the waypoints
     */

    public Pane pane() {
        draw(); //todo pourquoi draw ici ?
        return PANE;
    }

    //add a waypoint to the pane
    private void addWaypointPane(Waypoint w, String pinStyleClassPosition){
        MapViewParameters mapViewParameters = MAP_VIEW_PARAMETERS_WRAPPED.get();

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
        group.getStyleClass().add(pinStyleClassPosition);

        PANE.getChildren().add(group);


        //todo rajouter gestionnaire sur les groupes ici ? Faire des méthodes privées !
        //delete on mouse click
        group.setOnMouseClicked(mouseEvent -> {
            TRANSIT_POINTS_LIST.remove(w);
        });

        //Mouse gliding
        group.setOnMousePressed(mouseEvent -> {
            wrappedCoordinatesForMouseGliding.set(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
        });

        group.setOnMouseDragged(mouseEvent -> {
            /*Point2D deltaVector = (new Point2D(mouseEvent.getX(), mouseEvent.getY()))
                    .subtract(wrappedCoordinatesForMouseGliding.get());
            group.setLayoutX(mapViewParameters.viewX(PointWebMercator.ofPointCh(w.point())) + deltaVector.getX()); //todo faire plus propre
            group.setLayoutY(mapViewParameters.viewY(PointWebMercator.ofPointCh(w.point())) + deltaVector.getY());*/

            Point2D deltaVector = (new Point2D(mouseEvent.getX(), mouseEvent.getY()))
                    .subtract(wrappedCoordinatesForMouseGliding.get());
            group.setLayoutX(group.getLayoutX() + deltaVector.getX()); //todo faire plus propre
            group.setLayoutY(group.getLayoutY() + deltaVector.getY());

            wrappedCoordinatesForMouseGliding.set(new Point2D(group.getLayoutX(), group.getLayoutY()));
            draw();
        });

        group.setOnMousePressed(mouseEvent -> {
            if(!mouseEvent.isStillSincePress()){
                Point2D deltaVector = (new Point2D(mouseEvent.getX(), mouseEvent.getY()))
                        .subtract(wrappedCoordinatesForMouseGliding.get());
                group.setLayoutX(group.getLayoutX() + deltaVector.getX()); //todo faire plus propre
                group.setLayoutY(group.getLayoutY() + deltaVector.getY());

                wrappedCoordinatesForMouseGliding.set(new Point2D(group.getLayoutX(), group.getLayoutY()));
            }else{
                TRANSIT_POINTS_LIST.remove(w);
            }
        });

        TRANSIT_POINTS_LIST.addListener((ListChangeListener<? super Waypoint>) change -> { //todo propre ? bizarre.
            draw();
        });

        MAP_VIEW_PARAMETERS_WRAPPED.addListener((property, oldValue, newValue) -> {

        });


    }

    /**
     * Method that draws the waypoints on the pane
     */

    public void draw(){
        PANE.getChildren().clear();

        Iterator<Waypoint> itr = TRANSIT_POINTS_LIST.iterator();
        boolean firstElementSeen = false;

        while(itr.hasNext()) {
            Waypoint w = itr.next();

            if(!firstElementSeen){
                firstElementSeen = true;
                addWaypointPane(w, PIN_STYLE_CLASS_FIRST);
            }else if(!itr.hasNext()){
                addWaypointPane(w, PIN_STYLE_CLASS_LAST);
            }else{
                addWaypointPane(w, PIN_STYLE_CLASS_MIDDLE);
            }
        }
    }

    /**
     * Adds a new waypoint at the given position in the map's coordinate system
     * @param x x position in the map coordinate system
     * @param y y position in the map coordinate system
     */

    public void addWaypointMap(double x, double y){
        PointCh waypointLocalisation = MAP_VIEW_PARAMETERS_WRAPPED.get().pointAt(x, y).toPointCh();
        int nearestNodeInRadius = GRAPH.nodeClosestTo(waypointLocalisation, SEARCH_DISTANCE);

        if(nearestNodeInRadius != -1){
            Waypoint newWaypoint = new Waypoint(waypointLocalisation, nearestNodeInRadius);

            //remove PIN_STYLE_CLASS_LAST from the previous last waypoint
            PANE.getChildren().get(TRANSIT_POINTS_LIST.size() - 1).getStyleClass().remove(PIN_STYLE_CLASS_LAST);

            //add the new waypoint to the list and draw it on the pane
            TRANSIT_POINTS_LIST.add(newWaypoint);
            addWaypointPane(newWaypoint, PIN_STYLE_CLASS_LAST);
        }else{
            System.out.println("Aucun noeud à proximité");
        }
    }
}
