package gbw.bytestreamer.util;

/**
 * Java reads from LSB - Least significant bit - first. Opposite normal reading direction.
 * So in regard to positions, the bit at position 0 for byte 0b000000001 would be 1 / on
 */
public class Bit {


    /**
     * @param i8 to read the value of
     * @param position must be within 0 - 7 (both inclusive).
     * @return whether that bit is on (true) or off (false)
     */
    public static boolean valueAt(byte i8, int position) {
        return (i8 & (1 << position)) != 0;
    }
    /**
     * @param i16 to read the value of
     * @param position must be within 0 - 15 (both inclusive).
     * @return whether that bit is on (true) or off (false)
     */
    public static boolean valueAt(short i16, int position){
        return (i16 & (1 << position)) != 0;
    }
    /**
     * @param i32 to read the value of
     * @param position must be within 0 - 31 (both inclusive).
     * @return whether that bit is on (true) or off (false)
     */
    public static boolean valueAt(int i32, int position){
        return (i32 & (1 << position)) != 0;
    }
    /**
     * @param i64 to read the value of
     * @param position must be within 0 - 63 (both inclusive).
     * @return whether that bit is on (true) or off (false)
     */
    public static boolean valueAt(long i64, int position){
        return (i64 & (1L << position)) != 0;
    }

    /**
     * Flips the specified bit in the byte and returns a new byte.
     * @param b byte to set the bit in.
     * @param position position of bit to be set.1
     * @param on true => 1, false == 0
     * @return a new byte
     */
    public static byte set(byte b, int position, boolean on){
        if(on){
            // Set the bit at the specified position to 1
            return (byte) (b | (1 << position));
        }else{
            // Set the bit at the specified position to 0
            return (byte) (b & ~(1 << position));
        }
    }

    /**
     *
     * @param bytes
     * @return A bool array representing which bits where on or off in the order so that the representation of the bit at position 0 is at index 0 for each byte.
     */
    public static boolean[] representationOf(byte... bytes){
        boolean[] bitRepresentation = new boolean[bytes.length * 8];
        int byteNum = 0;
        for(byte b : bytes){
            for(int i = 0; i < 8; i++){
                //If the bit is set / on / 1
                bitRepresentation[byteNum * 8 + (7-i)] = ((b >> i) & 1) == 1;
            }
            byteNum++;
        }
        return bitRepresentation;
    }

}
