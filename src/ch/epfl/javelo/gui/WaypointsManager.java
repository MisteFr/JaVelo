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
import javafx.scene.Node;
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
    private final ObjectProperty<MapViewParameters> mapViewParametersProperty;
    private final ObservableList<Waypoint> transitPointsList;
    private final Consumer<String> errorManager;

    //pane containing the waypoints
    private final Pane pane;

    private final ObjectProperty<Point2D> latestMousePosition;

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

    private static final String ERROR_MESSAGE_NO_ROUTES_AROUND = "Aucune route à proximité !";

    /**
     * WaypointsManager constructor.
     *
     * @param g                               JaVelo Graph instance
     * @param mapViewParametersObjectProperty MapViewParameters wrapped into a JavaFx property
     * @param transitPList                    ObservableList of Waypoint(s)
     * @param errorReporter                   Object for reporting errors
     */

    public WaypointsManager(Graph g, ObjectProperty<MapViewParameters> mapViewParametersObjectProperty,
                            ObservableList<Waypoint> transitPList, Consumer<String> errorReporter) {
        graph = g;
        mapViewParametersProperty = mapViewParametersObjectProperty;
        transitPointsList = transitPList;
        errorManager = errorReporter;

        latestMousePosition = new SimpleObjectProperty<>(Point2D.ZERO);

        pane = new Pane();
        pane.setPickOnBounds(false);

        initializeListeners();
        draw();
    }

    /**
     * Returns the pane containing the waypoints
     *
     * @return Pane the pane containing the waypoints
     */

    public Pane pane() {
        return pane;
    }

    /**
     * Adds a new waypoint at the given position in the map's coordinate system
     *
     * @param x x position in the map coordinate system
     * @param y y position in the map coordinate system
     */

    public void addWaypoint(double x, double y) {
        Waypoint newWaypoint = createWaypoint(x, y);

        if (newWaypoint != null) {
            transitPointsList.add(newWaypoint);
        } else {
            errorManager.accept(ERROR_MESSAGE_NO_ROUTES_AROUND);
        }
    }

    //create an instance of waypoint for the given coordinates or return null
    private Waypoint createWaypoint(double x, double y) {
        PointCh waypointLocalisation = mapViewParametersProperty.get().pointAt(x, y).toPointCh();

        if (waypointLocalisation != null
                && graph.nodeClosestTo(waypointLocalisation, SEARCH_DISTANCE) != -1) {

            int nearestNodeInRadius = graph.nodeClosestTo(waypointLocalisation, SEARCH_DISTANCE);

            return new Waypoint(waypointLocalisation, nearestNodeInRadius);
        } else {
            return null;
        }
    }

    //remove a Waypoint from the map
    private void removeWaypoint(Waypoint w) {
        transitPointsList.remove(w);
    }

    //draw waypoints on the pane
    private void draw() {
        pane.getChildren().clear();

        Iterator<Waypoint> itr = transitPointsList.iterator();
        boolean firstElementSeen = false;

        while (itr.hasNext()) {
            Waypoint w = itr.next();

            String pinStyleClassPosition;

            if (!firstElementSeen) {
                firstElementSeen = true;
                pinStyleClassPosition = PIN_STYLE_CLASS_FIRST;
            } else if (!itr.hasNext()) {
                pinStyleClassPosition = PIN_STYLE_CLASS_LAST;
            } else {
                pinStyleClassPosition = PIN_STYLE_CLASS_MIDDLE;
            }

            Group groupWaypoint = createGroupForWaypoint(pinStyleClassPosition);

            //add the group to the Pane and initialize listeners
            pane.getChildren().add(groupWaypoint);
            initializeGroupListeners(w, groupWaypoint);
        }
        updatePinsPosition();
    }

    //Update the position of the pins on the Pane
    private void updatePinsPosition() {
        MapViewParameters mapViewParameters = mapViewParametersProperty.get();

        int positionInWaypointsList = 0;
        for (Node g : pane.getChildren()) {
            Waypoint w = transitPointsList.get(positionInWaypointsList);

            g.setLayoutX(mapViewParameters.viewX(PointWebMercator.ofPointCh(w.point())));
            g.setLayoutY(mapViewParameters.viewY(PointWebMercator.ofPointCh(w.point())));

            ++positionInWaypointsList;
        }
    }

    //Create JavaFX Group for a Waypoint w with a special pinStyleClassPosition
    //corresponding to the position of the waypoint
    private Group createGroupForWaypoint(String pinStyleClassPosition) {
        SVGPath svgpath_outside = new SVGPath();
        SVGPath svgpath_inside = new SVGPath();


        svgpath_outside.getStyleClass().add(OUTSIDE_PIN_STYLE_CLASS);
        svgpath_outside.setContent(OUTSIDE_PIN_PATH);
        svgpath_inside.getStyleClass().add(INSIDE_PIN_STYLE_CLASS);
        svgpath_inside.setContent(INSIDE_PIN_PATH);

        Group group = new Group(svgpath_outside, svgpath_inside);

        group.getStyleClass().add(PIN_STYLE_CLASS);
        group.getStyleClass().add(pinStyleClassPosition);

        return group;
    }

    //initialize event listeners for the waypoint's group
    private void initializeGroupListeners(Waypoint waypoint, Group group) {

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
            if (!mouseEvent.isStillSincePress()) {
                //waypoint released and was moved since pressed, update waypoint position

                Waypoint newWaypoint =
                        createWaypoint(group.getLayoutX(), group.getLayoutY());

                if (newWaypoint != null) {
                    transitPointsList.set(transitPointsList.indexOf(waypoint), newWaypoint);
                } else {
                    errorManager.accept(ERROR_MESSAGE_NO_ROUTES_AROUND);
                    //to be sure waypoints are replaced at the correct position
                    draw();
                }

            }
        });

        group.setOnMouseClicked(mouseEvent -> {
            //mouse didn't move from the position it was pressed at, remove the waypoint
            if (mouseEvent.isStillSincePress())
                removeWaypoint(waypoint);
        });
    }

    //initialize listener to MapViewParameters and TRANSIT_POINT_LIST changes
    private void initializeListeners() {
        mapViewParametersProperty.addListener((p, o, n) -> updatePinsPosition());
        transitPointsList.addListener((ListChangeListener<Waypoint>) change -> draw());
    }
}