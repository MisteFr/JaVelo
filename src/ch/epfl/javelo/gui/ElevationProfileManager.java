package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

    private final ReadOnlyObjectProperty<ElevationProfile> profile;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<Rectangle2D> rectangle;

    private ObjectProperty<Transform> screenToWorld;
    private ObjectProperty<Transform> worldToScreen;

    private final Polygon polygon;
    private final Line highlightedLine;

    private final Insets rectangleInsets = new Insets(10, 10, 20, 40);


    //pane containing the waypoints
    private final BorderPane pane;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profileToDisplay,
                                   DoubleProperty highlightedPos){
        profile = profileToDisplay;
        highlightedPosition = highlightedPos;

        pane = new BorderPane();
        polygon = new Polygon();
        highlightedLine = new Line();
        rectangle = new SimpleObjectProperty<>(Rectangle2D.EMPTY);

        createPane();
        updateTransformations();
        initializeListeners();
        drawPolygon();
        initializeBindings();
    }

    /**
     * Return the Pane of the Elevation Profile
     *
     * @return Pane of the Elevation Profile
     */

    public Pane pane(){
        return pane;
    }

    /**
     * Returns a read-only property containing the position of the mouse pointer
     * along the profile (in meters, rounded to the nearest integer), or NaN if the mouse pointer is not above the profile.
     *
     * @return position of the mouse along the profile in meters
     */

    public ReadOnlyObjectProperty<Double> mousePositionOnProfileProperty(){
        return new SimpleObjectProperty<>(highlightedPosition.get());
    }

    //update the transformations
    private void updateTransformations(){
        Affine a = new Affine();

        //back to javafx origin
        a.prependTranslation(-rectangleInsets.getLeft(), -rectangle.get().getMinY());

        //scaling
        a.prependScale(profile.get().length() / rectangle.get().getWidth(),
                (profile.get().minElevation() - profile.get().maxElevation())
                       / rectangle.get().getHeight());

        //inverse y
        a.prependTranslation(0, profile.get().maxElevation());

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
    private void drawPolygon(){
        polygon.getPoints().clear();

        ArrayList<Double> tempListPoints = new ArrayList<>();

        //bottom right point
        tempListPoints.add(rectangle.get().getMaxX());
        tempListPoints.add(rectangle.get().getMaxY());


        //bottom left point
        tempListPoints.add(rectangle.get().getMinX());
        tempListPoints.add(rectangle.get().getMaxY());

        //for loop on double are way costful
        for(int x = (int) rectangle.get().getMinX(); x < rectangle.get().getMaxX(); x++){
            //y is a don't care here, goal is to get x in the real world
            Point2D correspondingProfilePos = screenToWorld.get().transform(x, 0);
            //elevation at the x point in the real world
            double elevationAtPos = profile.get().elevationAt(correspondingProfilePos.getX());
            //y corresponding to this elevation in the screen
            Point2D coordinatePointY = worldToScreen.get().transform(correspondingProfilePos.getX(), elevationAtPos);

            tempListPoints.add((double) x);
            tempListPoints.add(coordinatePointY.getY());
        }

        polygon.getPoints().setAll(tempListPoints);
    }

    //create the pane with all its elements
    private void createPane(){
        //est ce qu'on est sensé créer la ligne directement?
        //comment placer les points bottom left et right

        pane.getChildren().clear();
        System.out.println(pane.getStylesheets());

        pane.getStylesheets().add("elevation_profile.css");

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
        v.setId("profile_data");
        String textStats = String.format("Longueur : %.1f km" +
                "     Montée : %.0f m" +
                "     Descente : %.0f m" +
                "     Altitude : de %.0f m à %.0f m",
                profile.get().length() / 1000,
                profile.get().totalAscent(),
                profile.get().totalDescent(),
                profile.get().minElevation(),
                profile.get().maxElevation());
        Text t = new Text(textStats);
        v.getChildren().add(t);

        pane.setBottom(v);
    }

    private void initializeBindings(){
        //each time the pane dimensions change, update the dimensions of the rectangle
        Pane centerPane = (Pane) pane.getCenter();
        rectangle.bind(createObjectBinding(
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

        //binding of the highlightedLine
        highlightedLine.layoutXProperty().bind(createDoubleBinding(
                () -> {
                    Point2D point2D = worldToScreen.get().transform(highlightedPosition.get(), 0);
                    return point2D.getX();
                }, highlightedPosition, rectangle)
        );
        highlightedLine.startYProperty().bind(Bindings.select(rectangle, "minY"));
        highlightedLine.endYProperty().bind(Bindings.select(rectangle, "maxY"));
        highlightedLine.visibleProperty().bind(highlightedPosition.greaterThanOrEqualTo(0.0));
    }

    //initialize listners
    private void initializeListeners(){
        //each time the coordinates of the rectangle are updated, we need to recompute the transformations
        rectangle.addListener(observable -> {
            updateTransformations();
            drawPolygon();
            System.out.println(highlightedLine.getLayoutX());
        });

        //if the profile is modified -> update the statistics + re draw the polygon
        profile.addListener(observable -> {
            createPane();
            drawPolygon();
        });
    }
}
