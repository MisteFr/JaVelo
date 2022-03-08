package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;
import ch.epfl.javelo.Bits;

import java.nio.*;

/**
 * GraphEdges class
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    /**
     * Size of an edge in byte (8 bits) in the edgesBuffer
     */
    private final static int BBUFFER_EDGE_ENTRY_SIZE = 10;

    /**
     * Offset of the identity of the destination node and direction of the edge
     */
    private final static int OFFSET_IDENTITY_AND_DIRECTION = 0;

    /**
     * Offset of the length of the edge in byte in the edgesBuffer
     */
    private final static int OFFSET_LENGTH = 4;

    /**
     * Offset of the altitude difference in byte in the edgesBuffer
     */
    private final static int OFFSET_ALTITUDE_DIFFERENCE = 6;

    /**
     * Offset of the identity of the AttributeSet in byte in the edgesBuffer
     */
    private final static int OFFSET_IDENTITY_ATTRIBUTE_SET = 8;

    /**
     * Offset of the profile type in the 32 bits of a profile
     */
    private final static int OFFSET_TYPE_PROFILE = 30;

    /**
     * Offset of the identity of the first sample of data in the 32 bits of a profile
     */
    private final static int OFFSET_IDENTITY_FIRST_SAMPLE = 0;

    /**
     * Length of the profile type in the 32 bits of a profile
     */
    private final static int LENGTH_TYPE_PROFILE = 2;

    /**
     * Length of the identity of the first sample of data in the 32 bits of a profile
     */
    private final static int LENGTH_IDENTITY_FIRST_SAMPLE = 30;


    /**
     * Check if the edge of identity 'edgeId' goes in the opposite direction of the OSM path from which it comes
     * index parameter of getInt() is in bytes as we are working with a ByteBuffer.
     *
     * @param edgeId id of the edge
     * @return boolean true if inverted, false otherwise
     */

    public boolean isInverted(int edgeId) {
        return edgesBuffer.getInt((edgeId * BBUFFER_EDGE_ENTRY_SIZE) + OFFSET_IDENTITY_AND_DIRECTION) < 0;
    }

    /**
     * Get the identity of the destination node of the edge of identity 'edgeId'
     * index parameter of getInt() is in bytes as we are working with a ByteBuffer
     *
     * @param edgeId id of the edge
     * @return int the target node id
     */

    public int targetNodeId(int edgeId) {
        int data = edgesBuffer.getInt((edgeId * BBUFFER_EDGE_ENTRY_SIZE) + OFFSET_IDENTITY_AND_DIRECTION);
        if (data >= 0) {
            return data;
        } else {
            return ~data;
        }
    }

    /**
     * Get the length in meters of the edge of identity 'edgeId'
     * index parameter of getInt() is in bytes as we are working with a ByteBuffer
     *
     * @param edgeId identity of the edge
     * @return double length in meters of the edge
     */

    public double length(int edgeId) {
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort((edgeId * BBUFFER_EDGE_ENTRY_SIZE) + OFFSET_LENGTH)));
    }

    /**
     * Get the positive elevation in meters of the edge of identity 'edgeId'
     * index parameter of getInt() is in bytes as we are working with a ByteBuffer
     *
     * @param edgeId identity of the edge
     * @return double elevation in meters of the edge
     */

    public double elevationGain(int edgeId) {
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort((edgeId * BBUFFER_EDGE_ENTRY_SIZE) + OFFSET_ALTITUDE_DIFFERENCE)));
    }

    /**
     * Check if the edge of identity 'edgeId' has a profile
     * index parameter of get() is an integer as we are working with an IntBuffer
     *
     * @param edgeId identity of the edge
     * @return boolean true if edge has a profile, false otherwise
     */

    public boolean hasProfile(int edgeId) {
        return Bits.extractUnsigned(profileIds.get(edgeId), 30, 2) > 0;
    }

    /**
     * Return the profile sample of the edge of identity 'edgeId'
     *
     * @param edgeId identity of the edge
     * @return float[] the profileSample of the edge of identity 'edgeId', empty float[] if profileType == 0.
     */

    public float[] profileSamples(int edgeId) {
        int profileType = Bits.extractUnsigned(profileIds.get(edgeId), OFFSET_TYPE_PROFILE, LENGTH_TYPE_PROFILE);
        int indexFirstSample = Bits.extractUnsigned(profileIds.get(edgeId), OFFSET_IDENTITY_FIRST_SAMPLE, LENGTH_IDENTITY_FIRST_SAMPLE);
        int samplesNumber = 1 + Math2.ceilDiv(Short.toUnsignedInt(edgesBuffer.getShort((edgeId * BBUFFER_EDGE_ENTRY_SIZE) + OFFSET_LENGTH)), Q28_4.ofInt(2));


        if (profileType == 0) {
            return new float[]{};
        }

        float[] data = new float[samplesNumber];

        switch (profileType) {
            case 1:
                //data isn't compressed
                for (int i = 0; i < samplesNumber; i++) {
                    data[i] = Q28_4.asFloat(Short.toUnsignedInt(elevations().get(indexFirstSample + i)));
                }
                break;

            case 2:
                //data is compressed (8 bits Q4.4)
                data[0] = Q28_4.asFloat(Short.toUnsignedInt(elevations().get(indexFirstSample)));

                //We know we have x samplesNumber samples across 16 + (samplesNumber - 1) * 8 bits.
                //We want to stay 2 iterations in a row on the same portion of 16 bits and counter allows to switch from one portion of 16 bits to another when we are at 2 iterations
                //First 16 bits are ignored as we already read them for data[0]

                int counter8Bits = 1;
                for (int i = 1; i < samplesNumber; i++) {
                    float differenceWithLastSample = Q28_4.asFloat(Bits.extractSigned(elevations().get(indexFirstSample + counter8Bits), (16 - (i % 2) * 8) % 16, 8));
                    data[i] = data[(i - 1)] + differenceWithLastSample;

                    if (i % 2 == 0) {
                        counter8Bits++;
                    }
                }
                break;

            case 3:
                //data is compressed (4 bits Q4.4)
                data[0] = Q28_4.asFloat(Short.toUnsignedInt(elevations().get(indexFirstSample)));

                //We know we have x samplesNumber samples across 16 + (samplesNumber - 1) * 4 bits.
                //We want to stay 4 iterations in a row on the same portion of 16 bits and counter allows to switch from one portion of 16 bits to another when we are at 4 iterations
                //First 16 bits are ignored as we already read them for data[0]

                int counter4Bits = 1;
                for (int i = 1; i < samplesNumber; i++) {
                    float differenceWithLastSample = Q28_4.asFloat(Bits.extractSigned(elevations().get(indexFirstSample + counter4Bits), (16 - (i % 4) * 4) % 16, 4));
                    data[i] = data[(i - 1)] + differenceWithLastSample;

                    if (i % 4 == 0) {
                        counter4Bits++;
                    }
                }
                break;
        }

        //The profile samples are always ordered in the direction of the OSM track.
        //Therefore, if the edge goes in the opposite direction of the OSM track we reverse the data.

        if (isInverted(edgeId)) {
            int i = 0;
            int i2 = data.length - 1;

            while (i < i2) {
                float tempVal = data[i];
                data[i] = data[i2];
                data[i2] = tempVal;
                ++i;
                --i2;
            }
        }

        return data;
    }

    /**
     * Get the identity of the AttributeSet assigned to the edge of identity 'edgeId'
     *
     * @param edgeId identity of the edge
     * @return int identity of the AttributeSet assigned to the edge
     */

    public int attributesIndex(int edgeId) {
        return Short.toUnsignedInt(edgesBuffer.getShort((edgeId * BBUFFER_EDGE_ENTRY_SIZE) + OFFSET_IDENTITY_ATTRIBUTE_SET));
    }
}
