package gbw.bytestreamer.util;

import gbw.bytestreamer.util.Type;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeTest {

    private static final class WierdTestClass{}
    @Test
    void of() {
        //Primitive types - should succeed
        assertEquals(Type.INT8, Type.of(byte.class));
        assertEquals(Type.INT16, Type.of(short.class));
        assertEquals(Type.INT32, Type.of(int.class));
        assertEquals(Type.INT64, Type.of(long.class));
        assertEquals(Type.FLOAT32, Type.of(float.class));
        assertEquals(Type.FLOAT64, Type.of(double.class));

        //Complex types - should succeed
        assertEquals(Type.INT8, Type.of(Byte.class));
        assertEquals(Type.INT16, Type.of(Short.class));
        assertEquals(Type.INT32, Type.of(Integer.class));
        assertEquals(Type.INT64, Type.of(Long.class));
        assertEquals(Type.FLOAT32, Type.of(Float.class));
        assertEquals(Type.FLOAT64, Type.of(Double.class));
        assertEquals(Type.STRING, Type.of("String".getClass()));
        assertEquals(Type.STRING, Type.of(String.class));

        //Unknown complex type, should be unknown
        assertEquals(Type.UNKNOWN, Type.of(WierdTestClass.class));
        assertEquals(Type.UNKNOWN, Type.of(new WierdTestClass().getClass()));
    }
}