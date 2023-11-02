package gbw.io;

import gbw.bytestreamer.NoParse;
import gbw.bytestreamer.UnsafeByteParser;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class UnsafeByteParserTest {

    private static class WierdUncoveredClass{}
    private static final byte[] EMPTY_BUFFER = new byte[0];
    private static final byte[] HELLO_THERE = "hello there".getBytes();
    @Test
    void abstractParse() {
        //Floats - 32
        byte[] floatBytes = ByteBuffer.allocate(4).putFloat(3.14f).array();
        assertDoesNotThrow(() -> UnsafeByteParser.parse(Float.class, floatBytes));
        assertEquals(3.14f, UnsafeByteParser.parse(Float.class, floatBytes), 0.001f); // Check if the parsed float is approximately equal
        //However, it should fail on not enough bytes - buffer underflow
        assertNull(UnsafeByteParser.parse(Float.class, EMPTY_BUFFER));

        //Integers
        byte[] intBytes = ByteBuffer.allocate(4).putInt(42).array();
        assertDoesNotThrow(() -> UnsafeByteParser.parse(Integer.class, intBytes));
        assertEquals(42, UnsafeByteParser.parse(Integer.class, intBytes));
        //However, it should fail on not enough bytes - buffer underflow
        assertNull(UnsafeByteParser.parse(Integer.class, EMPTY_BUFFER));

        //Int64 - Long
        byte[] longBytes = ByteBuffer.allocate(8).putLong(1234567890L).array();
        assertDoesNotThrow(() -> UnsafeByteParser.parse(Long.class, longBytes));
        assertEquals(1234567890L, UnsafeByteParser.parse(Long.class, longBytes));
        //However, it should fail on not enough bytes - buffer underflow
        assertNull(UnsafeByteParser.parse(Long.class, EMPTY_BUFFER));

        //Bytes
        byte[] byteBytes = {65}; // ASCII value for 'A'
        assertDoesNotThrow(() -> UnsafeByteParser.parse(Byte.class, byteBytes));
        assertEquals((byte) 65, UnsafeByteParser.parse(Byte.class, byteBytes));
        //However, it should fail on not enough bytes - buffer underflow
        assertNull(UnsafeByteParser.parse(Byte.class, EMPTY_BUFFER));

        //NoParse
        byte[] noParseBytes = new byte[8]; // Any data will do
        assertDoesNotThrow(() -> UnsafeByteParser.parse(NoParse.class, noParseBytes));
        assertNull(UnsafeByteParser.parse(NoParse.class, noParseBytes));
        //Contrary to the rest, NoParse should always return null.
        assertNull(UnsafeByteParser.parse(NoParse.class, EMPTY_BUFFER));
        assertNull(UnsafeByteParser.parse(NoParse.class, null));


        //String: Succeeds in parsing "hello there"
        assertDoesNotThrow(() -> UnsafeByteParser.parse(String.class, HELLO_THERE));
        assertEquals("hello there", UnsafeByteParser.parse(String.class, HELLO_THERE));

        //Throws on uncovered class
        assertThrows(UnsafeByteParser.UncoveredTypeException.class, () -> UnsafeByteParser.parse(WierdUncoveredClass.class, EMPTY_BUFFER));
    }
}