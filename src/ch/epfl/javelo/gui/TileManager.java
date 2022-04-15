package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

/**
 * TileManager Class
 * Represents an OSM tile manager. Its role is to get tiles from a tile server and store them in a memory cache and a disk cache.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class TileManager {

    //Memory cache capacity
    private final static int CACHE_CAPACITY = 100;

    //Memory cache array with access-order
    //Load factor is one as capacity won't be increased
    private final LinkedHashMap<TileId, Image> memoryCache = new LinkedHashMap<>(CACHE_CAPACITY, 1f, true);

    //Path to the memory disk folder
    private final Path pathToMemoryDisk;

    //Name of the tile server where are stored tile images
    private final String tileServerName;

    /**
     * Constructor of TileManager
     * @param pathFolder path of the disk cache
     * @param tileServerN name of the tile server
     */
    public TileManager(Path pathFolder, String tileServerN) {
        pathToMemoryDisk = pathFolder;
        tileServerName = tileServerN;
    }

    /**
     * Add a pair of tileId and its corresponding image to the memory cache
     * If the memory cache, we remove the first pair
     *
     * @param tileIdentity identity of the tile associated to the image
     * @param image        image of the tile
     */

    private void addToCache(TileId tileIdentity, Image image) {
        if (memoryCache.size() >= CACHE_CAPACITY) {
            //complexity is 0(1)
            memoryCache.remove(memoryCache.entrySet().iterator().next().getKey());
        }
        memoryCache.put(tileIdentity, image);
    }

    /**
     * Return if the memory cache contains the image corresponding to a tile identity
     *
     * @param tileIdentity identity of the tile
     * @return if the memory cache contains the image of this tile
     */

    private boolean cacheContains(TileId tileIdentity) {
        return memoryCache.containsKey(tileIdentity);
    }

    /**
     * Returns the image associated to a tile identity
     *
     * @param tileIdentity identity of the tile
     * @return the image associated to the tile identity
     * @throws IOException if something went wrong while getting the corresponding image from the tile server
     */

    public Image imageForTileAt(TileId tileIdentity) throws IOException {
        //look in memory cache first
        if (cacheContains(tileIdentity)) {
            return memoryCache.get(tileIdentity);
        } else {
            //look in memory disk
            Path pathToFile = pathToMemoryDisk.resolve(String.valueOf(tileIdentity.zoomLevel))
                                    .resolve(String.valueOf(tileIdentity.indexX))
                                    .resolve(tileIdentity.indexY + ".png");

            if (Files.exists(pathToFile)) {
                //we load the image and place it in the memory cache
                Image imageTile = new Image(pathToFile.toUri().toString());
                addToCache(tileIdentity, imageTile);
                return imageTile;
            } else {
                //we are going to load the image from the tile server
                URL u = new URL("https", tileServerName, 443, "/" + tileIdentity.zoomLevel
                        + "/" + tileIdentity.indexX + "/" + tileIdentity.indexY + ".png");
                URLConnection c = u.openConnection();
                c.setRequestProperty("User-Agent", "JaVelo");
                //5 seconds timeout in case something went wrong with url / the server isn't reachable
                c.setConnectTimeout(5 * 1000);

                try (InputStream i = c.getInputStream()) {
                    //create the directory in cache folder /zoomLevel/xIndex/
                    Files.createDirectories(pathToFile.getParent());

                    try (OutputStream outStream = new FileOutputStream(pathToFile.toFile())) {
                        //save image to file
                        i.transferTo(outStream);

                        Image imageTile = new Image(pathToFile.toUri().toString());

                        //save image in memory cache
                        addToCache(tileIdentity, imageTile);

                        return imageTile;
                    }
                }
            }
        }
    }

    /**
     * TileId Record
     * Represents the identity of an OSM Tile
     *
     * @param zoomLevel level of zoom
     * @param indexX    index x of the tile
     * @param indexY    index y of the tile
     */

    public record TileId(int zoomLevel, int indexX, int indexY) {

        /**
         * Check if tile is valid at construction
         * @throws IllegalArgumentException if the tile parameters aren't valid
         */

        public TileId {
            Preconditions.checkArgument(isValid(zoomLevel, indexX, indexY));
        }

        /**
         * Checks if the parameters are a valid tile identity
         *
         * @param zoomLevel level of zoom of the tile to check
         * @param indexX    index x of the tile to check
         * @param indexY    index y of the tile to check
         * @return if the tile is valid or not
         */

        public static boolean isValid(int zoomLevel, int indexX, int indexY) {
            //no restrictions concerning the zoom level (can be greater than 20) in TileId, will be restricted in the gui.
            double maxIndex_X_and_Y = Math.pow(2, zoomLevel);
            return ((indexX + 1) <= maxIndex_X_and_Y && (indexY + 1) <= maxIndex_X_and_Y
                    && zoomLevel >= 0 && indexX >= 0 && indexY >= 0);
        }
    }
}
