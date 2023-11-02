package gbw.bytestreamer.util;

import java.io.ObjectOutputStream;

/**
 * NB: "Null" is for deserializing purposes considered a single byte with all zeros.
 */
public class Size {

    /**
     * @return -1 if the type is not known, else the byte size of the type
     */
    public static int of(Class<?> clazz){
        return switch (Type.of(clazz)) {
            case NULL -> 1;
            case INT8 -> Byte.BYTES;
            case INT16 -> Short.BYTES;
            case INT32 -> Integer.BYTES;
            case INT64 -> Long.BYTES;
            case FLOAT32 -> Float.BYTES;
            case FLOAT64 -> Double.BYTES;
            default -> -1;
        };
    }

    /**
     * Shorthand for: Size.of(Ref.get().getClass()) <br>
     * @return -1 if the type is not known, else the byte size of the type
     */
    public static int of(IRef<?> ref){
        return of(ref.get().getClass());
    }

    public static int of(String string, Encodings encoding){
        return string.length() * encoding.bytesPerChar;
    }

}
