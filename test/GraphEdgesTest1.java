
import ch.epfl.javelo.data.GraphEdges;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class GraphEdgesTest1 {


    IntBuffer profileIds = IntBuffer.wrap(new int[]{
            // Type : 0. Index du premier échantillon : 1.
            (2 << 30) | 1
    });

    ShortBuffer elevations = ShortBuffer.wrap(new short[]{
            (short) 0,
//            (short) 0x180C, (short) 0xFEFF,
//            (short) 0xFFFE, (short) 0xF000 //TypeIn3
            (short) 0x180C, (short) 0x1212 //Type2
    });


    //PROF TESTS

    @Test
    void isInvertedWorks() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertTrue(edges.isInverted(0));
    }

    @Test
    void nodeTargetWorks() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertEquals(12, edges.targetNodeId(0));
    }

    @Test
    void lengthWorks() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertEquals(16.6875, edges.length(0));
    }

    @Test
    void elevationGainWorks() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertEquals(16.0, edges.elevationGain(0));
    }

    @Test
    void typeOfProfileWorks(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        //assertEquals(2,edges.typeOfProfile(0)); 
    }

    @Test
    void attributeIndexWorks() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertEquals(2022, edges.attributesIndex(0));
    }

    @Test
    void profileSamplesWorksType2NotInverted() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        //Changement - = 4m.
        edgesBuffer.putShort(4, (short) 0x4_0);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        float[] type3InvertedArray = new float[]{
                384.0625f,
                384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        //float[] type3Array = GraphEdges.inverse(type3InvertedArray.clone());
        float[] type2Array = new float[]{
                384.75f, 385.875f, 387f
        }; //0x0180 - 0x1212
        assertArrayEquals(type2Array, edges.profileSamples(0));
    }

    @Test
    void profileWorksWithType3Inverted(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
            (short) 0x180C, (short) 0xFEFF,
            (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        float[] type3InvertedArray = new float[]{
                384.0625f,
                384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        assertArrayEquals(type3InvertedArray, edges.profileSamples(0));
    }

    @Test
    void profileWorksWithType3NotInverted(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        float[] type3Array = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };

        assertArrayEquals(type3Array, edges.profileSamples(0));
    }

    @Test
    void profileWorksWithType0(){

        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        //Longueur : 4m
        edgesBuffer.putShort(4, (short) 0x4_0);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (0 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0, (short) 0x1801, (short) 0x180C, (short) 0x180E //TypeIn0
        });
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        float[] type0Array = new float[0];
        assertArrayEquals(type0Array, edges.profileSamples(0));
    }


    @Test
    void profileWorksWithType1Inverted(){

        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        //Longueur : 4m
        edgesBuffer.putShort(4, (short) 0x4_0);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (1 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0, (short) 0x1801, (short) 0x180C, (short) 0x180E //TypeIn1
        });
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        float[] type1InvertedArray = new float[]{
                384.875f, 384.75f, 384.0625f
        };
        for (int i = 0; i < edges.profileSamples(0).length; ++i){
            System.out.println(edges.profileSamples(0)[i]);
        }
        assertArrayEquals(type1InvertedArray, edges.profileSamples(0));
    }


    //Added
    @Test
    public void eazeaze()
    {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short)((short) 0x10_b / 4f));
// Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        int type = 1;
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (type << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) (0x180C + 1),
                (short) (0x180C + 2), (short) (0x180C + 1)
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertTrue(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        //assertEquals(16.6875 / 2, edges.length(0), 1);


        assertEquals(16.0, edges.elevationGain(0));

        assertEquals(2022, edges.attributesIndex(0));

        float[] expectedSamples = new float[]{
                (float) ((short) (0x180C + 1) / 16.0), (float) ((short) (0x180C + 2)/ 16.0), (float) ((short) (0x180C + 1) / 16.0), (float) (0x180C / 16.0)

        };
        float[] result = edges.profileSamples(0);
        System.out.println(Arrays.toString(result));
        assertArrayEquals(expectedSamples, result);
    }

    @Test
    public void eazeaze2()
    {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short)((short) 0x10_b / 2f));
// Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        int type = 2;
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (type << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xFF00
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertTrue(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        assertEquals(16.6875 / 2, edges.length(0), 1);


        assertEquals(16.0, edges.elevationGain(0));

        assertEquals(2022, edges.attributesIndex(0));

        float[] expectedSamples = new float[]{
                (float)(384.75 - 2*0.125 - 3/16.0), (float)(384.75 - 2*0.125 - 2/16.0), (float)(384.75 - 0.125 - 2/16.0), (float)(384.75 - 0.125 - 1/16.0), (float)(384.75 - 0.125), 384.75f
        };
        float[] result = edges.profileSamples(0);
        System.out.println(Arrays.toString(result));
        assertArrayEquals(expectedSamples, result);
    }


}
