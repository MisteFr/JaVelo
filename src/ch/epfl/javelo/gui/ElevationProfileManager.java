package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;

import static javafx.beans.binding.Bindings.*;

public final class ElevationProfileManager {

    private final ReadOnlyObjectProperty<ElevationProfile> profileProperty;
    private final DoubleProperty highlightedPositionProperty;
    private final ObjectProperty<Rectangle2D> rectangleProperty;

    private final ObjectProperty<Point2D> mouseCoordinatesProperty;
    private final DoubleProperty mousePositionOnProfileProperty;

    private ObjectProperty<Transform> screenToWorld;
    private ObjectProperty<Transform> worldToScreen;

    private final Polygon polygon;
    private final Line highlightedLine;
    private final Text textStatistics;
    private final Path gridNode;

    private static final int ONE_KILOMETER_IN_METERS = 1000;

    //pane containing the elevation profile
    private final BorderPane pane;

    private static final String BORDER_PANE_STYLE_CLASS = "elevation_profile.css";
    private static final int[] POS_STEPS =
            { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
    public static final int FONT_SIZE = 10;
    private static final int[] ELE_STEPS =
            { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };
    private static final int MIN_PIXEL_DELTA_HORIZONTAL_LINES = 25;
    private static final int MIN_PIXEL_DELTA_VERTICAL_LINES = 50;
    private final Group labels = new Group();
    private static final int ZERO_CONSTANT_FOR_UNIDIMENSIONAL_VECTORS = 0;

    private static final String GRID_NODE_ID = "grid";
    private static final String POLYGON_NODE_ID = "profile";
    private static final String PROFILE_NODE_ID = "profile_data";
    private static final String GRID_LABEL_STYLE_CLASS = "grid_label";
    private static final String HORIZONTAL_LABEL_STYLE_CLASS = "horizontal";
    private static final String LABEL_FONT = "Avenir";
    private static final String VERTICAL_LABEL_STYLE_CLASS = "vertical";
    private static final String STATS_TEXT = "Longueur : %.1f km" +
            "     Montée : %.0f m" +
            "     Descente : %.0f m" +
            "     Altitude : de %.0f m à %.0f m";

    private static final Insets RECTANGLE_INSETS = new Insets(10, 10, 20, 40);

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profile,
                                   DoubleProperty highlightedPos) {
        profileProperty = profile;
        highlightedPositionProperty = highlightedPos;

        pane = new BorderPane();
        pane.getStylesheets().add(BORDER_PANE_STYLE_CLASS);
        polygon = new Polygon();
        highlightedLine = new Line();
        textStatistics = new Text();
        gridNode = new Path();


        rectangleProperty = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
        mouseCoordinatesProperty = new SimpleObjectProperty<>(Point2D.ZERO);
        mousePositionOnProfileProperty = new SimpleDoubleProperty(Double.NaN);
        screenToWorld = new SimpleObjectProperty<>(new Affine());
        worldToScreen = new SimpleObjectProperty<>(new Affine());

        createPane();
        initializeListeners();
        initializeBindings();
        initializeHandlers();

        //only draw the polygon, update the transformation and the stats if the profile property isn't null
        if (profileProperty.isNotNull().get()) {
            updateTransformations();
            updateStats();
            drawPolygon();
            updateGridAndLabels();
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
        a.prependTranslation(-RECTANGLE_INSETS.getLeft(), -rectangleProperty.get().getMinY());

        //scaling
        a.prependScale(profileProperty.get().length() / rectangleProperty.get().getWidth(),
                (profileProperty.get().minElevation() - profileProperty.get().maxElevation())
                        / rectangleProperty.get().getHeight());

        //inverse y
        a.prependTranslation(0, profileProperty.get().maxElevation());

        screenToWorld = new SimpleObjectProperty<>(a);

        //create the inverse transformation
        Transform b;
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


        //for loop on double are worst for performances
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
        pane.getChildren().clear();
        pane.getStylesheets().add(BORDER_PANE_STYLE_CLASS);

        Pane p = new Pane();
        pane.setCenter(p);

        gridNode.setId(GRID_NODE_ID);
        p.getChildren().add(gridNode);


        p.getChildren().add(labels);

        polygon.setId(POLYGON_NODE_ID);
        p.getChildren().add(polygon);

        p.getChildren().add(highlightedLine);


        VBox v = new VBox();
        v.getChildren().add(textStatistics);
        v.setId(PROFILE_NODE_ID);

        pane.setBottom(v);
    }

    private void updateGridAndLabels() {

        gridNode.getElements().clear();
        labels.getChildren().clear();

        //Determine the values to be used for POS_STEP and ELE_STEP
        int[] steps_used = computePosAndEleStepsUsed();

        List<PathElement> pathElements = new ArrayList<>();
        List<Text> labelsTemp = new ArrayList<>();

        //create new vertical lines
        for(int i = 0; i < profileProperty.get().length(); i += steps_used[0]){
            double x = rectangleProperty.get().getMinX() + worldToScreen.get().deltaTransform(i, 0).getX();
            pathElements.add(new MoveTo(x, rectangleProperty.get().getMaxY()));
            pathElements.add(new LineTo(x, rectangleProperty.get().getMinY()));

            //create new labels for distance
            labelsTemp.add(createHorizontalText(x, i));
        }

        //create new horizontal lines.
        double j = steps_used[1] - profileProperty.get().minElevation() % steps_used[1];
        while(j < profileProperty.get().maxElevation() - profileProperty.get().minElevation()){
            double y = rectangleProperty.get().getMaxY() + worldToScreen.get().deltaTransform(0, j).getY();
            pathElements.add(new MoveTo(rectangleProperty.get().getMinX(), y));
            pathElements.add(new LineTo(rectangleProperty.get().getMaxX(), y));

            //create new labels for elevation
            labelsTemp.add(createVerticalText(y, j));

            j += steps_used[1];
        }

        //update lines and labels
        gridNode.getElements().setAll(pathElements);
        labels.getChildren().addAll(labelsTemp);
    }

    //update the statistics text
    private void updateStats() {
        String textStats = String.format(STATS_TEXT,
                profileProperty.get().length() / ONE_KILOMETER_IN_METERS,
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
                () -> new Rectangle2D(
                        RECTANGLE_INSETS.getLeft(),
                        RECTANGLE_INSETS.getTop(),
                        Math.max(0,
                                centerPane.widthProperty().get() - RECTANGLE_INSETS.getRight() - RECTANGLE_INSETS.getLeft()),
                        Math.max(0,
                                centerPane.heightProperty().get() - RECTANGLE_INSETS.getBottom() - RECTANGLE_INSETS.getTop())
                ),
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
                }, mouseCoordinatesProperty, screenToWorld
        ));
    }

    //initialize listeners on the rectangleProperty and the profileProperty
    private void initializeListeners() {
        //each time the coordinates of the rectangle are updated, we need to recompute the transformations
        rectangleProperty.addListener(observable -> {
            if (profileProperty.isNotNull().get()) {
                updateTransformations();
                drawPolygon();
                if(profileProperty.get() != null)
                    updateGridAndLabels();
            }
        });

        profileProperty.addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if(!newValue.equals(oldValue)){
                    updateTransformations();
                    updateGridAndLabels();
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
        pane.getCenter().setOnMouseExited(mouseEvent -> mouseCoordinatesProperty.setValue(null));
    }

    //return the elements of pos_step (with index 0) and ele_steps (with index 1) that are used.
    private int[] computePosAndEleStepsUsed(){

        int[] posAndEleStepsUsed = new int[2];
        int index_pos_steps = 0;
        double verticalMinPixelsInMeters =
                screenToWorld
                        .get()
                        .deltaTransform(MIN_PIXEL_DELTA_VERTICAL_LINES, ZERO_CONSTANT_FOR_UNIDIMENSIONAL_VECTORS)
                        .getX();

        while(index_pos_steps < POS_STEPS.length && verticalMinPixelsInMeters > POS_STEPS[index_pos_steps]){
            ++index_pos_steps;
        }
        posAndEleStepsUsed[0] = (index_pos_steps == POS_STEPS.length)
                ? POS_STEPS[POS_STEPS.length - 1]
                : POS_STEPS[index_pos_steps];

        int index_ele_steps = 0;
        double horizontalMinPixelsInMeters =
                Math.abs(screenToWorld
                        .get()
                        .deltaTransform(ZERO_CONSTANT_FOR_UNIDIMENSIONAL_VECTORS, MIN_PIXEL_DELTA_HORIZONTAL_LINES)
                        .getY());

        while(index_ele_steps < ELE_STEPS.length && horizontalMinPixelsInMeters > ELE_STEPS[index_ele_steps]){
            ++index_ele_steps;
        }
        posAndEleStepsUsed[1] = (index_ele_steps == ELE_STEPS.length)
                ? ELE_STEPS[ELE_STEPS.length - 1]
                : ELE_STEPS[index_ele_steps];

        return posAndEleStepsUsed;
    }

    //create horizontal text
    private Text createHorizontalText(double x, int i){
        Text t = new Text();
        t.getStyleClass().add(GRID_LABEL_STYLE_CLASS);
        t.getStyleClass().add(HORIZONTAL_LABEL_STYLE_CLASS);
        t.setText(Integer.toString(i /ONE_KILOMETER_IN_METERS));
        t.setFont(Font.font(LABEL_FONT, FONT_SIZE));
        t.setTextOrigin(VPos.TOP);
        t.setX(x - 0.5 * t.prefWidth(0));
        t.setY(rectangleProperty.get().getMaxY());

        return t;
    }

    //create vertical text
    private Text createVerticalText(double y, double j){
        Text t = new Text();
        t.getStyleClass().add(GRID_LABEL_STYLE_CLASS);
        t.getStyleClass().add(VERTICAL_LABEL_STYLE_CLASS);
        t.setFont(Font.font(LABEL_FONT, FONT_SIZE));
        t.setTextOrigin(VPos.CENTER);
        t.setText(Integer.toString((int) (profileProperty.get().minElevation() + j)));
        t.setX(rectangleProperty.get().getMinX() - t.prefWidth(0) - 2);
        t.setY(y);

        return t;
    }
}
