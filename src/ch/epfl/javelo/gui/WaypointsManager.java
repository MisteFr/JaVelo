package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

    //search distance for the radius of the nearestNode function when adding a waypoint
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

    public final static int CREATE_WAYPOINT_POSITION = -1;

    private final ObjectProperty<Point2D> latestMousePosition = new SimpleObjectProperty<Point2D>(Point2D.ZERO);

    /**
     * WaypointsManager constructor.
     * @param graph JaVelo Graph instance
     * @param javaProperty MapViewParameters wrapped into a JavaFx property
     * @param transitPointsList ObservableList of Waypoint(s)
     * @param errorReporter Object for reporting errors
     */

    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> javaProperty, ObservableList<Waypoint> transitPointsList, Consumer<String> errorReporter){
        GRAPH = graph;
        MAP_VIEW_PARAMETERS_WRAPPED = javaProperty;
        TRANSIT_POINTS_LIST = transitPointsList;
        ERROR_REPORTER = errorReporter;
        PANE = new Pane();
        PANE.setPickOnBounds(false);

        initializeMapParametersListener();
    }

    /**
     * Returns the pane containing the waypoints
     * @return Pane the pane containing the waypoints
     */

    public Pane pane() {
        return PANE;
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

            String pinStyleClassPosition;

            if(!firstElementSeen){
                firstElementSeen = true;
                pinStyleClassPosition = PIN_STYLE_CLASS_FIRST;
            }else if(!itr.hasNext()){
                pinStyleClassPosition = PIN_STYLE_CLASS_LAST;
            }else{
                pinStyleClassPosition = PIN_STYLE_CLASS_MIDDLE;
            }

            Group groupWaypoint = createGroupForWaypoint(w, pinStyleClassPosition);

            //add the group to the Pane and initialize listeners
            PANE.getChildren().add(groupWaypoint);
            initializeGroupListeners(w, groupWaypoint);
        }
    }

    /**
     * Adds a new waypoint at the given position in the map's coordinate system
     * @param x x position in the map coordinate system
     * @param y y position in the map coordinate system
     * @param position position of the waypoint in the TRANSIT_POINTS_LIST
     *                 if -1 it is added at the else at the position requested
     */

    public void addWaypoint(double x, double y, int position){
        PointCh waypointLocalisation = MAP_VIEW_PARAMETERS_WRAPPED.get().pointAt(x, y).toPointCh();
        int nearestNodeInRadius = GRAPH.nodeClosestTo(waypointLocalisation, SEARCH_DISTANCE);

        if(nearestNodeInRadius != -1){
            Waypoint newWaypoint = new Waypoint(waypointLocalisation, nearestNodeInRadius);

            //add the new waypoint to the list and draw it on the pane
            if(position == -1){
                TRANSIT_POINTS_LIST.add(newWaypoint);
            }else{
                TRANSIT_POINTS_LIST.set(position, newWaypoint);
            }
        }else{
            ERROR_REPORTER.accept("Aucune route à proximité !");
        }

        //we need to draw even if no nodes were found in radius
        //to reset waypoint to its old position
        draw();
    }

    //remove a Waypoint from the map
    private void removeWaypoint(Waypoint w){
        TRANSIT_POINTS_LIST.remove(w);
        draw();
    }

    //Create JavaFX Group for a Waypoint w with a special pinStyleClassPosition
    //corresponding to the position of the waypoint
    private Group createGroupForWaypoint(Waypoint w, String pinStyleClassPosition){
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

        return group;
    }

    //initialize event listeners for the waypoint's group
    private void initializeGroupListeners(Waypoint w, Group group){
        //Mouse gliding
        group.setOnMousePressed(mouseEvent -> {
            latestMousePosition.set(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
        });

        //Mouse dragging
        group.setOnMouseDragged(mouseEvent -> {
            double xTranslation = mouseEvent.getX() - latestMousePosition.get().getX();
            double yTranslation = mouseEvent.getY() - latestMousePosition.get().getY();

            group.setLayoutX(group.getLayoutX() + xTranslation);
            group.setLayoutY(group.getLayoutY() + yTranslation);
        });

        //Mouse released
        group.setOnMouseReleased(mouseEvent -> {
            if(!mouseEvent.isStillSincePress()){
                //waypoint released and was moved since pressed, update waypoint position
                addWaypoint(group.getLayoutX(), group.getLayoutY(), TRANSIT_POINTS_LIST.indexOf(w));
            }else{
                //mouse didn't move from the position it was pressed at, remove the waypoint
                removeWaypoint(w);
            }
        });
    }

    //initialize listener to MapViewParameters changes, redraw only if zoom level changed
    private void initializeMapParametersListener(){
        MAP_VIEW_PARAMETERS_WRAPPED.addListener((property, oldValue, newValue) ->{
            if(oldValue.zoomLevel() != newValue.zoomLevel()){
                System.out.println(oldValue);
                System.out.println(newValue);
                draw();
            }
        });
    }
}
