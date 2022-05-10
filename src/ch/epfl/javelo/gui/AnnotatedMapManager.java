package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import java.util.function.Consumer;

import static javafx.beans.binding.Bindings.createDoubleBinding;


/**
 * Class that manages the display of the "annotated" map
 */

public final class AnnotatedMapManager {

    //pane containing the annoted map
    private final Pane pane;

    private final RouteBean routeBean;

    private final ObjectProperty<Point2D> mouseCoordinatesProperty = new SimpleObjectProperty<>(Point2D.ZERO);
    private final DoubleProperty mousePositionOnRouteProperty = new SimpleDoubleProperty(Double.NaN);

    public static final ObjectProperty<MapViewParameters> MAP_VIEW_PARAMETERS
            = new SimpleObjectProperty<>(
                    new MapViewParameters(12, 543200, 370650));

    private static final String MAP_CSS = "map.css";

    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean,
                               Consumer<String> errorReporter){

        this.routeBean = routeBean;
        RouteManager routeManager = new RouteManager(routeBean, MAP_VIEW_PARAMETERS);

        WaypointsManager waypointsManager =
                new WaypointsManager(graph,
                        MAP_VIEW_PARAMETERS,
                        routeBean.waypointsProperty(),
                        errorReporter);

        BaseMapManager baseMapManager =
                new BaseMapManager(tileManager,
                        waypointsManager,
                        MAP_VIEW_PARAMETERS);

        pane = new StackPane(baseMapManager.pane(), routeManager.pane(), waypointsManager.pane());
        pane.getStylesheets().add(MAP_CSS);

        initializeHandlers();
    }

    /**
     * Returns the pane containing the annotated map
     * @return pane containing the annotated map
     */

    public Pane pane(){
        return pane;
    }

    /**
     * Returns the property containing the position of the mouse pointer along the route
     * @return the property containing the position of the mouse pointer along the route
     */

    public ReadOnlyDoubleProperty mousePositionOnRouteProperty(){
        return mousePositionOnRouteProperty;
    }

    //initialize handlers on the pane for the mouse events
    private void initializeHandlers() {
        //update the latest position of the mouse
        pane.setOnMouseMoved(mouseEvent -> {
            mouseCoordinatesProperty.setValue(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
        });

        //mouse went out of the map
        pane.setOnMouseExited(mouseEvent -> mouseCoordinatesProperty.setValue(null));

        mousePositionOnRouteProperty.bind(createDoubleBinding(
                () -> {
                    if(routeBean.routeProperty().isNotNull().get()
                            && mouseCoordinatesProperty.isNotNull().get()){
                        PointCh point = MAP_VIEW_PARAMETERS.get().pointAt(mouseCoordinatesProperty.get().getX(),
                                mouseCoordinatesProperty.get().getY()).toPointCh();

                        //get the position of the nearest point on the route
                        RoutePoint routePoint = routeBean.route().pointClosestTo(point);

                        PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(routePoint.point());

                        //get the  coordinates of the new point on the map
                        Point2D newPointOnMap = new Point2D(MAP_VIEW_PARAMETERS.get().viewX(pointWebMercator),
                                MAP_VIEW_PARAMETERS.get().viewY(pointWebMercator));

                        if(newPointOnMap.distance(mouseCoordinatesProperty.get()) < 15){
                            return routePoint.position();
                        }else{
                            return Double.NaN;
                        }
                    }else{
                        return Double.NaN;
                    }
                }, mouseCoordinatesProperty, MAP_VIEW_PARAMETERS, routeBean.routeProperty()
        ));
    }
}
