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
    private final ObjectProperty<MapViewParameters> mapViewParametersWrapped;
    private final ObservableList<Waypoint> transitPointsList;
    private final Consumer<String> errorReporter;

    //pane containing the waypoints
    private final Pane pane;

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

    private static final String ERROR_MESSAGE_NO_ROUTES_AROUND = "Aucune route à proximité !";

    private final ObjectProperty<Point2D> latestMousePosition = new SimpleObjectProperty<>(Point2D.ZERO);

    /**
     * WaypointsManager constructor.
     *
     * @param graph                    JaVelo Graph instance
     * @param mapViewParametersWrapped MapViewParameters wrapped into a JavaFx property
     * @param transitPointsList        ObservableList of Waypoint(s)
     * @param errorReporter            Object for reporting errors
     */

    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParametersWrapped,
                            ObservableList<Waypoint> transitPointsList, Consumer<String> errorReporter) {
        this.graph = graph;
        this.mapViewParametersWrapped = mapViewParametersWrapped;
        this.transitPointsList = transitPointsList;
        this.errorReporter = errorReporter;
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
     * @param x        x position in the map coordinate system
     * @param y        y position in the map coordinate system
     * @param position position of the waypoint in the TRANSIT_POINTS_LIST
     *                 if -1 it is added at the else at the position requested
     */

    public void addWaypoint(double x, double y, int position) {
        PointCh waypointLocalisation = mapViewParametersWrapped.get().pointAt(x, y).toPointCh();
        int nearestNodeInRadius = graph.nodeClosestTo(waypointLocalisation, SEARCH_DISTANCE);

        if (nearestNodeInRadius != -1) {
            Waypoint newWaypoint = new Waypoint(waypointLocalisation, nearestNodeInRadius);

            //add the new waypoint to the list and draw it on the pane
            if (position == CREATE_WAYPOINT_POSITION) {
                transitPointsList.add(newWaypoint);
            } else {
                transitPointsList.set(position, newWaypoint);
            }
        } else {
            errorReporter.accept(ERROR_MESSAGE_NO_ROUTES_AROUND);
            //we need to move waypoint to its old position
            updatePinsPosition();
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
        MapViewParameters mapViewParameters = mapViewParametersWrapped.get();

        int positionInWaypointsList = 0;
        for (Node g : pane.getChildren()) {
            //the pane should only contain nodes that are groups but Pane isn't immutable, so we check in case
            //it was modified
            if (g instanceof Group) {
                Waypoint w = transitPointsList.get(positionInWaypointsList);

                g.setLayoutX(mapViewParameters.viewX(PointWebMercator.ofPointCh(w.point())));
                g.setLayoutY(mapViewParameters.viewY(PointWebMercator.ofPointCh(w.point())));

                ++positionInWaypointsList;
            }
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
    private void initializeGroupListeners(Waypoint w, Group group) {
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
            if (!mouseEvent.isStillSincePress()) {
                //waypoint released and was moved since pressed, update waypoint position
                addWaypoint(group.getLayoutX(), group.getLayoutY(), transitPointsList.indexOf(w));
            } else {
                //mouse didn't move from the position it was pressed at, remove the waypoint
                removeWaypoint(w);
            }
        });
    }

    //initialize listener to MapViewParameters and TRANSIT_POINT_LIST changes
    private void initializeListeners() {
        mapViewParametersWrapped.addListener((property, oldValue, newValue) -> updatePinsPosition());
        transitPointsList.addListener((ListChangeListener<Waypoint>) change -> draw());
    }
}