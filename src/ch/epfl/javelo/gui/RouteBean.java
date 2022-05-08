package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.*;

/**
 * RouteBean class
 * A JavaFX bean used to represent a route and its different properties that will be used by the javaFX view.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public final class RouteBean {

    //todo voir ce qui peut être final (tout étant donné que ce sont des cellules ?)
    private final ObservableList<Waypoint> waypoints = FXCollections.observableArrayList(); //todo initialiser ?
    private final ObjectProperty<Route> route = new SimpleObjectProperty<>();
    private final DoubleProperty highlightedPosition = new SimpleDoubleProperty(Double.NaN);
    //the highlighted position must have a NaN value while no position needs to be showed.
    private final ObjectProperty<ElevationProfile> elevationProfile = new SimpleObjectProperty<>();
    private final static int CACHE_CAPACITY = 100;
    private final LinkedHashMap<Pair<Waypoint, Waypoint>, Route> routeComputingBuffer = new LinkedHashMap<>(CACHE_CAPACITY, 0.75f, true);


    /**
     * Constructor for new RouteBean.
     * @param routeComputer the routeComputer that will be used to dynamically adapt the route to the list of waypoints
     */

    public RouteBean(RouteComputer routeComputer){
        addListeners(routeComputer);
    }

    /**
     * Accessor for the DoubleProperty wrapper of the highlightedPosition's attribute,
     * the position on the route in meters that is highlighted.
     * @return DoubleProperty property
     */

    public DoubleProperty highlightedPositionProperty(){
        return highlightedPosition; // todo pas de copie car on veut que les modifications se répercutent sur l'objet ?
    }

    /**
     * Accessor for DoubleProperty highlightedPosition's attribute, the position on the route in meters that is highlighted.
     * @return double attribute, the position in meter or Double.NaN if no correct value was stored.
     */
    //todo vérifier inconsistance dans la description du prof des standards beans

    public double getHighlightedPosition(){
        return highlightedPosition.get();
    }

    /**
     * Setter for DoubleProperty highlightedPosition's attribute, the position on the route in meters that is highlighted.
     * @param newValue the value that will be given to the DoubleProperty highlightedPosition's attribute
     *                 must be between 0 and the route's length.
     */

    public void setHighlightedPosition(double newValue){
        Preconditions.checkArgument((newValue >= 0)); //todo autres manières de faire ? + regarder si besoin de conditions
        //sur length de la route
        highlightedPosition.set(newValue);
    }


    /**
     * Accessor for the ObservableList of waypoints property, corresponding to the
     * list of all the waypoints that the route needs to pass by.
     * @return waypoints property
     */
    //todo vérifier les standards des beans par rapport aux objets de type ObservableList

    public ObservableList<Waypoint> waypointsProperty(){
        return waypoints;
    }

    /**
     * Accessor for the ReadOnlyObjectProperty route property, corresponding to
     * the route that passes by all the given waypoints.
     */
    // todo peut-être à supprimer, ou faire en sorte que ce soit immuable (dépendamment du fonctionnement de ReadOnlyObjectProperty)

    public ReadOnlyObjectProperty<Route> route(){
        return route; //todo transtypage suffit ?
    }

    /**
     * Accessor for the ReadOnlyObjectProperty elevationProfile property, corresponding
     * to the elevationProfile of the route.
     */
    // todo peut-être à supprimer, ou faire en sorte que ce soit immuable (dépendamment du fonctionnement de ReadOnlyObjectProperty)

    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty(){
        return elevationProfile;
    }

    //todo ajouter accesseurs directs à la route et à l'elevationProfile ? Standards beans // on y a déjà accès via les propriétés.

    //creates the listeners to update the route and elevationProfile according to the modifications of the waypoints list.
    private void addListeners(RouteComputer routeComputer) {
        waypoints.addListener((ListChangeListener<Waypoint>) change -> {
            //if there is not enough waypoints in the ObservableList, the computed route as well as its elevationProfile are null.
            if(waypoints.size() < 2){
                route.set(null);
                elevationProfile.set(null);
            }else{
                List<Route> segments = new ArrayList<>();
                boolean containsNull = false;
                Route bestRoute;
                //todo débugging, clean
                //System.out.println(routeComputingBuffer.size());
                for(int i = 0; i < waypoints.size() - 1; ++i){
                    ObjectProperty<Boolean> routeHasBeenComputed = new SimpleObjectProperty<>(false); // todo vérifier si cette solution est acceptable
                    Pair<Waypoint, Waypoint> routeSegmentWaypoints = new Pair<>(waypoints.get(i), waypoints.get(i+1));

                    //we check if the best route between the two waypoints is stored in the buffer.
                    if(routeComputingBuffer.containsKey(routeSegmentWaypoints)){
                        System.out.println("Cache already contains route !");
                        routeHasBeenComputed.set(true);
                    }

                    if(!routeHasBeenComputed.get()){
                        System.out.println("On compute une route");
                        bestRoute = routeComputer.bestRouteBetween(waypoints.get(i).nodeId(), waypoints.get(i+1).nodeId());
                        //if no route is found between the two waypoints, the computed route as well as its elevationProfile are null.
                        if(bestRoute == null){
                            containsNull = true;
                            break;
                        }
                        //only executed if bestRoute != null
                        addToCache(routeSegmentWaypoints, bestRoute);
                    }else{
                        bestRoute = routeComputingBuffer.get(routeSegmentWaypoints);
                    }
                    segments.add(bestRoute);
                }

                if(!containsNull){
                    route.set(new MultiRoute(segments));
                    elevationProfile.set(ElevationProfileComputer.elevationProfile(route.get(), 5));
                }else{
                    route.set(null);
                    elevationProfile.set(null);
                }
            }
        });
    }

    //for inner working, see tileManager.
    private void addToCache(Pair<Waypoint, Waypoint> pair, Route route) {
        if (routeComputingBuffer.size() >= CACHE_CAPACITY) {
            //complexity is 0(1)
            routeComputingBuffer.remove(routeComputingBuffer.entrySet().iterator().next().getKey());
        }
        routeComputingBuffer.put(pair, route);
    }
}

