package gbw.bytestreamer.util;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BufferParserTest {



    private static class WierdUncoveredClass{}
    private static final byte[] EMPTY_BUFFER = new byte[0];
    private static final byte[] HELLO_THERE = "hello there".getBytes();
    @Test
    void parse() {
        //Floats - 32
        byte[] floatBytes = Bytes.of(3.14f);
        assertDoesNotThrow(() -> BufferParser.parse(Float.class, floatBytes));
        assertEquals(3.14f, BufferParser.parse(Float.class, floatBytes), 0.001f); // Check if the parsed float is approximately equal
        //However, it should fail on not enough bytes - buffer underflow
        assertThrows(BufferParser.NotEnoughBytesException.class, () -> BufferParser.parse(Float.class, EMPTY_BUFFER));

        //Integers
        assertDoesNotThrow(() -> BufferParser.parse(Integer.class, Bytes.of(42)));
        assertEquals(42, BufferParser.parse(Integer.class, Bytes.of(42)));
        //However, it should fail on not enough bytes - buffer underflow
        assertThrows(BufferParser.NotEnoughBytesException.class, () -> BufferParser.parse(Integer.class, EMPTY_BUFFER));

        //Int64 - Long
        byte[] longBytes = Bytes.of(1234567890L);
        assertDoesNotThrow(() -> BufferParser.parse(Long.class, longBytes));
        assertEquals(1234567890L, BufferParser.parse(Long.class, longBytes));
        //However, it should fail on not enough bytes - buffer underflow
        assertThrows(BufferParser.NotEnoughBytesException.class, () -> BufferParser.parse(Long.class, EMPTY_BUFFER));

        //Bytes
        byte[] byteBytes = {65}; // ASCII value for 'A'
        assertDoesNotThrow(() -> BufferParser.parse(Byte.class, byteBytes));
        assertEquals((byte) 65, BufferParser.parse(Byte.class, byteBytes));
        //However, it should fail on not enough bytes - buffer underflow
        assertThrows(BufferParser.NotEnoughBytesException.class, () -> BufferParser.parse(Byte.class, EMPTY_BUFFER));

        //NoParse
        byte[] noParseBytes = new byte[8]; // Any data will do
        assertDoesNotThrow(() -> BufferParser.parse(NoParse.class, noParseBytes));
        assertNull(BufferParser.parse(NoParse.class, noParseBytes));
        //Contrary to the rest, NoParse should always return null.
        assertNull(BufferParser.parse(NoParse.class, EMPTY_BUFFER));
        assertNull(BufferParser.parse(NoParse.class, null));


        //String: Succeeds in parsing "hello there"
        assertDoesNotThrow(() -> BufferParser.parseString(HELLO_THERE));
        assertEquals("hello there", BufferParser.parseString(HELLO_THERE));

        //Throws on uncovered class
        assertThrows(BufferParser.UncoveredTypeException.class, () -> BufferParser.parse(WierdUncoveredClass.class, EMPTY_BUFFER));
    }
    @Test
    void parseShort() {
        for(short i16 : List.of((short) 0, (short) 1, (short) -1, Short.MAX_VALUE, Short.MIN_VALUE)){
            short expected = i16;
            short actual = BufferParser.parseShort(Bytes.of(i16));
            assertEquals(expected, actual);
        }
    }

    @Test
    void parseInt() {
        for(int i32 : List.of(0,  1,  -1, Integer.MAX_VALUE, Integer.MIN_VALUE)){
            int expected = i32;
            int actual = BufferParser.parseInt(Bytes.of(i32));
            assertEquals(expected, actual);
        }
    }

    @Test
    void parseLong() {
        for(long i64 : List.of(0L,  1L,  -1L, Long.MAX_VALUE, Long.MIN_VALUE)){
            long expected = i64;
            long actual = BufferParser.parseLong(Bytes.of(i64));
            assertEquals(expected, actual);
        }
    }

    @Test
    void parseFloat() {
        for(float f32 : List.of(0.0f,  1.1f,  -1.1f, Float.MAX_VALUE, Float.MIN_VALUE, Float.NaN, Float.MIN_NORMAL)){
            float expected = f32;
            float actual = BufferParser.parseFloat(Bytes.of(f32));
            assertEquals(expected, actual);
        }
    }

    @Test
    void parseDouble() {
        for(double f64 : List.of(0.0d,  1.1d,  -1.1d, Double.MAX_VALUE, Double.MIN_VALUE, Double.NaN, Double.MIN_NORMAL)){
            double expected = f64;
            double actual = BufferParser.parseDouble(Bytes.of(f64));
            assertEquals(expected, actual);
        }
    }

}