package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;

import java.io.IOException;

/**
 * BaseMapManager class
 * Manages the display and interaction with the background map.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class BaseMapManager {

    private final TileManager TILE_MANAGER;
    private final WaypointsManager WAYPOINTS_MANAGER;
    private final ObjectProperty<MapViewParameters> MAP_VIEW_PARAMETERS_WRAPPED;

    //size of an OSM Tile in pixels
    private final static int OSM_TILE_SIZE = 256;

    //pane of the map
    private final Pane PANE;

    //canvas of the pane
    private final Canvas CANVAS;

    //will be true if redraw is needed
    private boolean redrawNeeded;

    //last width saved
    private double lastWidth;

    //last height saved
    private double lastHeight;

    /**
     * BaseMapManager constructor
     * @param tileM TileManager to get the tiles from the map
     * @param waypointsM WaypointManager
     * @param mapParamsWrapped MapViewParameters wrapped into a JavaFx property
     */

    public BaseMapManager(TileManager tileM, WaypointsManager waypointsM, ObjectProperty<MapViewParameters> mapParamsWrapped) {
        TILE_MANAGER = tileM;
        WAYPOINTS_MANAGER = waypointsM;
        MAP_VIEW_PARAMETERS_WRAPPED = mapParamsWrapped;
        lastHeight = 0.0;
        lastWidth = 0.0;

        CANVAS = new Canvas();
        PANE = new Pane();

        PANE.getChildren().add(CANVAS);

        //when the pane width or height will change -> canvas width and height will update accordingly
        CANVAS.widthProperty().bind(PANE.widthProperty());
        CANVAS.heightProperty().bind(PANE.heightProperty());

        CANVAS.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        //initial draw
        redrawOnNextPulse();
    }

    /**
     * Returns the JavaFX pane displaying the background map
     * @return Pane
     */

    public Pane pane() {
        return PANE;
    }

    //Method that draws the map background
    private void draw() {
        GraphicsContext graphicsContext = CANVAS.getGraphicsContext2D();
        //TODO: est ce que c'est vraiment nécessaire de clear le canvas?
        graphicsContext.clearRect(0, 0, CANVAS.getWidth(), CANVAS.getHeight());

        MapViewParameters mapViewParameters = MAP_VIEW_PARAMETERS_WRAPPED.get();

        //index of the top left tile
        int topLeftXIndexTile = (int) (mapViewParameters.indexTopLeftX() / OSM_TILE_SIZE);
        int topLeftYIndexTile = (int) (mapViewParameters.indexTopLeftY() / OSM_TILE_SIZE);

        //index of the bottom right tile
        int bottomRightXIndexTile = (int) ((mapViewParameters.indexTopLeftX() + CANVAS.getWidth()) / OSM_TILE_SIZE);
        int bottomRightYIndexTile = (int) ((mapViewParameters.indexTopLeftY() + CANVAS.getHeight()) / OSM_TILE_SIZE);

        for (int i = topLeftXIndexTile; i <= bottomRightXIndexTile; i++) {
            for (int j = topLeftYIndexTile; j <= bottomRightYIndexTile; j++) {
                try {
                    //get the image corresponding to each tile displayed (at least partially) on the map portion
                    Image image = TILE_MANAGER.imageForTileAt(new TileManager.TileId(mapViewParameters.zoomLevel(), i, j));

                    //draw the image to the corresponding position using the topLeft point
                    graphicsContext.drawImage(image, i * OSM_TILE_SIZE - mapViewParameters.indexTopLeftX(), j * OSM_TILE_SIZE - mapViewParameters.indexTopLeftY());
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }

        //draw the waypoints
        WAYPOINTS_MANAGER.draw();
    }

    //if the windows properties changed, redraw on next pulse
    private void redrawIfNeeded() {
        //dimensions changed
        if (!(CANVAS.getHeight() == lastHeight && CANVAS.getWidth() == lastWidth)) {
            lastWidth = CANVAS.getWidth();
            lastHeight = CANVAS.getHeight();
            redrawOnNextPulse();
        }

        if (!redrawNeeded) return;
        redrawNeeded = false;

        draw();
    }

    //redraw on next pulse
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
