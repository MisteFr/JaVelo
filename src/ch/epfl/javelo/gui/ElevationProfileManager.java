package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.ArrayList;

import static javafx.beans.binding.Bindings.*;

public final class ElevationProfileManager {

    private final ReadOnlyObjectProperty<ElevationProfile> profileProperty;
    private final DoubleProperty highlightedPositionProperty;
    private final ObjectProperty<Rectangle2D> rectangleProperty;

    private final ObjectProperty<Point2D> mouseCoordinatesProperty = new SimpleObjectProperty<>(Point2D.ZERO);
    private final DoubleProperty mousePositionOnProfileProperty = new SimpleDoubleProperty(Double.NaN);

    private ObjectProperty<Transform> screenToWorld = new SimpleObjectProperty<>(new Affine());
    private ObjectProperty<Transform> worldToScreen = new SimpleObjectProperty<>(new Affine());

    private final Polygon polygon;
    private final Line highlightedLine;
    private final Text textStatistics;

    private final Insets rectangleInsets = new Insets(10, 10, 20, 40);

    //pane containing the elevation profile
    private final BorderPane pane;

    private static final String BORDER_PANE_STYLE_CLASS = "elevation_profile.css";

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profile,
                                   DoubleProperty highlightedPos) {
        profileProperty = profile;
        highlightedPositionProperty = highlightedPos;

        pane = new BorderPane();
        pane.getStylesheets().add(BORDER_PANE_STYLE_CLASS);
        polygon = new Polygon();
        highlightedLine = new Line();
        textStatistics = new Text();

        rectangleProperty = new SimpleObjectProperty<>(Rectangle2D.EMPTY);

        createPane();
        initializeListeners();
        initializeBindings();
        initializeHandlers();

        //only draw the polygon, update the transformation and the stats if the profile property isn't null
        if (profileProperty.isNotNull().get()) {
            updateTransformations();
            updateStats();
            drawPolygon();
        }
    }

    /**
     * Return the Pane of the Elevation Profile
     *
     * @return Pane of the Elevation Profile
     */

    public Pane pane() {
        return pane;
    }

    /**
     * Returns a read-only property containing the position of the mouse pointer
     * along the profile (in meters, rounded to the nearest integer), or NaN if the mouse pointer is not above the profile.
     *
     * @return position of the mouse along the profile in meters
     */

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePositionOnProfileProperty;
    }

    //update the transformations
    private void updateTransformations() {
        Affine a = new Affine();

        //back to javafx origin
        a.prependTranslation(-rectangleInsets.getLeft(), -rectangleProperty.get().getMinY());

        //scaling
        a.prependScale(profileProperty.get().length() / rectangleProperty.get().getWidth(),
                (profileProperty.get().minElevation() - profileProperty.get().maxElevation())
                        / rectangleProperty.get().getHeight());

        //inverse y
        a.prependTranslation(0, profileProperty.get().maxElevation());

        screenToWorld = new SimpleObjectProperty<>(a);

        //create the inverse transformation
        Transform b = null;
        try {
            b = screenToWorld.get().createInverse();
        } catch (NonInvertibleTransformException e) {
            throw new RuntimeException(e);
        }
        worldToScreen = new SimpleObjectProperty<>(b);
    }

    //draw the polygon
    private void drawPolygon() {
        polygon.getPoints().clear();

        ArrayList<Double> tempListPoints = new ArrayList<>();

        //bottom right point
        tempListPoints.add(rectangleProperty.get().getMaxX());
        tempListPoints.add(rectangleProperty.get().getMaxY());


        //bottom left point
        tempListPoints.add(rectangleProperty.get().getMinX());
        tempListPoints.add(rectangleProperty.get().getMaxY());

        //for loop on double are way costful
        for (int x = (int) rectangleProperty.get().getMinX(); x < rectangleProperty.get().getMaxX(); x++) {
            //y is a don't care here, goal is to get x in the real world
            Point2D correspondingProfilePos = screenToWorld.get().transform(x, 0);
            //elevation at the x point in the real world
            double elevationAtPos = profileProperty.get().elevationAt(correspondingProfilePos.getX());
            //y corresponding to this elevation in the screen
            Point2D coordinatePointY = worldToScreen.get().transform(correspondingProfilePos.getX(), elevationAtPos);

            tempListPoints.add((double) x);
            tempListPoints.add(coordinatePointY.getY());
        }
        polygon.getPoints().setAll(tempListPoints);
    }

    //create the pane with all its elements
    private void createPane() {
        //est ce qu'on est sensé créer la ligne directement?
        //comment placer les points bottom left et right

        pane.getChildren().clear();

        pane.getStylesheets().add(BORDER_PANE_STYLE_CLASS);

        Pane p = new Pane();
        pane.setCenter(p);

        Path path = new Path();
        path.setId("grid");
        p.getChildren().add(path);


        //passer le group en attribut de classe
        Group g = new Group();
        p.getChildren().add(g);

        Text t1 = new Text("tesst1");
        t1.getStyleClass().add("grid_label");
        t1.getStyleClass().add("horizontal");
        g.getChildren().add(t1);

        Text t2 = new Text("tesst2");
        t2.getStyleClass().add("grid_label");
        t2.getStyleClass().add("vertical");
        g.getChildren().add(t2);
        //pour les étiquettes maxElevation - minElevation / step (nombre de labels)

        polygon.setId("profile");
        p.getChildren().add(polygon);

        p.getChildren().add(highlightedLine);


        VBox v = new VBox();
        v.getChildren().add(textStatistics);
        v.setId("profile_data");

        pane.setBottom(v);
    }

    //update the statistics text
    private void updateStats() {
        String textStats = String.format("Longueur : %.1f km" +
                        "     Montée : %.0f m" +
                        "     Descente : %.0f m" +
                        "     Altitude : de %.0f m à %.0f m",
                profileProperty.get().length() / 1000,
                profileProperty.get().totalAscent(),
                profileProperty.get().totalDescent(),
                profileProperty.get().minElevation(),
                profileProperty.get().maxElevation());
        textStatistics.setText(textStats);
    }

    //initialize the bindings on the rectangleProperty, the highlightedLine and the mousePositionOnProfileProperty
    private void initializeBindings() {
        //each time the pane dimensions change, update the dimensions of the rectangle
        Pane centerPane = (Pane) pane.getCenter();
        rectangleProperty.bind(createObjectBinding(
                () -> {
                    Rectangle2D r = new Rectangle2D(
                            rectangleInsets.getLeft(),
                            rectangleInsets.getTop(),
                            Math2.clamp(0,
                                    centerPane.widthProperty().get() - rectangleInsets.getRight() - rectangleInsets.getLeft(),
                                    centerPane.widthProperty().get()),
                            Math2.clamp(0,
                                    centerPane.heightProperty().get() - rectangleInsets.getBottom() - rectangleInsets.getTop(),
                                    centerPane.heightProperty().get())
                    );
                    return r;
                },
                centerPane.widthProperty(), centerPane.heightProperty())
        );

        //binding of the highlightedLines
        highlightedLine.layoutXProperty().bind(createDoubleBinding(
                () -> {
                    Point2D point2D = worldToScreen.get().transform(highlightedPositionProperty.get(), 0);
                    return point2D.getX();
                }, highlightedPositionProperty, rectangleProperty, worldToScreen)
        );
        highlightedLine.startYProperty().bind(Bindings.select(rectangleProperty, "minY"));
        highlightedLine.endYProperty().bind(Bindings.select(rectangleProperty, "maxY"));
        highlightedLine.visibleProperty().bind(highlightedPositionProperty.greaterThanOrEqualTo(0.0));

        //binding to update the position of the highlighted line according to the mouse position
        mousePositionOnProfileProperty.bind(createDoubleBinding(
                () -> {
                    if(mouseCoordinatesProperty.isNotNull().get()){
                        return screenToWorld.get().transform(mouseCoordinatesProperty.get().getX(), 0).getX();
                    }else{
                        return Double.NaN;
                    }
                }, mouseCoordinatesProperty, worldToScreen, screenToWorld
        ));
    }

    //initialize listeners on the rectangleProperty and the profileProperty
    private void initializeListeners() {
        //each time the coordinates of the rectangle are updated, we need to recompute the transformations
        rectangleProperty.addListener(observable -> {
            if (profileProperty.isNotNull().get()) {
                updateTransformations();
                drawPolygon();
            }
        });

        profileProperty.addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if(!newValue.equals(oldValue)){
                    updateTransformations();
                }

                //update the statistics and draw the polygon
                updateStats();
                drawPolygon();
            }
        }));
    }

    //initialize handlers on the pane (mouse management)
    private void initializeHandlers(){
        pane.getCenter().setOnMouseMoved(mouseEvent -> {
            if(rectangleProperty.get().contains(new Point2D(mouseEvent.getX(), mouseEvent.getY()))){
                mouseCoordinatesProperty.setValue(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
            }else{
                mouseCoordinatesProperty.setValue(null);
            }
        });

        //mouse went out of the map
        pane.getCenter().setOnMouseExited(mouseEvent -> {
            mouseCoordinatesProperty.setValue(null);
        });
    }
}
