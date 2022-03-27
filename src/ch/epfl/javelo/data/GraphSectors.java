package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * GraphSectors record
 * Represents the array containing the 16384 sectors of JaVelo.
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public record GraphSectors(ByteBuffer buffer) {

    private static final int OFFSET_ID = 0;
    private static final int OFFSET_NB = OFFSET_ID + Integer.BYTES;
    private static final int SECTOR_BYTES = Integer.BYTES + Short.BYTES;
    private static final int SECTORS_ON_AXIS = 128;


    /**
     * Represents a Sector in the 128*128 grid of Sectors.
     * endNodeId is the id of the last node of the sector + 1.
     */
    public record Sector(int startNodeId, int endNodeId) {}


    /**
     * Models a two-dimensional grid of sectors and iterates through the sectors in the scope of the square to add them
     * to the returned List<Sector>. See comments in the method for more insights.
     *
     * @param center PointCh parameter that corresponds to the center of the drawn square
     * @param distance The distance of the middle of each side of the square from the center PointCh parameter.
     * @return A list of the Sectors that are, even partly, in the scope of the square.
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {

        ArrayList<Sector> result = new ArrayList<>();

        int bottomEIndex = Math2.clamp(0,
                (int) (((center.e() - distance) - SwissBounds.MIN_E) / (SwissBounds.WIDTH / SECTORS_ON_AXIS)), 127);

        int bottomNIndex = Math2.clamp(0,
                (int) (((center.n() - distance) - SwissBounds.MIN_N) / (SwissBounds.HEIGHT / SECTORS_ON_AXIS)), 127);
        // We have the (bottomEIndex, bottomNIndex) sector, that corresponds to the sector included in the bottom left of the square.

        int topEIndex = Math2.clamp(0,
                (int) (((center.e() + distance) - SwissBounds.MIN_E) / (SwissBounds.WIDTH / SECTORS_ON_AXIS)), 127);

        int topNIndex = Math2.clamp(0,
                (int) (((center.n() + distance) - SwissBounds.MIN_N) / (SwissBounds.HEIGHT / SECTORS_ON_AXIS)), 127);
        // We have the (topEIndex, topNIndex) sector, that corresponds to the sector included in the top right of the square.


        int index;
        int indexBytes;
        int identityOfFirstNode;
        int identityOfLastNode;

        //We loop through the appropriate sectors (the borders of our scope were defined earlier in the method) to add them to the result ArrayList.
        for (int i = bottomNIndex; i <= topNIndex; i++) {
            for (int j = bottomEIndex; j <= topEIndex; j++) {

                index = 128 * i + j;
                indexBytes = index * SECTOR_BYTES; // We can translate the two-dimensional array of the coordinates of a sector in our grid to an index in the one-dimensional buffer attribute.
                identityOfFirstNode = buffer.getInt(indexBytes);
                identityOfLastNode = identityOfFirstNode + Short.toUnsignedInt(buffer.getShort(indexBytes + OFFSET_NB));

                result.add(new Sector(identityOfFirstNode, identityOfLastNode));
            }
        }

        return result;
    }
}
