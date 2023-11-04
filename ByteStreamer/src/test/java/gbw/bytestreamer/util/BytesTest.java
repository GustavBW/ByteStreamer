package gbw.bytestreamer.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BytesTest {

    @Test
    void testConcat() {
        byte[] a = {1};
        byte[] b = {2};
        byte[] c = {3};
        byte[] d = {4};
        byte[] e = {5};
        byte[] f = {6};
        byte[] expected = {1,2,3,4,5,6};
        byte[] actual = Bytes.concat(a,b,c,d,e,f);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testOfShort() {
        short i16 = 145;
        byte[] expected = {(byte) 145, 0};
        byte[] result = Bytes.of(i16);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testOfInt() {
        int i32 = 1234567890;
        byte[] expected = {(byte) 0xD2, 0x02,(byte) 0x96, 0x49};
        byte[] result = Bytes.of(i32);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testOfLong() {
        for(long i64 : List.of(1234567890123456789L, Long.MAX_VALUE, Long.MIN_VALUE, 0L)){
            final long copy = i64;
            byte[] expected = new byte[8];

            for (int i = 0; i < 8; i++) {
                expected[i] = (byte) (i64 & 0xFF);
                i64 >>= 8;
            }

            byte[] result = Bytes.of(copy);
            assertArrayEquals(expected, result);
        }
    }

    @Test
    public void testOfFloat() {
        for(float f32: List.of(123.456f, Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_NORMAL, Float.NaN)){
            int intBits = Float.floatToIntBits(f32);
            byte[] expected = new byte[4];
            expected[0] = (byte) (intBits & 0xFF);
            expected[1] = (byte) ((intBits >> 8) & 0xFF);
            expected[2] = (byte) ((intBits >> 16) & 0xFF);
            expected[3] = (byte) ((intBits >> 24) & 0xFF);

            byte[] result = Bytes.of(f32);
            assertArrayEquals(expected, result);
        }
    }

    @Test
    public void testOfDouble() {
        for(double f64 : List.of(123.456, Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_NORMAL, Double.NaN)){
            long intBits = Double.doubleToLongBits(f64);
            byte[] expected = new byte[8];
            expected[0] = (byte) (intBits & 0xFF);
            expected[1] = (byte) ((intBits >> 8) & 0xFF);
            expected[2] = (byte) ((intBits >> 16) & 0xFF);
            expected[3] = (byte) ((intBits >> 24) & 0xFF);
            expected[4] = (byte) ((intBits >> 32) & 0xFF);
            expected[5] = (byte) ((intBits >> 40) & 0xFF);
            expected[6] = (byte) ((intBits >> 48) & 0xFF);
            expected[7] = (byte) ((intBits >> 56) & 0xFF);
            byte[] result = Bytes.of(f64);
            assertArrayEquals(expected, result);
        }
    }
}