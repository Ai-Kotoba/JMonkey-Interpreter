import org.junit.jupiter.api.Test;
import object.String;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import object.Boolean;
import object.Integer;

public class ObjectTest {
    @Test
    void testStringHashKey(){
        var hello1 = new String("Hello World");
        var hello2 = new String( "Hello World");
        var diff1 = new String( "My name is johnny");
        var diff2 = new String( "My name is johnny");
        assertEquals(hello1.hashKey(), hello2.hashKey());
        assertEquals(diff1.hashKey(), diff2.hashKey());
        assertNotEquals(hello1.hashCode(), diff1.hashCode());
    }
    @Test
    void testBooleanHashKey(){
        var true1 = new Boolean( true);
        var true2 = new Boolean( true);
        var false1 = new Boolean( false);
        var false2 = new Boolean( false);
        assertEquals(true1.hashKey(), true2.hashKey());
        assertEquals(false1.hashKey(), false2.hashKey());
        assertNotEquals(true1.hashCode(), false2.hashCode());
    }
    @Test
    void testIntegerHashKey(){
        var one1 = new Integer( 1);
        var one2 = new Integer( 1);
        var two1 = new Integer( 2);
        var two2 = new Integer( 2);
        assertEquals(one1.hashKey(), one2.hashKey());
        assertEquals(two1.hashKey(), two2.hashKey());
        assertNotEquals(one1.hashCode(), two1.hashCode());
    }
}