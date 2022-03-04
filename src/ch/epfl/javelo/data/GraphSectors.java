package ch.epfl.javelo.data;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public record GraphSectors(ByteBuffer buffer){

    private static final int OFFSET_ID = 0;
    private static final int OFFSET_NB = OFFSET_ID + Integer.BYTES;
    private static final int SECTOR_BYTES = Integer.BYTES + Short.BYTES;



    public record Sector(int startNodeId, int endNodeId){}



    public List<Sector> sectorsInArea(PointCh center, double distance){

        ArrayList<Sector> result = new ArrayList<>();
        int bottomEIndex = (int) (((center.e() - distance) - SwissBounds.MIN_E)/(2.7265625 * 1000));
        int bottomNIndex = (int) (((center.n() - distance) - SwissBounds.MIN_N)/(1.7265625 * 1000));
        // I have the (bottomEIndex, bottomNIndex) sector, I need to find its index in the buffer.

        int topEIndex = (int) (((center.e()) + distance)/(2.7265625 * 1000));
        int topNIndex = (int) (((center.n()) + distance)/(1.7265625 * 1000));
        // I have the (topEIndex, topNIndex) sector, I need to find its index in the buffer.

        // This is what I do now.
        int index;
        int indexBytes;
        int identityOfFirstNode;
        int identityOfLastNode;

        for (int i = bottomNIndex; i <= topNIndex; i++) {
            for (int j = bottomEIndex; j < topEIndex; j++) {

                index = 128 * i + j;
                indexBytes = index * SECTOR_BYTES;
                identityOfFirstNode = buffer.getInt(indexBytes); //Pas assez
                identityOfLastNode = identityOfFirstNode + Short.toUnsignedInt(buffer.getShort(indexBytes + OFFSET_NB));

                result.add(new Sector(identityOfFirstNode, identityOfLastNode));
            }
        }

        return result;
    }
}
