package gbw.bytestreamer;

import sun.misc.Unsafe;

public class Size {

    private static final String sFloat = Float.class.toString();

    public static int of(Class<?> clazz) {
        if (clazz == int.class) {
            return Integer.BYTES; // Size of int in bytes
        } else if (clazz == byte.class) {
            return Byte.BYTES; // Size of byte in bytes
        } else if (clazz == char.class) {
            return Character.BYTES; // Size of char in bytes
        } else if (clazz == short.class) {
            return Short.BYTES; // Size of short in bytes
        } else if (clazz == long.class) {
            return Long.BYTES; // Size of long in bytes
        } else if (clazz == float.class) {
            return Float.BYTES; // Size of float in bytes
        } else if (clazz == double.class) {
            return Double.BYTES; // Size of double in bytes
        } else if (clazz == boolean.class) {
            return 1; // Size of boolean is typically 1 byte
        } else {
            return -1;
        }
    }


}
