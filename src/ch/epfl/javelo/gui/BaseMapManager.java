package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;

import java.io.IOException;

public final class BaseMapManager {

    private final TileManager tileManager;
    private final WaypointsManager waypointsManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersWrapped;
    private boolean redrawNeeded;
    private final Canvas canvas;
    private final Pane pane;
    private double lastWidth;
    private double lastHeight;


    public BaseMapManager(TileManager tileM, WaypointsManager waypointsM, ObjectProperty<MapViewParameters> mapParamsWrapped){
        tileManager = tileM;
        waypointsManager = waypointsM;
        mapViewParametersWrapped = mapParamsWrapped;
        lastHeight = 0.0;
        lastWidth = 0.0;

        canvas = new Canvas(1200, 1200);
        pane = new Pane();

        pane.getChildren().add(canvas);

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        redrawOnNextPulse();
    }

    public Pane pane(){
        return pane;
    }

    public void draw(){
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        //TODO: est ce que c'est vraiment nécessaire de clear le canvas?
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        MapViewParameters mapViewParameters = mapViewParametersWrapped.get();

        //we want to extract all the tiles to draw from the mapViewParameters wrapped now

        try {
            //int topLeftXIndex = (int) (mapViewParameters.topLeft().getX() / 256);
            //int topLeftYIndex = (int) (mapViewParameters.topLeft().getY() / 256);


            //int topLeftXIndex = (int) (mapViewParameters.indexTopLeftX() / 256) + 1;
            int topLeftXIndex = (int) (mapViewParameters.indexTopLeftX() / 256);
            int topLeftYIndex = (int) (mapViewParameters.indexTopLeftY() / 256) + 3;

            int bottomRightXIndex = (int) (topLeftXIndex + (canvas.getWidth() / 256));
            int bottomRightYIndex = (int) (topLeftYIndex - (canvas.getHeight() / 256));

            for (int i = topLeftXIndex; i <= bottomRightXIndex; i++) {
                for (int j = bottomRightYIndex; j <= topLeftYIndex; j++) {
                    System.out.println(i + " - " + j);
                    Image image = tileManager.imageForTileAt(new TileManager.TileId(mapViewParameters.zoomLevel(), i, j));
                    graphicsContext.drawImage(image, (i - topLeftXIndex) * 256, (j - bottomRightYIndex) * 256);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        waypointsManager.draw();
    }

    private void redrawIfNeeded(){
        //dimensions changed
        if(!(canvas.getHeight() == lastHeight && canvas.getWidth() == lastWidth)){
            lastWidth = canvas.getWidth();
            lastHeight = canvas.getHeight();
            redrawOnNextPulse();
        }

        if(!redrawNeeded) return;
        redrawNeeded = false;

        draw();
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
