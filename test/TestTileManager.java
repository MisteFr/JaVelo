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
                new TileManager.TileId(19, 271725, 185422, tm.getServerConfiguration()));

        for(int y = 271725; y < (271725+101); y++){
            //I checked that the memoryCache was working as intended
           tm.imageForTileAt(new TileManager.TileId(19, 271725, y, tm.getServerConfiguration()));
        }
        Platform.exit();
    }

    @Test
    void testValidityTileId(){
        TileManager tm = new TileManager(
                Path.of("cache"), "tile.openstreetmap.org");
        assertThrows(IllegalArgumentException.class, () -> {
            new TileManager.TileId(1, 2, 1, tm.getServerConfiguration());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TileManager.TileId(1, 1, 2, tm.getServerConfiguration());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TileManager.TileId(2, 3, 4, tm.getServerConfiguration());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TileManager.TileId(2, 4, 3, tm.getServerConfiguration());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TileManager.TileId(-1, 4, 3, tm.getServerConfiguration());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TileManager.TileId(2, -3, 3, tm.getServerConfiguration());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TileManager.TileId(2, 2, -3, tm.getServerConfiguration());
        });


        new TileManager.TileId(1, 1, 1, tm.getServerConfiguration());
        new TileManager.TileId(2, 3, 3, tm.getServerConfiguration());
    }
}
