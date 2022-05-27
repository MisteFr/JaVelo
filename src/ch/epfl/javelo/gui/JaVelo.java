package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.GpxGenerator;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import static javafx.beans.binding.Bindings.createDoubleBinding;
import static javafx.scene.control.SplitPane.setResizableWithParent;

/**
 * JaVelo class
 * The main class of the application.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class JaVelo extends Application {

    private static final String GRAPH_DATA_DIRECTORY = "javelo-data";
    private static final String OSM_CACHE_PATH = "osm-cache";
    private static final String TILE_SERVER_HOST_OSM = "tile.openstreetmap.org";
    private static final String TILE_SERVER_HOST_CYCLOSM = "a.tile-cyclosm.openstreetmap.fr";
    private static final String DIR_CYCLOSM = "cyclosm";
    private static final String TILE_SERVER_HOST_BLACK_AND_WHITE = "stamen-tiles.a.ssl.fastly.net";
    private static final String DIR_BLACK_AND_WHITE = "toner";
    private static final String TILE_SERVER_HOST_DARK_MODE = "cartodb-basemaps-a.global.ssl.fastly.net";
    private static final String DIR_DARK_MODE = "dark_all";

    private static final String MAIN_WINDOW_TITLE = "JaVelo";
    private static final String MENU_TITLE = "Fichier";
    private static final String MENU_ACTION_TEXT = "Exporter GPX";
    private static final String GPX_FILE_EXPORTED_NAME = "javelo.gpx";

    private final ObjectProperty<TileManager> tileManagerObjectProperty = new SimpleObjectProperty<>();

    private static final int ELEVATION_PROFILE_MANAGER_PANE_INDEX = 1;

    private static final int WINDOW_MIN_WIDTH = 800;
    private static final int WINDOW_MIN_HEIGHT = 600;

    /**
     * Main function to launch Javelo
     * @param args args to launch
     */
    public static void main(String[] args) { launch(args); }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of(GRAPH_DATA_DIRECTORY));
        Path cacheBasePath = Path.of(OSM_CACHE_PATH);
        CostFunction cf = new CityBikeCF(graph);

        tileManagerObjectProperty.set(new TileManager(cacheBasePath, TILE_SERVER_HOST_OSM));

        RouteBean routeBean = new RouteBean(new RouteComputer(graph, cf));

        ErrorManager errorManager = new ErrorManager();


        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph, tileManagerObjectProperty.get(),
                routeBean, errorManager::displayError);


        ElevationProfileManager elevationProfileManager = new ElevationProfileManager(
                routeBean.elevationProfileProperty(), routeBean.highlightedPositionProperty());

        //when resizing vertically elevationProfileManager pane won't be resized
        setResizableWithParent(elevationProfileManager.pane(), false);


        //we first initialize it without the elevationProfileManager pane() as the route won't exist
        SplitPane splitPane = new SplitPane(annotatedMapManager.pane());

        splitPane.setOrientation(Orientation.VERTICAL);

        MenuBar menuBar = initializeMenu(routeBean, annotatedMapManager);

        initializeBinding(routeBean,
                annotatedMapManager, elevationProfileManager);
        initializeListener(routeBean, elevationProfileManager, splitPane);

        StackPane stackPane = new StackPane(splitPane, errorManager.pane());

        BorderPane borderPane = new BorderPane(stackPane, menuBar, null, null, null);

        primaryStage.setMinWidth(WINDOW_MIN_WIDTH);
        primaryStage.setMinHeight(WINDOW_MIN_HEIGHT);
        primaryStage.setTitle(MAIN_WINDOW_TITLE);
        primaryStage.setScene(new Scene(borderPane));
        primaryStage.show();
    }

    //initialize the menu
    private MenuBar initializeMenu(RouteBean routeBean, AnnotatedMapManager annotatedMapManager) {
        MenuBar menuBar = new MenuBar();

        Menu menu = new Menu(MENU_TITLE);
        menuBar.getMenus().add(menu);

        MenuItem menuItem = new MenuItem(MENU_ACTION_TEXT);
        menu.getItems().add(menuItem);

        menuItem.setOnAction(action -> {
            //export the GPX file
            try {
                GpxGenerator.writeGpx(GPX_FILE_EXPORTED_NAME, routeBean.route(), routeBean.elevationProfile());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });

        menuItem.disableProperty().bind(routeBean.routeProperty().isNull());

        //EXTENSIONS
        Menu menuMap = new Menu("Fonds de carte");
        menuBar.getMenus().add(menuMap);

        MenuItem originalMap = new MenuItem("Carte originale OpenStreetMap");
        menuMap.getItems().add(originalMap);

        originalMap.setOnAction(action -> {
                tileManagerObjectProperty.get().changeServerConfigurations(TILE_SERVER_HOST_OSM);
                annotatedMapManager.draw();
            }
        );

        MenuItem blackAndWhiteMap = new MenuItem("Carte en noir et blanc");
        menuMap.getItems().add(blackAndWhiteMap);

        blackAndWhiteMap.setOnAction(action -> {
                tileManagerObjectProperty.get()
                    .changeServerConfigurations(TILE_SERVER_HOST_BLACK_AND_WHITE, DIR_BLACK_AND_WHITE);
                annotatedMapManager.draw();
            }
        );

        MenuItem cylOsmMap = new MenuItem("Carte CyclOSM");
        menuMap.getItems().add(cylOsmMap);

        cylOsmMap.setOnAction(action -> {
                tileManagerObjectProperty.get()
                        .changeServerConfigurations(TILE_SERVER_HOST_CYCLOSM, DIR_CYCLOSM);
                annotatedMapManager.draw();
            }
        );

        MenuItem darkMap = new MenuItem("Carte en mode sombre");
        menuMap.getItems().add(darkMap);

        darkMap.setOnAction(action -> {
                tileManagerObjectProperty.get()
                        .changeServerConfigurations(TILE_SERVER_HOST_DARK_MODE, DIR_DARK_MODE);
                annotatedMapManager.draw();
            }
        );

        Menu menuOptions = new Menu("Options");
        menuBar.getMenus().add(menuOptions);

        //delete waypoint
        MenuItem removeWaypointsItem = new MenuItem("Supprimer les waypoints");
        menuOptions.getItems().add(removeWaypointsItem);

        removeWaypointsItem.setOnAction(action -> routeBean.waypoints().clear());

        //inverse route
        MenuItem inverseRouteItem = new MenuItem("Inverser l'itinéraire");
        menuOptions.getItems().add(inverseRouteItem);

        inverseRouteItem.setOnAction(action -> {
            FXCollections.reverse(routeBean.waypoints());
        });

        return menuBar;
    }

    //initialize binding on the highlightedPositionProperty of routeBean
    private void initializeBinding(RouteBean routeBean,
                                   AnnotatedMapManager annotatedMapManager,
                                   ElevationProfileManager elevationProfileManager) {
        routeBean.highlightedPositionProperty().bind(createDoubleBinding(
                () -> {
                    //bind to the position of the mouse on the route if it is greater than zero
                    //else to the position on the profile
                    if (annotatedMapManager.mousePositionOnRouteProperty().get() > 0.0) {
                        return annotatedMapManager.mousePositionOnRouteProperty().get();
                    } else {
                        return elevationProfileManager.mousePositionOnProfileProperty().get();
                    }
                }, annotatedMapManager.mousePositionOnRouteProperty(),
                elevationProfileManager.mousePositionOnProfileProperty()
        ));
    }

    //initialize the listener on the routeProperty of routeBean
    private void initializeListener(RouteBean routeBean,
                                    ElevationProfileManager elevationProfileManager,
                                    SplitPane splitPane) {
        routeBean.routeProperty().addListener(((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue == null) {
                splitPane.getItems().remove(ELEVATION_PROFILE_MANAGER_PANE_INDEX);
            }

            if (oldValue == null && newValue != null) {
                splitPane.getItems().add(ELEVATION_PROFILE_MANAGER_PANE_INDEX, elevationProfileManager.pane());
            }
        }));
    }
}


