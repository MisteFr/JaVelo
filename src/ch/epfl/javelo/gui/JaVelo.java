package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.GpxGenerator;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
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
    private static final String TILE_SERVER_HOST = "tile.openstreetmap.org";
    private static final String MAIN_WINDOW_TITLE = "JaVelo";
    private static final String MENU_TITLE = "Fichier";
    private static final String MENU_ACTION_TEXT = "Exporter GPX";
    private static final String GPX_FILE_EXPORTED_NAME = "javelo.gpx";

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

        TileManager tileManager =
                new TileManager(cacheBasePath, TILE_SERVER_HOST);

        RouteBean routeBean = new RouteBean(new RouteComputer(graph, cf));

        ErrorManager errorManager = new ErrorManager();

        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph, tileManager,
                routeBean, errorManager::displayError);

        ElevationProfileManager elevationProfileManager = new ElevationProfileManager(
                routeBean.elevationProfileProperty(), routeBean.highlightedPositionProperty());

        //when resizing vertically elevationProfileManager pane won't be resized
        setResizableWithParent(elevationProfileManager.pane(), false);

        BorderPane borderPane = new BorderPane();

        //we first initialize it without the elevationProfileManager pane() as the route won't exist
        SplitPane splitPane = new SplitPane(annotatedMapManager.pane());
        splitPane.setOrientation(Orientation.VERTICAL);
        borderPane.setCenter(splitPane);

        MenuBar menuBar = initializeMenu(routeBean);
        borderPane.setTop(menuBar);

        initializeBinding(routeBean,
                annotatedMapManager, elevationProfileManager);
        initializeListener(routeBean, elevationProfileManager, splitPane);

        StackPane stackPane = new StackPane(borderPane, errorManager.pane());

        primaryStage.setMinWidth(WINDOW_MIN_WIDTH);
        primaryStage.setMinHeight(WINDOW_MIN_HEIGHT);
        primaryStage.setTitle(MAIN_WINDOW_TITLE);
        primaryStage.setScene(new Scene(stackPane));
        primaryStage.show();
    }

    //initialize the menu
    private MenuBar initializeMenu(RouteBean routeBean) {
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
        menuBar.setUseSystemMenuBar(true);

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


