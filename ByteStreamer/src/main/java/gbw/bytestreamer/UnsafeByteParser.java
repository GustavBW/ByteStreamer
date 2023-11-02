package gbw.bytestreamer;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class UnsafeByteParser {

    public static class UncoveredTypeException extends RuntimeException{
        public UncoveredTypeException(String message){super(message);}
    }

    /**
     * Throws when a class it cannot parse is given
     * @param clazz Class to parse the bytes as
     * @param buffer Byte array containing the data to parse
     * @return A valid instance of some type T - or null.
     */
    @SuppressWarnings("unchecked")
    public static <T> T parse(Class<T> clazz, byte[] buffer) throws UncoveredTypeException {
        ByteBuffer wrapped = ByteBuffer.wrap(buffer);
        try{
            switch (clazz.toString()){
                case "float", "class java.lang.Float" -> {
                    return (T) (Object) wrapped.getFloat();
                }
                case "int", "class java.lang.Integer" -> {
                    return (T) (Object) wrapped.getInt();
                }
                case "long", "class java.lang.Long" -> {
                    return (T) (Object) wrapped.getLong();
                }
                case "byte", "class java.lang.Byte" -> {
                    return (T) (Object) buffer[buffer.length - 1];
                }
                case "String", "class java.lang.String" -> {
                    return (T) new String(buffer);
                }
                case "NoParse", "class gbw.bytestreamer.NoParse" -> {
                    return null;
                }
                default -> {
                    throw new UncoveredTypeException("Type not covered by UnsafeByteParser: " + clazz.toString());
                }
            }
        }catch (BufferUnderflowException e){
            return null;
        }
    }

}
