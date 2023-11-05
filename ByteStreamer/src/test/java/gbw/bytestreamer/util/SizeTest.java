package gbw.bytestreamer.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//Size relies completely on gbw.bytestreamer.util.Type
//
class SizeTest {

    @Test
    public void of() {
        //Complex types
        assertEquals(Byte.BYTES, Size.of(Byte.class));
        assertEquals(Short.BYTES, Size.of(Short.class));
        assertEquals(Integer.BYTES, Size.of(Integer.class));
        assertEquals(Long.BYTES, Size.of(Long.class));
        assertEquals(Float.BYTES, Size.of(Float.class));
        assertEquals(Double.BYTES, Size.of(Double.class));


        //Primitive types
        assertEquals(Byte.BYTES, Size.of(byte.class));
        assertEquals(Short.BYTES, Size.of(short.class));
        assertEquals(Integer.BYTES, Size.of(int.class));
        assertEquals(Long.BYTES, Size.of(long.class));
        assertEquals(Float.BYTES, Size.of(float.class));
        assertEquals(Double.BYTES, Size.of(double.class));

        //Null. Because why... why actually
        assertEquals(1, Size.of((Class<?>) null));

        //Unknowns
        assertEquals(-1, Size.of(String.class));
        assertEquals(-1, Size.of(Object.class));
    }

    @Test
    public void testOfSizeWithIRef() {
        assertEquals(Float.BYTES, Size.of(new Ref<>(3.15f)));
        assertEquals(Integer.BYTES, Size.of(new Ref<>(42)));
        assertEquals(Long.BYTES, Size.of(new Ref<>(1234567890L)));
        assertEquals(Byte.BYTES, Size.of(new Ref<>((byte) 8)));
        //Since String are... cumbersome, this should fail as to calculate the size of the string, the encoding is needed as well.
        assertNotEquals(2 * 3, Size.of(new Ref<>("abc"))); // Assuming 2 bytes per character
        //However if the encoding is provided. It should just return length * encoding.byteAmount.
        assertEquals(3, Size.of("abc", Encodings.UTF_8));
    }
}