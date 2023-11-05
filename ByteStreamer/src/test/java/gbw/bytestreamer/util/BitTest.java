package gbw.bytestreamer.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitTest {

    @Test
    void valueAt() {
        byte b0 = (byte) 0b10101010;
        assertFalse(Bit.valueAt(b0, 0));
        assertTrue(Bit.valueAt(b0, 1));
        assertFalse(Bit.valueAt(b0, 2));
        assertTrue(Bit.valueAt(b0, 3));
        assertFalse(Bit.valueAt(b0, 4));
        assertTrue(Bit.valueAt(b0, 5));
        assertFalse(Bit.valueAt(b0, 6));
        assertTrue(Bit.valueAt(b0, 7));

        byte b1 = (byte) 0b11110000;
        assertFalse(Bit.valueAt(b1, 0));
        assertFalse(Bit.valueAt(b1, 1));
        assertFalse(Bit.valueAt(b1, 2));
        assertFalse(Bit.valueAt(b1, 3));
        assertTrue(Bit.valueAt(b1, 4));
        assertTrue(Bit.valueAt(b1, 5));
        assertTrue(Bit.valueAt(b1, 6));
        assertTrue(Bit.valueAt(b1, 7));

        byte b2 = (byte) 0b00000000; // All bits off
        for (int i = 0; i < 8; i++) {
            assertFalse(Bit.valueAt(b2, i));
        }

        byte b3 = (byte) 0b11111111; // All bits on
        for (int i = 0; i < 8; i++) {
            assertTrue(Bit.valueAt(b3, i));
        }
    }
    @Test
    void valueAtShort() {
        short s0 = (short) 0b1010101010101010;
        for(int i = 0; i < Short.BYTES * 8; i++){
            if(i % 2 == 0){
                assertFalse(Bit.valueAt(s0, i));
            }else{
                assertTrue(Bit.valueAt(s0, i));
            }
        }

        short s1 = (short) 0b1111111100000000;
        for(int i = 0; i < Short.BYTES * 8; i++){
            if(i < Short.BYTES * 8 / 2){
                assertFalse(Bit.valueAt(s1, i));
            }else{
                assertTrue(Bit.valueAt(s1, i));
            }
        }

        short s2 = (short) 0b0000000000000000; // All bits off
        for (int i = 0; i < Short.BYTES * 8; i++) {
            assertFalse(Bit.valueAt(s2, i));
        }

        short s3 = (short) 0b1111111111111111; // All bits on
        for (int i = 0; i < Short.BYTES * 8; i++) {
            assertTrue(Bit.valueAt(s3, i));
        }
    }
    @Test
    void valueAtInt() {
        int i0 = 0b10101010101010101010101010101010;
        for(int i = 0; i < Integer.BYTES * 8; i++){
            if(i % 2 == 0){
                assertFalse(Bit.valueAt(i0, i));
            }else{
                assertTrue(Bit.valueAt(i0, i));
            }
        }

        int i1 = 0b11111111111111110000000000000000;
        for(int i = 0; i < Integer.BYTES * 8; i++){
            if(i < Integer.BYTES * 8 / 2){
                assertFalse(Bit.valueAt(i1, i));
            }else{
                assertTrue(Bit.valueAt(i1, i));
            }
        }

        int i2 = 0b00000000000000000000000000000000; // All bits off
        for (int i = 0; i < Integer.BYTES * 8; i++) {
            assertFalse(Bit.valueAt(i2, i));
        }

        int i3 = 0b11111111111111111111111111111111; // All bits on
        for (int i = 0; i < Integer.BYTES * 8; i++) {
            assertTrue(Bit.valueAt(i3, i));
        }
    }

    @Test
    void valueAtLong() {
        long i0 = 0b1010101010101010101010101010101010101010101010101010101010101010L;
        for(int i = 0; i < Long.BYTES * 8; i++){
            if(i % 2 == 0){
                assertFalse(Bit.valueAt(i0, i));
            }else{
                assertTrue(Bit.valueAt(i0, i));
            }
        }

        long i1 = 0b1111111111111111111111111111111100000000000000000000000000000000L;
        for(int i = 0; i < Long.BYTES * 8; i++){
            if(i < Long.BYTES * 8 / 2){
                assertFalse(Bit.valueAt(i1, i));
            }else{
                assertTrue(Bit.valueAt(i1, i));
            }
        }

        long i2 = 0b0000000000000000000000000000000000000000000000000000000000000000L; // All bits off
        for (int i = 0; i < Long.BYTES * 8; i++) {
            assertFalse(Bit.valueAt(i2, i));
        }

        long i3 = 0b1111111111111111111111111111111111111111111111111111111111111111L; // All bits on
        for (int i = 0; i < Long.BYTES * 8; i++) {
            assertTrue(Bit.valueAt(i3, i));
        }
    }

    @Test
    void set() {
        byte b1 = (byte) 0b11011010;
        byte result1 = Bit.set(b1, 0, true);
        assertEquals((byte) 0b11011011, result1);

        byte result2 = Bit.set(b1, 4, false);
        assertEquals((byte) 0b11001010, result2);

        byte result3 = Bit.set(b1, 7, false);
        assertEquals((byte) 0b01011010, result3);

        byte result4 = Bit.set(b1, 2, true);
        assertEquals((byte) 0b11011110, result4);
    }

    @Test
    void representationOf() {
        byte b1 = (byte) 0b11011010; // 218 in decimal
        boolean[] expected1 = {true, true, false, true, true, false, true, false};
        assertArrayEquals(expected1, Bit.representationOf(b1));

        byte b2 = (byte) 0b00000000; // All bits off
        boolean[] expected2 = {false, false, false, false, false, false, false, false};
        assertArrayEquals(expected2, Bit.representationOf(b2));

        byte b3 = (byte) 0b11111111; // All bits on
        boolean[] expected3 = {true, true, true, true, true, true, true, true};
        assertArrayEquals(expected3, Bit.representationOf(b3));
    }
}