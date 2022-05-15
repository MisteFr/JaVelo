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

    private final ObjectProperty<Point2D> mouseCoordinatesProperty = new SimpleObjectProperty<>(Point2D.ZERO);
    private final DoubleProperty mousePositionOnProfileProperty = new SimpleDoubleProperty(Double.NaN);

    private ObjectProperty<Transform> screenToWorld = new SimpleObjectProperty<>(new Affine());
    private ObjectProperty<Transform> worldToScreen = new SimpleObjectProperty<>(new Affine());

    private final Polygon polygon;
    private final Line highlightedLine;
    private final Text textStatistics;


    private Path gridNode;
    private static final int[] POS_STEPS = // todo bonne portée etc ?
            { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
    private static final int[] ELE_STEPS =
            { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };
    private static final int MIN_PIXEL_DELTA_HORIZONTAL_LINES = 25;
    private static final int MIN_PIXEL_DELTA_VERTICAL_LINES = 50;
    private List<Text> labels = new ArrayList<>();

    private final Insets rectangleInsets = new Insets(10, 10, 20, 40);

    //pane containing the elevation profile
    private final BorderPane pane;

    private static final String BORDER_PANE_STYLE_CLASS = "elevation_profile.css";

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profile,
                                   DoubleProperty highlightedPos) {
        profileProperty = profile; //todo à fix ? (quand null)
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

        if(profileProperty.get() != null){
            updateGridAndLabels();
        }
    }

    /**
     * Return the Pane of the Elevation Profile
     *
     * @return Pane of the Elevation Profile
     */

    public Pane pane() { //todo vérifier si il faudrait pas plutôt return le borderPane ?
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

        //for loop on double are way costfull
        for (int x = (int) rectangleProperty.get().getMinX(); x < rectangleProperty.get().getMaxX(); x++) { //todo and if we use getWidth ?
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

        gridNode = new Path();
        gridNode.setId("grid");
        p.getChildren().add(gridNode);


        //passer le group en attribut de classe
        Group g = new Group();
        p.getChildren().add(g);


        polygon.setId("profile");
        p.getChildren().add(polygon);

        p.getChildren().add(highlightedLine);


        VBox v = new VBox();
        v.getChildren().add(textStatistics);
        v.setId("profile_data");

        pane.setBottom(v);
    }

    private void updateGridAndLabels() {

        gridNode.getElements().clear();
        pane().getChildren().removeAll(labels);

        //Determine the values to be used for POS_STEP and ELE_STEP
        int pos_steps_used;
        int ele_steps_used;

        int index_pos_steps = 0;
        while(index_pos_steps < POS_STEPS.length &&
                screenToWorld.get().deltaTransform(MIN_PIXEL_DELTA_VERTICAL_LINES, 0).getX() > POS_STEPS[index_pos_steps]){
            ++index_pos_steps;
        }
        pos_steps_used = (index_pos_steps == POS_STEPS.length) ? POS_STEPS[POS_STEPS.length - 1] : POS_STEPS[index_pos_steps];

        int index_ele_steps = 0;
        while(index_ele_steps < ELE_STEPS.length &&
                Math.abs(screenToWorld.get().deltaTransform(0, MIN_PIXEL_DELTA_HORIZONTAL_LINES).getY()) > ELE_STEPS[index_ele_steps]){ //todo clean car Math.abs
            ++index_ele_steps;
        }
        ele_steps_used = (index_ele_steps == ELE_STEPS.length) ? ELE_STEPS[ELE_STEPS.length - 1] : ELE_STEPS[index_ele_steps];


        List<PathElement> pathElements = new ArrayList<>();
        labels = new ArrayList<>();

        //create vertical lines
        for(int i = 0; i < profileProperty.get().length(); i += pos_steps_used){
            double x = rectangleProperty.get().getMinX() + worldToScreen.get().deltaTransform(i, 0).getX();
            pathElements.add(new MoveTo(x, rectangleProperty.get().getMaxY()));
            pathElements.add(new LineTo(x, rectangleProperty.get().getMinY()));

            //update labels for distance
            Text t = new Text();
            t.getStyleClass().add("grid_label");
            t.getStyleClass().add("horizontal");
            t.setText(Integer.toString(i /1000));
            t.setFont(Font.font("Avenir", 10));
            t.setTextOrigin(VPos.TOP);
            t.setX(x - 0.5 * t.prefWidth(0));
            t.setY(rectangleProperty.get().getMaxY());

            labels.add(t);
        }

        //create horizontal lines.
        double j = ele_steps_used - profileProperty.get().minElevation() % ele_steps_used; //todo del debug purposes
        //System.out.println("profileProperty.get().minElevation(): " + profileProperty.get().minElevation() + "\n" + "ele_steps_used: " + ele_steps_used +"\n"  + "FirstHorizontalLineY: " + j);
        while(j < profileProperty.get().maxElevation() - profileProperty.get().minElevation()){
            double y = rectangleProperty.get().getMaxY() + worldToScreen.get().deltaTransform(0, j).getY();
            pathElements.add(new MoveTo(rectangleProperty.get().getMinX(), y));
            pathElements.add(new LineTo(rectangleProperty.get().getMaxX(), y));

            //update labels for elevation
            Text t = new Text();
            t.getStyleClass().add("grid_label");
            t.getStyleClass().add("vertical");
            t.setFont(Font.font("Avenir", 10));
            t.setTextOrigin(VPos.CENTER);
            t.setText(Integer.toString((int) (profileProperty.get().minElevation() + j))); //todo possible de faire sans conversion double int ? Fonctionne parfaitement en l'état.
            t.setX(rectangleProperty.get().getMinX() - t.prefWidth(0) - 2);
            t.setY(y);

            labels.add(t);

            j += ele_steps_used;
        }


        gridNode.getElements().setAll(pathElements);
        pane().getChildren().addAll(labels);
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
                () -> new Rectangle2D(
                        rectangleInsets.getLeft(),
                        rectangleInsets.getTop(),
                        Math.max(0,
                                centerPane.widthProperty().get() - rectangleInsets.getRight() - rectangleInsets.getLeft()),
                        Math.max(0,
                                centerPane.heightProperty().get() - rectangleInsets.getBottom() - rectangleInsets.getTop())
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
                if(profileProperty.get() != null){ //todo vérifier solution avec if profileProperty
                    updateGridAndLabels();
                }
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
}
