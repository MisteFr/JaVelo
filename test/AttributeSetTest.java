import ch.epfl.javelo.data.Attribute;
import ch.epfl.javelo.data.AttributeSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AttributeSetTest {

    @Test
    void testConstructorFunction() {
        //should pass
        new AttributeSet(1000000000000000000L);
        AttributeSet.of(Attribute.LCN_YES);

        //binary representation length is 63, bits at index 62 is 0 -> it is okay
        new AttributeSet(0b001000000000000000000000100000000000000000001010000000000000000L);

        //binary representation length is 64, bits at index 62 and 63 are 0 -> it is okay
        new AttributeSet(0b0001000000000000000000000100000000000000000001010000000000000000L);

        //binary representation length is 62, bits at index 61 is 1 -> it is okay
        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet(0b111000000000000000000000100000000000000000001010000000000000000L);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            //binary representation length is 64, bits at index 63 is 1 -> should fail
            new AttributeSet(0b1001000000000000000000000100000000000000000001010000000000000000L);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            //binary representation length is 63, bits at index 62 is 1 -> should fail.
            new AttributeSet(0b101000000000000000000000100000000000000000001010000000000000000L);
        });
    }

    @Test
    void testToStringFunction() {
        AttributeSet set = AttributeSet.of(Attribute.TRACKTYPE_GRADE1, Attribute.HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());

        AttributeSet set2 = AttributeSet.of(Attribute.HIGHWAY_UNCLASSIFIED, Attribute.HIGHWAY_TRACK, Attribute.HIGHWAY_CYCLEWAY, Attribute.LCN_YES);
        assertEquals("{highway=track,highway=unclassified,highway=cycleway,lcn=yes}", set2.toString());

        AttributeSet set3 = AttributeSet.of(Attribute.MOTORROAD_YES, Attribute.TRACKTYPE_GRADE2, Attribute.RCN_YES, Attribute.ONEWAY_YES);
        assertEquals("{motorroad=yes,tracktype=grade2,oneway=yes,rcn=yes}", set3.toString());
    }

    @Test
    void testContainsFunction() {
        //TODO
    }

    @Test
    void testIntersectFunction() {
        //TODO
    }
}
