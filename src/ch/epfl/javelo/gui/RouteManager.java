package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class RouteManager {

    private final RouteBean beanRoute;
    private final ReadOnlyObjectProperty<MapViewParameters> mapParametersProperty;
    private final Consumer<String> errorReporter;

    private final Polyline polyline;
    private final Circle circle;

    //pane containing the polyline and the circle highlighted
    private final Pane pane;

    private static final String WAYPOINT_ALREADY_EXIST_AT_POSITION_MESSAGE = "Un point de passage est déjà présent à cet endroit !";
    private static final String POLYLINE_ID = "route";
    private static final String CIRCLE_ID = "highlight";
    private static final int CIRCLE_RADIUS = 5;


    /**
     * RouteManager constructor.
     *
     * @param beanRoute     RouteBean instance
     * @param mapParameters MapViewParameters wrapped into a ReadOnlyObjectProperty
     * @param errorReporter Object for reporting errors
     */

    public RouteManager(RouteBean beanRoute, ReadOnlyObjectProperty<MapViewParameters> mapParameters,
                        Consumer<String> errorReporter) {
        this.beanRoute = beanRoute;
        this.mapParametersProperty = mapParameters;
        this.errorReporter = errorReporter;
        this.pane = new Pane();
        this.polyline = new Polyline();
        this.circle = new Circle(CIRCLE_RADIUS);

        initializePane();
        initializeListeners();
    }

    /**
     * Returns the pane containing the polyline and the highlighted circle
     *
     * @return Pane the pane containing the polyline and the highlighted circle
     */

    public Pane pane() {
        return pane;
    }

    //initialize the Pane correctly including the polyline and the highlighted circle
    private void initializePane() {
        pane.setPickOnBounds(false);

        polyline.setId(POLYLINE_ID);
        polyline.setVisible(false);

        circle.setId(CIRCLE_ID);
        circle.setVisible(false);

        pane.getChildren().add(polyline);
        pane.getChildren().add(circle);
    }

    //initialize Listeners for the mapParameters, the route, the highlightedPositionProperty and the circle
    private void initializeListeners() {
        mapParametersProperty.addListener((property, oldValue, newValue) -> {
            if (polyline.isVisible()) {
                if (oldValue.zoomLevel() == newValue.zoomLevel()) {
                    //update the position of the polyline and the circle
                    updatePositionPolyline();
                    updatePositionCircle();
                } else {
                    //update the points of the polyline
                    updatePointsPolyline();
                }
            }
        });

        beanRoute.route().addListener((property, oldValue, newValue) -> {
            if (newValue != null && newValue != oldValue) {
                //if the new route is different from the old one, update the points of the polyline
                if (!polyline.isVisible() && !circle.isVisible()) {
                    polyline.setVisible(true);
                    circle.setVisible(true);
                }

                updatePointsPolyline();
            } else if (polyline.isVisible() && circle.isVisible() && newValue == null && oldValue != null) {
                //hide the polyline and the circle
                polyline.setVisible(false);
                circle.setVisible(false);
            }
        });

        beanRoute.highlightedPositionProperty().addListener((property, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                updatePositionCircle();
            }
        });

        circle.setOnMouseClicked(mouseEvent -> {
            Point2D pInPane = circle.localToParent(mouseEvent.getX(), mouseEvent.getY());
            addWaypointOnRoute(pInPane);
        });
    }

    //add a waypoint on the route at a position
    private void addWaypointOnRoute(Point2D pInPane) {
        int nearestNodeOnRoute = beanRoute.route().get().nodeClosestTo(beanRoute.getHighlightedPosition());

        //check if a waypoint is already associated to this node, if yes abort.
        for (Waypoint w : beanRoute.waypointsProperty()) {
            if (w.nodeId() == nearestNodeOnRoute) {
                errorReporter.accept(WAYPOINT_ALREADY_EXIST_AT_POSITION_MESSAGE);
                return;
            }
        }

        int indexToInsert = beanRoute.route().get().indexOfSegmentAt(beanRoute.getHighlightedPosition()) + 1;
        PointCh waypointLocalisation = mapParametersProperty.get()
                .pointAt(pInPane.getX(), pInPane.getY()).toPointCh();
        Waypoint newWaypoint = new Waypoint(waypointLocalisation, nearestNodeOnRoute);
        beanRoute.waypointsProperty().add(indexToInsert, newWaypoint);
    }

    //update the points of the polyline and replace circle and polyline
    private void updatePointsPolyline() {
        polyline.getPoints().clear();

        MapViewParameters mapViewParameters = mapParametersProperty.get();

        //to avoid listeners of the list to be called each time we add a point
        //we won't see half polyline on the screen too
        List<Double> pointsToAdd = new ArrayList<>();
        for (PointCh point : beanRoute.route().get().points()) {
            pointsToAdd.add(PointWebMercator.ofPointCh(point)
                    .xAtZoomLevel(mapViewParameters.zoomLevel()));
            pointsToAdd.add(PointWebMercator.ofPointCh(point)
                    .yAtZoomLevel(mapViewParameters.zoomLevel()));
        }

        polyline.getPoints().setAll(pointsToAdd);

        updatePositionPolyline();
        updatePositionCircle();
    }

    //update the position of the polyline on the map
    private void updatePositionPolyline() {
        polyline.setLayoutX(-mapParametersProperty.get().indexTopLeftX());
        polyline.setLayoutY(-mapParametersProperty.get().indexTopLeftY());
    }

    //update the position of the highlighted circle on the map
    private void updatePositionCircle(){
        PointCh pointCenterHighlightedPosition = beanRoute.route().get().pointAt(beanRoute.getHighlightedPosition());
        MapViewParameters mapViewParameters = mapParametersProperty.get();

        circle.setLayoutX(mapViewParameters.viewX(PointWebMercator.ofPointCh(pointCenterHighlightedPosition)));
        circle.setLayoutY(mapViewParameters.viewY(PointWebMercator.ofPointCh(pointCenterHighlightedPosition)));
    }
}
