package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public record GraphSectors(ByteBuffer buffer) {

    private static final int OFFSET_ID = 0;
    private static final int OFFSET_NB = OFFSET_ID + Integer.BYTES;
    private static final int SECTOR_BYTES = Integer.BYTES + Short.BYTES;


    public record Sector(int startNodeId, int endNodeId) {}


    // TODO: Premier problème, et si un de mes deux points est au dessus ou en dessous des valeurs possibles ?
    //  Facile à patch, je mets une condition sur la donnée avec un ternary operator pour aller plus vite.

    //TODO que se passe-t-il si un des bytes a son bit de poids fort à 1 ?


    public List<Sector> sectorsInArea(PointCh center, double distance) {

        assert distance >= 0; // TODO: Est ce attendu ?

        int nSecteursParDimension = 3;
        ArrayList<Sector> result = new ArrayList<>();

        //int bottomEIndex = (int) (((center.e() - distance) - SwissBounds.MIN_E) / (2.7265625 * 1000)); //!!!!! REMETTRE LES VALEURS POUR LES ADAPTER A UNE TAILLE DE 128*128
        int bottomEIndex = (int) (((center.e() - distance) - SwissBounds.MIN_E) / (((double) 349/(double) nSecteursParDimension)* 1000));
        bottomEIndex = (bottomEIndex < 0) ? 0 : bottomEIndex;
        //int bottomNIndex = (int) (((center.n() - distance) - SwissBounds.MIN_N) / (1.7265625 * 1000)); //!!!!! REMETTRE LES VALEURS POUR LES ADAPTER A UNE TAILLE DE 128*128
        int bottomNIndex = (int) (((center.n() - distance) - SwissBounds.MIN_N) / (((double) 221/(double) nSecteursParDimension) * 1000));
        bottomNIndex = (bottomNIndex < 0) ? 0 : bottomNIndex;
        // I have the (bottomEIndex, bottomNIndex) sector, I need to find its index in the buffer.

        //int topEIndex = (int) (((center.e()) + distance) / (2.7265625 * 1000)); //!!!!! REMETTRE LES VALEURS POUR LES ADAPTER A UNE TAILLE DE 128*128
        //topEIndex = (topEIndex > 127) ? 127: topEIndex;
        int topEIndex = (int) (((center.e() + distance) - SwissBounds.MIN_E) / (((double) 349/(double) nSecteursParDimension) * 1000));
        topEIndex = (topEIndex > nSecteursParDimension - 1) ? nSecteursParDimension - 1 : topEIndex;
        //int topNIndex = (int) (((center.n()) + distance) / (1.7265625 * 1000)); //!!!!! REMETTRE LES VALEURS POUR LES ADAPTER A UNE TAILLE DE 128*128
        //topNIndex = (topNIndex > 127) ? 127: topNIndex;
        int topNIndex = (int) (((center.n() + distance) - SwissBounds.MIN_N) / (((double) 221/(double) nSecteursParDimension) * 1000));
        topNIndex = (topNIndex > nSecteursParDimension - 1) ? nSecteursParDimension - 1 : topNIndex;
        // I have the (topEIndex, topNIndex) sector, I need to find its index in the buffer.

        // This is what I do now.
        int index;
        int indexBytes;
        int identityOfFirstNode;
        int identityOfLastNode;

        for (int i = bottomNIndex; i <= topNIndex; i++) { //TODO : vérifier inégalités strictes.
            for (int j = bottomEIndex; j <= topEIndex; j++) {

                //index = 128 * i + j; //!!!!! REMETTRE LES VALEURS POUR LES ADAPTER A UNE TAILLE DE 128*128
                index = nSecteursParDimension * i + j;
                indexBytes = index * SECTOR_BYTES;
                //System.out.println(indexBytes);
                identityOfFirstNode = buffer.getInt(indexBytes);
                identityOfLastNode = identityOfFirstNode + Short.toUnsignedInt(buffer.getShort(indexBytes + OFFSET_NB));

                result.add(new Sector(identityOfFirstNode, identityOfLastNode));
            }
        }

        return result;
    }
}
