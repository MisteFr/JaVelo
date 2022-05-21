package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * BaseMapManager class
 * Manages the display and interaction with the background map.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class BaseMapManager {

    private final TileManager tileManager;
    private final WaypointsManager waypointsManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersProperty;

    //pane of the map
    private final Pane pane;

    //canvas of the pane
    private final Canvas canvas;

    //will be true if redraw is needed
    private boolean redrawNeeded;

    //size of an OSM Tile in pixels
    private final static int OSM_TILE_SIZE = 256;

    private final static int MINIMUM_ZOOM_LEVEL = 8;
    private final static int MAXIMUM_ZOOM_LEVEL = 19;

    private final static int SCALB_CONSTANT_FOR_ZOOM = 1;

    private static final String ZOOM_IN_TEXT = "+";
    private static final String ZOOM_OUT_TEXT = "-";
    private static final int ZOOM_IN_X_TRANSLATION = 5;
    private static final int ZOOM_IN_Y_TRANSLATION = 10;
    private static final int ZOOM_OUT_Y_TRANSLATION = 45;
    private static final String ZOOM_BUTTON_STYLE_CLASS = "zoom_button";

    /**
     * BaseMapManager constructor
     *
     * @param tileM            TileManager to get the tiles from the map
     * @param waypointsM       WaypointManager
     * @param mapParamsWrapped MapViewParameters wrapped into a JavaFx property
     */

    public BaseMapManager(TileManager tileM, WaypointsManager waypointsM, ObjectProperty<MapViewParameters> mapParamsWrapped) {
        tileManager = tileM;
        waypointsManager = waypointsM;
        mapViewParametersProperty = mapParamsWrapped;

        canvas = new Canvas();
        pane = new Pane();

        pane.getChildren().add(canvas);


        installHandlers();
        installBindings();
        installListeners();
        installZoomButtons();

        //initial draw
        redrawOnNextPulse();
    }

    /**
     * Returns the JavaFX pane displaying the background map
     *
     * @return Pane
     */

    public Pane pane() {
        return pane;
    }

    //Method that draws the map background
    private void draw() {
        //first clear the graphic context
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        MapViewParameters mapViewParameters = mapViewParametersProperty.get();

        //index of the top left tile
        int topLeftXIndexTile = (int) (mapViewParameters.indexTopLeftX() / OSM_TILE_SIZE);
        int topLeftYIndexTile = (int) (mapViewParameters.indexTopLeftY() / OSM_TILE_SIZE);

        //index of the bottom right tile
        int bottomRightXIndexTile = (int) ((mapViewParameters.indexTopLeftX() + canvas.getWidth()) / OSM_TILE_SIZE);
        int bottomRightYIndexTile = (int) ((mapViewParameters.indexTopLeftY() + canvas.getHeight()) / OSM_TILE_SIZE);

        for (int xTileMap = topLeftXIndexTile; xTileMap <= bottomRightXIndexTile; xTileMap++) {
            for (int yTileMap = topLeftYIndexTile; yTileMap <= bottomRightYIndexTile; yTileMap++) {
                try {
                    //get the image corresponding to each tile displayed (at least partially) on the map portion
                    Image image = tileManager.imageForTileAt(new TileManager.TileId(mapViewParameters.zoomLevel(),
                            xTileMap,
                            yTileMap));


                    //draw the image to the corresponding position using the topLeft point
                    graphicsContext.drawImage(image,
                            xTileMap * OSM_TILE_SIZE - mapViewParameters.indexTopLeftX(),
                            yTileMap * OSM_TILE_SIZE - mapViewParameters.indexTopLeftY());
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    //if the windows properties changed, redraw on next pulse
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        draw();
    }

    //redraw on next pulse
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    //from a delta Point2D vector and the current MapViewParameters object, creates the new MapViewParameters when moving.
    private MapViewParameters newMapViewParametersWhenMoving(Point2D delta, MapViewParameters oldMapViewParameters) {
        return oldMapViewParameters.withMinXY(
                oldMapViewParameters.indexTopLeftX() - delta.getX(),
                oldMapViewParameters.indexTopLeftY() - delta.getY()
        );
    }


    //installs the handlers in the constructor
    private void installHandlers() {
        ObjectProperty<Point2D> mouseCoordinatesProperty = new SimpleObjectProperty<>(Point2D.ZERO);
        SimpleLongProperty minScrollTime = new SimpleLongProperty();

        pane.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.isStillSincePress()) {
                waypointsManager.addWaypoint(mouseEvent.getX(), mouseEvent.getY());
            }
        });

        //event handler for movement of the map
        pane.setOnMousePressed(mouseEvent -> mouseCoordinatesProperty.setValue(
                new Point2D(mouseEvent.getX(), mouseEvent.getY())));


        pane.setOnMouseDragged(mouseEvent -> {
            Point2D delta = new Point2D(mouseEvent.getX(), mouseEvent.getY()).subtract(mouseCoordinatesProperty.get());

            mapViewParametersProperty.set(newMapViewParametersWhenMoving(delta, mapViewParametersProperty.get()));

            mouseCoordinatesProperty.setValue(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
        });


        //if the mouse did not move since press, create waypoint
        pane.setOnMouseReleased(mouseEvent -> {
            Point2D delta = new Point2D(mouseEvent.getX(), mouseEvent.getY()).subtract(mouseCoordinatesProperty.get());
            mapViewParametersProperty.set(newMapViewParametersWhenMoving(delta, mapViewParametersProperty.get()));
        });


        //even handler for zooming in and out by scrolling
        pane.setOnScroll(scrollEvent -> {

            if (scrollEvent.getDeltaY() == 0d) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);
            int zoomDelta = (int) Math.signum(scrollEvent.getDeltaY());

            MapViewParameters oldMapViewParameters = mapViewParametersProperty.get();
            int newZoomLevel = Math2.clamp(MINIMUM_ZOOM_LEVEL,
                    oldMapViewParameters.zoomLevel() + zoomDelta,
                    MAXIMUM_ZOOM_LEVEL);

            if (!(oldMapViewParameters.zoomLevel() == newZoomLevel)) {
                double multiplicativeFactorForZoom = Math.scalb(SCALB_CONSTANT_FOR_ZOOM
                        , newZoomLevel - oldMapViewParameters.zoomLevel());

                MapViewParameters newMapViewParameters = new MapViewParameters(
                        newZoomLevel,
                        multiplicativeFactorForZoom * (oldMapViewParameters.indexTopLeftX()
                                + scrollEvent.getX()) - scrollEvent.getX(),
                        multiplicativeFactorForZoom * (oldMapViewParameters.indexTopLeftY()
                                + scrollEvent.getY()) - scrollEvent.getY()
                );

                mapViewParametersProperty.setValue(newMapViewParameters);
            }
        });
    }


    //installs the bindings in the constructor
    private void installBindings() {
        //when the pane width or height will change -> canvas width and height will update accordingly
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
    }


    //installs the listeners on the canvas and the mapviewparameters
    private void installListeners() {
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        //if the height or the width of the canvas change -> redraw
        canvas.widthProperty().addListener((p, o, n) -> redrawOnNextPulse());
        canvas.heightProperty().addListener((p, o, n) -> redrawOnNextPulse());

        //when map properties are changed, redraw
        mapViewParametersProperty.addListener((p, o, n) -> redrawOnNextPulse());
    }

    //add zoom buttons to the pane and initialize its handlers
    private void installZoomButtons() {
        Button buttonZoomIn = new Button(ZOOM_IN_TEXT);
        Button buttonZoomOut = new Button(ZOOM_OUT_TEXT);

        buttonZoomIn.setTranslateX(ZOOM_IN_X_TRANSLATION);
        buttonZoomIn.setTranslateY(ZOOM_IN_Y_TRANSLATION);

        buttonZoomOut.setTranslateX(ZOOM_IN_X_TRANSLATION);
        buttonZoomOut.setTranslateY(ZOOM_OUT_Y_TRANSLATION);

        buttonZoomIn.setPadding(Insets.EMPTY);
        buttonZoomOut.setPadding(Insets.EMPTY);

        buttonZoomIn.getStyleClass().add(ZOOM_BUTTON_STYLE_CLASS);
        buttonZoomOut.getStyleClass().add(ZOOM_BUTTON_STYLE_CLASS);

        buttonZoomIn.setOnMouseClicked(mouseEvent -> {
            zoom(1);
        });

        buttonZoomOut.setOnMouseClicked(mouseEvent -> {
            zoom(-1);
        });

        pane.getChildren().add(buttonZoomIn);
        pane.getChildren().add(buttonZoomOut);
    }

    private void zoom(int zoomDelta) {
        MapViewParameters oldMapViewParameters = mapViewParametersProperty.get();
        int newZoomLevel = Math2.clamp(MINIMUM_ZOOM_LEVEL,
                oldMapViewParameters.zoomLevel() + zoomDelta,
                MAXIMUM_ZOOM_LEVEL);

        if (!(oldMapViewParameters.zoomLevel() == newZoomLevel)) {
            double multiplicativeFactorForZoom = (zoomDelta == 1) ? 2.0 : 0.5;

            MapViewParameters newMapViewParameters = new MapViewParameters(
                    newZoomLevel,
                    multiplicativeFactorForZoom * (oldMapViewParameters.indexTopLeftX()
                            + pane.getWidth() / 2) - pane.getWidth() / 2,
                    multiplicativeFactorForZoom * (oldMapViewParameters.indexTopLeftY()
                            + pane.getHeight() / 2) - pane.getHeight() / 2
            );

            mapViewParametersProperty.setValue(newMapViewParameters);
        }
    }

}