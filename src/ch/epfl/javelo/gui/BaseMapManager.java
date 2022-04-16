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

    private final TileManager tileManager;
    private final WaypointsManager waypointsManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersWrapped;

    //will be true if redraw is needed
    private boolean redrawNeeded;

    //pane of the map
    private final Pane pane;

    //canvas of the pane
    private final Canvas canvas;

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
        tileManager = tileM;
        waypointsManager = waypointsM;
        mapViewParametersWrapped = mapParamsWrapped;
        lastHeight = 0.0;
        lastWidth = 0.0;

        canvas = new Canvas();
        pane = new Pane();

        pane.getChildren().add(canvas);

        //when the pane width or height will change -> canvas width and height will update accordingly
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
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
        return pane;
    }

    //Method that draws the map background
    private void draw() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        //TODO: est ce que c'est vraiment nécessaire de clear le canvas?
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        MapViewParameters mapViewParameters = mapViewParametersWrapped.get();

        //index of the top left tile
        int topLeftXIndexTile = (int) (mapViewParameters.indexTopLeftX() / 256);
        int topLeftYIndexTile = (int) (mapViewParameters.indexTopLeftY() / 256);

        //index of the bottom right tile
        int bottomRightXIndexTile = (int) ((mapViewParameters.indexTopLeftX() + canvas.getWidth()) / 256);
        int bottomRightYIndexTile = (int) ((mapViewParameters.indexTopLeftY() + canvas.getHeight()) / 256);

        for (int i = topLeftXIndexTile; i <= bottomRightXIndexTile; i++) {
            for (int j = topLeftYIndexTile; j <= bottomRightYIndexTile; j++) {
                try {
                    //get the image corresponding to each tile displayed (at least partially) on the map portion
                    Image image = tileManager.imageForTileAt(new TileManager.TileId(mapViewParameters.zoomLevel(), i, j));

                    //draw the image to the corresponding position using the topLeft point
                    graphicsContext.drawImage(image, i * 256 - mapViewParameters.indexTopLeftX(), j * 256 - mapViewParameters.indexTopLeftY());
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }

        //draw the waypoints
        waypointsManager.draw();
    }

    //if the windows properties changed, redraw on next pulse
    private void redrawIfNeeded() {
        //dimensions changed
        if (!(canvas.getHeight() == lastHeight && canvas.getWidth() == lastWidth)) {
            lastWidth = canvas.getWidth();
            lastHeight = canvas.getHeight();
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
