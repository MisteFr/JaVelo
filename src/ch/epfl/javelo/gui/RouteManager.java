package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * RouteManager class
 * Manages the display of the route and (part of) the interaction with it.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class RouteManager {

    private final RouteBean beanRoute;
    private final ReadOnlyObjectProperty<MapViewParameters> mapParametersProperty;

    private final Polyline polyline;
    private final Circle circle;
    private final Group indicators;

    //pane containing the polyline and the circle highlighted
    private final Pane pane;

    private static final String POLYLINE_ID = "route";
    private static final String CIRCLE_ID = "highlight";
    private static final int CIRCLE_RADIUS = 5;

    /**
     * RouteManager constructor.
     *
     * @param beanR         RouteBean instance
     * @param mapParameters MapViewParameters wrapped into a ReadOnlyObjectProperty
     */

    public RouteManager(RouteBean beanR, ReadOnlyObjectProperty<MapViewParameters> mapParameters) {
        beanRoute = beanR;
        mapParametersProperty = mapParameters;
        pane = new Pane();
        polyline = new Polyline();
        circle = new Circle(CIRCLE_RADIUS);
        indicators = new Group();

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

        pane.getChildren().add(indicators);
        pane.getChildren().add(polyline);
        pane.getChildren().add(circle);
    }

    //add a waypoint on the route at a position
    private void addWaypointOnRoute(Point2D pInPane) {
        int nearestNodeOnRoute = beanRoute.route().nodeClosestTo(beanRoute.highlightedPosition());

        int indexToInsert = beanRoute.indexOfNonEmptySegmentAt(beanRoute.highlightedPosition()) + 1;
        PointCh waypointLocalisation = mapParametersProperty.get()
                .pointAt(pInPane.getX(), pInPane.getY()).toPointCh();
        Waypoint newWaypoint = new Waypoint(waypointLocalisation, nearestNodeOnRoute);
        beanRoute.waypoints().add(indexToInsert, newWaypoint);
    }

    //update the points of the polyline
    private void updatePointsPolyline() {
        polyline.getPoints().clear();

        MapViewParameters mapViewParameters = mapParametersProperty.get();

        List<Double> pointsToAdd = beanRoute.route().points().stream().flatMap(
                point -> {
                    double x = PointWebMercator.ofPointCh(point)
                            .xAtZoomLevel(mapViewParameters.zoomLevel());
                    double y = PointWebMercator.ofPointCh(point)
                            .yAtZoomLevel(mapViewParameters.zoomLevel());
                    return Stream.of(x, y);
                }
        ).collect(Collectors.toList());

        polyline.getPoints().setAll(pointsToAdd);
    }

    private void updateIndicatorsList() {
        MapViewParameters mapViewParameters = mapParametersProperty.get();
        indicators.getChildren().clear();

        //TODO: changer la constante en fonction du niveau de zoom
        double factor = Math.pow(2, 12 - mapViewParameters.zoomLevel());
        for(int i = 0; i <= beanRoute.route().length(); i += 1000 * factor){
            PointCh pointCenterHighlightedPosition = beanRoute.route().pointAt(i);

            Circle circleIndicator = new Circle(CIRCLE_RADIUS);
            circleIndicator.setId("indication");

            indicators.getChildren().add(circleIndicator);


            circleIndicator.setLayoutX(mapViewParameters.viewX(PointWebMercator.ofPointCh(pointCenterHighlightedPosition)));
            circleIndicator.setLayoutY(mapViewParameters.viewY(PointWebMercator.ofPointCh(pointCenterHighlightedPosition)));
        }
    }


    //update the position of the polyline on the map
    private void updatePositionPolyline() {
        polyline.setLayoutX(-mapParametersProperty.get().indexTopLeftX());
        polyline.setLayoutY(-mapParametersProperty.get().indexTopLeftY());
    }

    //update the position of the highlighted circle on the map
    private void updatePositionCircle() {
        PointCh pointCenterHighlightedPosition = beanRoute.route().pointAt(beanRoute.highlightedPosition());
        MapViewParameters mapViewParameters = mapParametersProperty.get();

        circle.setLayoutX(mapViewParameters.viewX(PointWebMercator.ofPointCh(pointCenterHighlightedPosition)));
        circle.setLayoutY(mapViewParameters.viewY(PointWebMercator.ofPointCh(pointCenterHighlightedPosition)));
    }

    //initialize Listeners for the mapParameters, the route, the highlightedPositionProperty and the circle
    private void initializeListeners() {
        mapParametersProperty.addListener((property, oldValue, newValue) -> {
            if (polyline.isVisible()) {
                if (oldValue.zoomLevel() != newValue.zoomLevel()) {
                    updatePointsPolyline();
                }

                updatePositionPolyline();
                updatePositionCircle();

                //extensions
                updateIndicatorsList();
            }
        });

        beanRoute.routeProperty().addListener((property, oldValue, newValue) -> {
            if (newValue != null && newValue != oldValue) {
                //if the new route is different from the old one, update the points of the polyline
                if (!polyline.isVisible()) {
                    polyline.setVisible(true);
                }

                updatePointsPolyline();
                updatePositionPolyline();
                updatePositionCircle();

                //extensions
                updateIndicatorsList();
            } else if (newValue == null && oldValue != null) {
                //hide the polyline and the circle
                polyline.setVisible(false);
                circle.setVisible(false);

                indicators.getChildren().clear();
            }
        });

        beanRoute.highlightedPositionProperty().addListener((property, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                //only update the position of the circle is the route exists
                if (beanRoute.routeProperty().get() != null) {
                    if (Double.isNaN(beanRoute.highlightedPositionProperty().get())) {
                        circle.setVisible(false);
                    } else {
                        circle.setVisible(true);
                        updatePositionCircle();
                    }
                }
            }
        });

        circle.setOnMouseClicked(mouseEvent -> {
            Point2D pInPane = circle.localToParent(mouseEvent.getX(), mouseEvent.getY());
            addWaypointOnRoute(pInPane);
        });
    }
}
