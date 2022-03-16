import ch.epfl.javelo.routing.ElevationProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileTest {

    @Test
    public void ElevationProfileTest(){
        //length = 0
        assertThrows(IllegalArgumentException.class, () -> {
           new ElevationProfile(0, new float[5]);
        });
        //length < 0
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(0, new float[5]);
        });
        //less than two samples
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(7, new float[1]);
        });
    }

    @Test
    public void lengthTest(){
        assertEquals(7.7, (new ElevationProfile(7.7, new float[4])).length());
        assertEquals(98, (new ElevationProfile(98, new float[4])).length());
    }

    //TODO correct approximation problem with diff, conversion float double. Apart from that, works well.
    @Test
    public void totalAscentAndDescentTests(){
        float[] s = {380, 381.5F, 379, 378, 382, 382.1F, 383.7F, 381}; // pretty realistic sample
        assertEquals(7.2F, (new ElevationProfile(9, s)).totalAscent());
        assertEquals(6.2F,(new ElevationProfile(9, s)).totalDescent());
    }


    //TODO same problem with approximations. Arthur a certainement réglé ça par le passé. Je sais que je peux rajouter un delta pour les comparaisons,
    //TODO mais il doit y avoir moyen d'avoir la bonne valeur exactement comme on l'attend à la fin plutôt que de rendre le testing plus permissif.
    @Test
    public void minAndMaxElevationTests(){
        float[] s = {380, 381.5F, 379, 378.01F, 382, 382.1F, 383.7F, 381};
        ElevationProfile e = new ElevationProfile(9, s);
        assertEquals(383.7F, e.maxElevation());
        assertEquals(378.01F, e.minElevation());

    }

    /**
     * Ce test est assez léger car on a déjà bien testé sampled.
     */
    @Test
    public void elevationAtTest(){
        float[] s1 = {380, 381.5F, 379, 378, 382, 382.1F, 383.7F, 381};
        float[] s2 = {0, 1};
        ElevationProfile e1 = new ElevationProfile(14, s1);
        ElevationProfile e2 = new ElevationProfile(4, s2);

        //tests normaux
        assertEquals(380, e1.elevationAt(0));
        assertEquals(382, e1.elevationAt(8));
        assertEquals(0.5, e2.elevationAt(2));
        assertEquals(1, e2.elevationAt(4));

        //tests sur les valeurs avant 0
        assertEquals(0, e2.elevationAt(-76));
        assertEquals(380, e1.elevationAt(-52));

        //tests sur les valeurs après length
        assertEquals(381, e1.elevationAt(15));
        assertEquals(381, e1.elevationAt(999));
        assertEquals(1, e2.elevationAt(12));
    }
}