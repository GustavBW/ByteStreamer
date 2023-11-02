package gbw.bytestreamer.util;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class UnsafeBufferParser {

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
        if (clazz == NoParse.class) {
            return null;
        }
        Type type = Type.of(clazz);
        if(type == Type.UNKNOWN){
            throw new UncoveredTypeException("Type not covered by UnsafeBufferParser: " + clazz.toString());
        }
        ByteBuffer wrapped = ByteBuffer.wrap(buffer);
        try{
            return switch (type){
                case FLOAT32 -> (T) (Object) wrapped.getFloat();
                case INT32 -> (T) (Object) wrapped.getInt();
                case INT64 -> (T) (Object) wrapped.getLong();
                case INT8 -> (T) (Object) buffer[buffer.length - 1];
                case STRING -> (T) new String(buffer);
                default -> null;
            };
        }catch (BufferUnderflowException e){
            return null;
        }
    }



}
