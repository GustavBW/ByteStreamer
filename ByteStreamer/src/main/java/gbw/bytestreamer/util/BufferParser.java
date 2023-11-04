package gbw.bytestreamer.util;

public class BufferParser {
    public static class ParsingException extends RuntimeException{
        public ParsingException(String message){super(message);}
    }

    public static class UncoveredTypeException extends ParsingException{
        public UncoveredTypeException(Class<?> clazz){super(clazz.toString());}
    }
    public static class NotEnoughBytesException extends ParsingException{
        public NotEnoughBytesException(String message){super(message);}
    }


    /**
     * Throws when a class it cannot parse is given
     * @param clazz Class to parse the bytes as
     * @param buffer Byte array containing the data to parse
     * @return A valid instance of some type T - or null.
     * @throws ParsingException
     */
    public static <T> T parse(Class<T> clazz, byte[] buffer) throws ParsingException {
        if (clazz == NoParse.class) {
            return null;
        }
        try{
            return (T) switch (Type.of(clazz)) {
                case NULL -> null;
                case INT8 -> buffer[0];
                case INT16 -> parseShort(buffer);
                case INT32 -> parseInt(buffer);
                case INT64 -> parseLong(buffer);
                case FLOAT32 -> parseFloat(buffer);
                case FLOAT64 -> parseDouble(buffer);
                default -> throw new UncoveredTypeException(clazz);
            };
        }catch (NullPointerException | ArrayIndexOutOfBoundsException e){
            //Wrapping the ArrayIndexOutOfBoundsException in a type that extends ParsingException so that it will be handled
            //correctly by the ByteSchema
            throw new NotEnoughBytesException(e.getMessage());
        }
    }

    /**
     *
     * @param outputArraySized An array to write each parsed element to - of the correct size
     * @return the output array provided as third parameter
     * @param <T> - type to parse each element as.
     * @throws ParsingException
     */
    public static <T extends Number> T[] parse(Class<T> clazz, byte[] buffer, T[] outputArraySized) throws ParsingException {
        int elementSize = Size.of(clazz);
        return null;
    }
    public static short parseShort(byte[] buffer) throws NullPointerException, ArrayIndexOutOfBoundsException {
        return ((short) ((buffer[0] & 0xFF) | (buffer[1] << 8)));
    }
    public static int parseInt(byte[] buffer) throws NullPointerException, ArrayIndexOutOfBoundsException {
        return (buffer[0] & 0xFF | (buffer[1] & 0xFF) << 8 | (buffer[2] & 0xFF) << 16 | (buffer[3] << 24));
    }
    public static long parseLong(byte[] buffer) throws NullPointerException, ArrayIndexOutOfBoundsException {
        return ((buffer[0] & 0xFFL) | (buffer[1] & 0xFFL) << 8 | (buffer[2] & 0xFFL) << 16 | (buffer[3] & 0xFFL) << 24 |
                (buffer[4] & 0xFFL) << 32 | (buffer[5] & 0xFFL) << 40 | (buffer[6] & 0xFFL) << 48 | (buffer[7] & 0xFFL) << 56);
    }
    public static float parseFloat(byte[] buffer) throws NullPointerException, ArrayIndexOutOfBoundsException {
        return Float.intBitsToFloat(parseInt(buffer));
    }
    public static double parseDouble(byte[] buffer) throws NullPointerException, ArrayIndexOutOfBoundsException {
        return Double.longBitsToDouble(parseLong(buffer));
    }




}
