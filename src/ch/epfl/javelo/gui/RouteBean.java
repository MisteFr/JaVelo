package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;

/**
 * RouteBean class
 * A JavaFX bean used to represent a route and its different properties that will be used by the javaFX view.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class RouteBean {

    public final ObservableList<Waypoint> waypointsList;
    public final ObjectProperty<Route> routeProperty;
    public final ObjectProperty<ElevationProfile> elevationProfileProperty;
    public final DoubleProperty highlightedPositionProperty;

    //Memory cache array with access-order
    private final LinkedHashMap<PairOfWaypoints, Route> routeComputingBuffer;

    //Memory cache capacity
    private final static int CACHE_CAPACITY = 100;

    //max step length for the elevation profile
    private static final int MAX_STEP_LENGTH = 5;

    private static final int MIN_NUMBER_OF_WAYPOINTS = 2;

    private static final float LOAD_FACTOR = 0.75f;

    //used in cache
    private record PairOfWaypoints(Waypoint w1, Waypoint w2) {
    }

    /**
     * Constructor for new RouteBean.
     *
     * @param routeComputer the routeComputer that will be used to dynamically adapt the route to the list of waypoints
     */

    public RouteBean(RouteComputer routeComputer) {
        waypointsList = FXCollections.observableArrayList();
        routeProperty = new SimpleObjectProperty<>();
        highlightedPositionProperty = new SimpleDoubleProperty(Double.NaN);
        elevationProfileProperty = new SimpleObjectProperty<>();
        routeComputingBuffer = new LinkedHashMap<>(CACHE_CAPACITY, LOAD_FACTOR, true);

        addListeners(routeComputer);
    }

    /**
     * Accessor for the DoubleProperty wrapper of the highlightedPosition's attribute,
     * the position on the route in meters that is highlighted.
     *
     * @return DoubleProperty property
     */

    public DoubleProperty highlightedPositionProperty() {
        return highlightedPositionProperty;
    }

    /**
     * Accessor for DoubleProperty highlightedPosition's attribute, the position on the route in meters that is highlighted.
     *
     * @return double attribute, the position in meter or Double.NaN if no correct value was stored.
     */
    public double highlightedPosition() {
        return highlightedPositionProperty.get();
    }

    /**
     * Setter for DoubleProperty highlightedPosition's attribute, the position on the route in meters that is highlighted.
     *
     * @param newValue the value that will be given to the DoubleProperty highlightedPosition's attribute
     *                 must be between 0 and the route's length.
     */

    public void setHighlightedPosition(double newValue) {
        Preconditions.checkArgument((newValue >= 0));
        highlightedPositionProperty.set(newValue);
    }


    /**
     * Accessor for the ObservableList of waypoints property, corresponding to the
     * list of all the waypoints that the route needs to pass by.
     *
     * @return waypoints property
     */

    public ObservableList<Waypoint> waypoints() {
        return waypointsList;
    }

    /**
     * Accessor for the ReadOnlyObjectProperty route property, corresponding to
     * the route that passes by all the given waypoints.
     *
     * @return the route property
     */

    public ReadOnlyObjectProperty<Route> routeProperty() {
        return routeProperty;
    }

    /**
     * Accessor for ObjectProperty route's attribute, return the route instance
     *
     * @return the route instance
     */

    public Route route() {
        return routeProperty.get();
    }

    /**
     * Accessor for ObjectProperty elevation profile's attribute, return the elevation profile instance
     *
     * @return the elevation profile instance
     */

    public ElevationProfile elevationProfile() {
        return elevationProfileProperty.get();
    }

    /**
     * Accessor for the ReadOnlyObjectProperty elevationProfile property, corresponding
     * to the elevationProfile of the route.
     *
     * @return the elevationProfile property
     */

    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfileProperty;
    }

    /**
     * Return the index of the segment containing a position on the route
     *
     * @param position position along the route
     * @return the index of the segment containing the position
     */

    public int indexOfNonEmptySegmentAt(double position) {
        int index = route().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypointsList.get(i).nodeId();
            int n2 = waypointsList.get(i + 1).nodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }

    //creates the listeners to update the route and elevationProfile according to
    // the modifications of the waypoints list.
    private void addListeners(RouteComputer routeComputer) {
        waypointsList.addListener((ListChangeListener<Waypoint>) change ->
                updateRouteAndElevationProfile(routeComputer));
    }

    //for inner working, see tileManager.
    private void addToCache(PairOfWaypoints pair, Route route) {
        if (routeComputingBuffer.size() >= CACHE_CAPACITY) {
            //complexity is 0(1)
            routeComputingBuffer.remove(routeComputingBuffer.entrySet().iterator().next().getKey());
        }
        routeComputingBuffer.put(pair, route);
    }

    private void updateRouteAndElevationProfile(RouteComputer routeComputer) {
        //if there is not enough waypoints in the ObservableList, the computed route as well
        // as its elevationProfile are null.
        if (waypointsList.size() < MIN_NUMBER_OF_WAYPOINTS) {
            routeProperty.set(null);
            elevationProfileProperty.set(null);
        }
        else {
            List<Route> segments = new ArrayList<>();
            boolean containsNull = false;
            Route bestRoute;

            for (int i = 0; i < waypointsList.size() - 1; ++i) {

                if (waypointsList.get(i).nodeId() == waypointsList.get(i + 1).nodeId()) {
                    //move to next waypoints
                    continue;
                }

                PairOfWaypoints routeSegmentWaypoints = new PairOfWaypoints(waypointsList.get(i),
                        waypointsList.get(i + 1));

                //we check if the best route between the two waypoints is stored in the buffer.
                if (!routeComputingBuffer.containsKey(routeSegmentWaypoints)) {
                    bestRoute = routeComputer.bestRouteBetween(waypointsList.get(i).nodeId(),
                            waypointsList.get(i + 1).nodeId());
                    //if no route is found between the two waypoints,
                    // the computed route as well as its elevationProfile are null.
                    if (bestRoute == null) {
                        containsNull = true;
                        break;
                    }
                    //only executed if bestRoute != null
                    addToCache(routeSegmentWaypoints, bestRoute);
                } else {
                    bestRoute = routeComputingBuffer.get(routeSegmentWaypoints);
                }
                segments.add(bestRoute);
            }

            if (!containsNull && !segments.isEmpty()) {
                if(segments.size() == 1){
                    routeProperty.set(new SingleRoute(segments.get(0).edges()));
                }else{
                    routeProperty.set(new MultiRoute(segments));
                }
                elevationProfileProperty.set(ElevationProfileComputer.elevationProfile(routeProperty.get(),
                        MAX_STEP_LENGTH));
            } else {
                routeProperty.set(null);
                elevationProfileProperty.set(null);
            }
        }
    }
}