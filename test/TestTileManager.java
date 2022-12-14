import ch.epfl.javelo.gui.TileManager;
import ch.epfl.javelo.projection.PointCh;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TestTileManager extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TileManager tm = new TileManager(
                Path.of("cache"), "tile.openstreetmap.org");
        Image tileImage = tm.imageForTileAt(
                new TileManager.TileId(19, 271725, 185422));

        for(int y = 271725; y < (271725+101); y++){
            //I checked that the memoryCache was working as intended
           tm.imageForTileAt(new TileManager.TileId(19, 271725, y));
        }
        Platform.exit();
    }

    @Test
    void testValidityTileId(){
        assertThrows(IllegalArgumentException.class, () -> {
            new TileManager.TileId(1, 2, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TileManager.TileId(1, 1, 2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TileManager.TileId(2, 3, 4);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TileManager.TileId(2, 4, 3);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TileManager.TileId(-1, 4, 3);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TileManager.TileId(2, -3, 3);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TileManager.TileId(2, 2, -3);
        });


        new TileManager.TileId(1, 1, 1);
        new TileManager.TileId(2, 3, 3);
    }
}
