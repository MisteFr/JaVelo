import ch.epfl.javelo.data.GraphSectors;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;

class GraphSectorsTest {

    @Test
    public void SectorsInAreaTest(){

        // This is a 3*3 grid of sectors.
        byte[] tab = {0b00000000,
                0b00000000, //Début du secteur 0. ID noeud 0: 0 ; nNoeuds: 39 // Test avec le noeud d'id 0
                0b00000000,
                0b00000000,
                0b00000000,
                0b00100111,
                0b00000000, //Début du secteur 1. ID noeud 0: 287 ; nNoeuds: 39
                0b00000000,
                0b00000001,
                0b00011111,
                0b00000000,
                0b00100111,
                0b00000000, //Début du secteur 2. ID noeud 0: 1295 ; nNoeuds: 50
                0b00000000,
                0b00000101,
                0b00001111,
                0b00000000,
                0b00110010,
                0b00000000, //Début du secteur 3. ID noeud 0: 1803 ; nNoeuds: 72
                0b00000000,
                0b00000111,
                0b00001011,
                0b00000000,
                0b01001000,
                0b00000000, //Début du secteur 4. ID noeud 0: 3851 ; nNoeuds: 94
                0b00000000,
                0b00001111,
                0b00001011,
                0b00000000,
                0b01011110,
                0b00000000, //Début du secteur 5. ID noeud 0: 5907 ; nNoeuds: 297
                0b00000000,
                0b00010111,
                0b00010011,
                0b00000001,
                0b00101001,
                0b00000000, //Début du secteur 6. ID noeud 0: 5971 ; nNoeuds: 0 // Test avec 0 noeuds
                0b00000000,
                0b00010111,
                0b01010011,
                0b00000000,
                0b00000000,
                0b00000000, //Début du secteur 7. ID noeud 0: 14195 ; nNoeuds: 366
                0b00000000,
                0b00110111,
                0b01110011,
                0b00000001,
                0b01101110,
                0b00000001, //Début du secteur 8. ID noeud 0: 25132927 ; nNoeuds: 1111
                0b01111111,
                0b01111111,
                0b01111111,
                0b00000100,
                0b01010111};

        ByteBuffer b = ByteBuffer.wrap(tab);
        GraphSectors gs = new GraphSectors(b);

        // Normal case

        ArrayList<GraphSectors.Sector> expectedResult = new ArrayList<>();

        // On prend en bas à gauche, par rapport à MIN_E = 2485000 et MIN_N = 1075000. Un point aux coordonnées (100,100) avec une distance de 0.
        assertEquals(new GraphSectors.Sector(0, 39), gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 100,
                SwissBounds.MIN_N + 100), 0).get(0));

        // On prend le secteur au milieu, avec le point se trouvant exactement au milieu et une distance de 0.
        assertEquals(new GraphSectors.Sector(3851, (3851+94)), gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 1.5*(((double) 349/(double) 3) * 1000),
                SwissBounds.MIN_N + 1.5*(((double) 221/(double) 3) * 1000)), 0).get(0));

        //Même chose, mais avec une distance d'un kilomètre. Comme voulu, ça ne change rien au résultat dans le cadre d'un découpage 3*3 de la grille.
        assertEquals(new GraphSectors.Sector(3851, (3851+94)), gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 1.5*(((double) 349/(double) 3) * 1000),
                SwissBounds.MIN_N + 1.5*(((double) 221/(double) 3) * 1000)), 1000).get(0));

        // Normalement, tous les secteurs doivent s'afficher. Je prends le point pile au milieu avec une distance de 70 km.
        for(GraphSectors.Sector o :  gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 1.5*(((double) 349/(double) 3) * 1000),
                SwissBounds.MIN_N + 1.5*(((double) 221/(double) 3) * 1000)), 70000)){
            System.out.println(o);
        }

        //Ca fonctionne. Normalement, les secteurs 1, 4 et 7 doivent s'afficher:
        System.out.println("SECOND TEST");
        for(GraphSectors.Sector o :  gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 1.5*(((double) 349/(double) 3) * 1000),
                SwissBounds.MIN_N + 1.5*(((double) 221/(double) 3) * 1000)), 45000)){
            System.out.println(o);
        }
        System.out.println("TROISIEME TEST");

        //C'est exactement le comportement attendu. Maintenant, il ne me reste plus qu'à tester les cas où le carré dépasse la distance du carré.
        for(GraphSectors.Sector o :  gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 1.5*(((double) 349/(double) 3) * 1000),
                SwissBounds.MIN_N + 1.5*(((double) 221/(double) 3) * 1000)), 1000000000)){
            System.out.println(o);
        }
        System.out.println("QUATRIEME TEST");

        //Je vais essayer de ne print que les quatre en haut à droite puis les quatre en haut à gauche.
        for(GraphSectors.Sector o :  gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 2.5*(((double) 349/(double) 3) * 1000),
                SwissBounds.MIN_N + 2.5*(((double) 221/(double) 3) * 1000)), 60000)){
            System.out.println(o);
        }

        System.out.println("CINQUIEME TEST");
        for(GraphSectors.Sector o :  gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 0.5*(((double) 349/(double) 3) * 1000),
                SwissBounds.MIN_N + 2.5*(((double) 221/(double) 3) * 1000)), 60000)){
            System.out.println(o);
        }

        //Parfait.

        assertThrows(AssertionError.class, () -> {
            gs.sectorsInArea(new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N),-1);
        });
    }
}