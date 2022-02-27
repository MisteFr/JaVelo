package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;

/**
 * AttributeSet Record
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */

public record AttributeSet(long bits) {

    /**
     * Compact constructor that checks for invalid bits
     * @throws IllegalArgumentException if bits value contains a bit value of 1 corresponding to an attribute
     * that doesn't exist
     */

    public AttributeSet {
        //we want to make sure bits at index 62 and 63 are 0s.
        long mask62_63 = 1L << 62 | 1L << 63;
        Preconditions.checkArgument((bits & mask62_63) == 0);
    }

    /**
     * Construct an AttributeSet with a given number of attributes.
     *
     *
     * @param attributes attributes for the new AttributeSet
     * @return AttributeSet object containing the desired attributes
     */

    public static AttributeSet of(Attribute... attributes){
        long bits = 0L;
        for (Attribute attr : attributes){
            //we create a mask at attribute index value to set the bit to 1 in bits.
            long maskPos = 1L << attr.ordinal();
            bits = bits | maskPos;
        }
        return new AttributeSet(bits);
    }

    /**
     * Test if the AttributeSet contains a given attribute
     *
     * @param attribute attribute to test
     * @return Boolean if the attribute is in AttributeSet or not
     */
    public boolean contains (Attribute attribute){
        long maskPos = 1L << attribute.ordinal();
        return (bits & maskPos) == maskPos;
    }

    /**
     * Test if the intersection if this AttributeSet (this) and the argument that is empty
     * TODO: optimize/improve?
     * @param that AttributeSet to intersect with this instance
     * @return Boolean if the intersection if empty or not
     */

    public boolean intersects(AttributeSet that){
        for(int i = 0; i < Attribute.COUNT; i ++){
            long maskPos = 1L << i;
            if((that.bits & maskPos) == maskPos && (bits & maskPos) == maskPos){
                return  true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",", "{", "}");
        for(Attribute attr : Attribute.ALL){
            if(contains(attr)){
                j.add(attr.keyValue());
            }
        }
        return j.toString();
    }
}
