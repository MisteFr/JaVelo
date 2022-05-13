package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * Waypoint Record
 * Represents a point of passage.
 *
 * @param point  position of the point of passage in the swiss coordinate system
 * @param nodeId identity of the nearest Javelo node of this point
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public record Waypoint(PointCh point, int nodeId) {
}
