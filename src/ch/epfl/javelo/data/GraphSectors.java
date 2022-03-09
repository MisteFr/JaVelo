package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * GraphSectors record
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


    //TODO que se passe-t-il si un des bytes a son bit de poids fort à 1 ? Normalement ce n'est pas possible ?

    //TODO: Utiliser Width de Swissbounds !! Pour faire plus propre, par exemple Width / 128

    /**
     *
     * @param center PointCh parameter that corresponds to the center of the drawn square
     * @param distance The distance of the middle of each side of the square from the center PointCh parameter.
     * @return A list of the Sectors that are, even partly, in the scope of the square.
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {

        assert distance >= 0; // TODO: Est ce attendu ?

        int nSecteursParDimension = 3;
        ArrayList<Sector> result = new ArrayList<>();

        int bottomEIndex = (int) (((center.e() - distance) - SwissBounds.MIN_E) / (SwissBounds.WIDTH / SECTORS_ON_AXIS));
        bottomEIndex = (bottomEIndex < 0) ? 0 : bottomEIndex;
        int bottomNIndex = (int) (((center.n() - distance) - SwissBounds.MIN_N) / (SwissBounds.HEIGHT / SECTORS_ON_AXIS));
        bottomNIndex = (bottomNIndex < 0) ? 0 : bottomNIndex;
        // I have the (bottomEIndex, bottomNIndex) sector, I need to find its index in the buffer.

        int topEIndex = (int) (((center.e() + distance) - SwissBounds.MIN_E) / (SwissBounds.WIDTH / SECTORS_ON_AXIS));
        topEIndex = (topEIndex > 127) ? 127: topEIndex;
        int topNIndex = (int) (((center.n() + distance) - SwissBounds.MIN_N) / (SwissBounds.HEIGHT / SECTORS_ON_AXIS));
        topNIndex = (topNIndex > 127) ? 127: topNIndex;
        // I have the (topEIndex, topNIndex) sector, I need to find its index in the buffer.

        // This is what I do now.
        int index;
        int indexBytes;
        int identityOfFirstNode;
        int identityOfLastNode;

        //We loop through the appropriate sectors (borders of our scope was defined earlier in the method) to add them to the result ArrayList.
        for (int i = bottomNIndex; i <= topNIndex; i++) { //TODO : vérifier inégalités strictes.
            for (int j = bottomEIndex; j <= topEIndex; j++) {

                index = 128 * i + j;
                indexBytes = index * SECTOR_BYTES;
                identityOfFirstNode = buffer.getInt(indexBytes);
                identityOfLastNode = identityOfFirstNode + Short.toUnsignedInt(buffer.getShort(indexBytes + OFFSET_NB));

                result.add(new Sector(identityOfFirstNode, identityOfLastNode));
            }
        }

        return result;
    }
}
