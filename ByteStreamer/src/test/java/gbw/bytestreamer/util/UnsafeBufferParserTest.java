package gbw.bytestreamer.util;

import gbw.bytestreamer.util.NoParse;
import gbw.bytestreamer.util.UnsafeBufferParser;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class UnsafeBufferParserTest {

    private static class WierdUncoveredClass{}
    private static final byte[] EMPTY_BUFFER = new byte[0];
    private static final byte[] HELLO_THERE = "hello there".getBytes();
    @Test
    void abstractParse() {
        //Floats - 32
        byte[] floatBytes = ByteBuffer.allocate(4).putFloat(3.14f).array();
        assertDoesNotThrow(() -> UnsafeBufferParser.parse(Float.class, floatBytes));
        assertEquals(3.14f, UnsafeBufferParser.parse(Float.class, floatBytes), 0.001f); // Check if the parsed float is approximately equal
        //However, it should fail on not enough bytes - buffer underflow
        assertNull(UnsafeBufferParser.parse(Float.class, EMPTY_BUFFER));

        //Integers
        byte[] intBytes = ByteBuffer.allocate(4).putInt(42).array();
        assertDoesNotThrow(() -> UnsafeBufferParser.parse(Integer.class, intBytes));
        assertEquals(42, UnsafeBufferParser.parse(Integer.class, intBytes));
        //However, it should fail on not enough bytes - buffer underflow
        assertNull(UnsafeBufferParser.parse(Integer.class, EMPTY_BUFFER));

        //Int64 - Long
        byte[] longBytes = ByteBuffer.allocate(8).putLong(1234567890L).array();
        assertDoesNotThrow(() -> UnsafeBufferParser.parse(Long.class, longBytes));
        assertEquals(1234567890L, UnsafeBufferParser.parse(Long.class, longBytes));
        //However, it should fail on not enough bytes - buffer underflow
        assertNull(UnsafeBufferParser.parse(Long.class, EMPTY_BUFFER));

        //Bytes
        byte[] byteBytes = {65}; // ASCII value for 'A'
        assertDoesNotThrow(() -> UnsafeBufferParser.parse(Byte.class, byteBytes));
        assertEquals((byte) 65, UnsafeBufferParser.parse(Byte.class, byteBytes));
        //However, it should fail on not enough bytes - buffer underflow
        assertNull(UnsafeBufferParser.parse(Byte.class, EMPTY_BUFFER));

        //NoParse
        byte[] noParseBytes = new byte[8]; // Any data will do
        assertDoesNotThrow(() -> UnsafeBufferParser.parse(NoParse.class, noParseBytes));
        assertNull(UnsafeBufferParser.parse(NoParse.class, noParseBytes));
        //Contrary to the rest, NoParse should always return null.
        assertNull(UnsafeBufferParser.parse(NoParse.class, EMPTY_BUFFER));
        assertNull(UnsafeBufferParser.parse(NoParse.class, null));


        //String: Succeeds in parsing "hello there"
        assertDoesNotThrow(() -> UnsafeBufferParser.parse(String.class, HELLO_THERE));
        assertEquals("hello there", UnsafeBufferParser.parse(String.class, HELLO_THERE));

        //Throws on uncovered class
        assertThrows(UnsafeBufferParser.UncoveredTypeException.class, () -> UnsafeBufferParser.parse(WierdUncoveredClass.class, EMPTY_BUFFER));
    }
}