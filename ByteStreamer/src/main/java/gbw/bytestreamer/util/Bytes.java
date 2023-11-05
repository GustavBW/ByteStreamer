package gbw.bytestreamer.util;

public class Bytes {

    public static byte[] concat(byte[]... arrs){
        int length = 0;
        for(byte[] arr : arrs) length += arr.length;
        return concat(length, arrs);
    }
    public static byte[] concat(int length, byte[]... arrs){
        byte[] toReturn = new byte[length];
        int destPos = 0;
        for (byte[] arr : arrs) {
            System.arraycopy(arr, 0, toReturn, destPos, arr.length);
            destPos += arr.length;
        }
        return toReturn;
    }
    public static String asZeroesAndOnes(byte... anything){
        StringBuilder sb = new StringBuilder();
        boolean[] bitRepresentation = asBoolArray(anything);
        sb.append("bit len: ").append(bitRepresentation.length).append(" | ");
        int bitNum = 0;
        for(boolean b : bitRepresentation){
            sb.append(b ? "1" : "0");
            bitNum++;
            if(bitNum % 8 == 0){
                sb.append(" ");
            }
        }
        return sb.toString();
    }
    public static boolean[] asBoolArray(byte... any){
        boolean[] bitRepresentation = new boolean[any.length * 8];
        int byteNum = 0;
        for(byte b : any){
            for(int i = 0; i < 8; i++){
                //If the bit is set / on / 1
                bitRepresentation[byteNum * 8 + i] = ((b >> i) & 1) == 1;
            }
            byteNum++;
        }
        return bitRepresentation;
    }

    public static byte[] of(short i16){
        byte[] bytes = new byte[2];

        bytes[0] = (byte) (i16 & 0xFF);
        bytes[1] = (byte) ((i16 >> 8) & 0xFF);

        return bytes;
    }
    public static byte[] of(int i32){
        byte[] bytes = new byte[4];

        bytes[0] = (byte) (i32 & 0xFF);
        bytes[1] = (byte) ((i32 >> 8) & 0xFF);
        bytes[2] = (byte) ((i32 >> 16) & 0xFF);
        bytes[3] = (byte) ((i32 >> 24) & 0xFF);

        return bytes;
    }
    public static byte[] of(long i64){
        byte[] bytes = new byte[8];

        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (i64 & 0xFF);
            i64 >>= 8;
        }

        return bytes;
    }
    public static byte[] of(float f32){
        return of(Float.floatToIntBits(f32));
    }
    public static byte[] of(double f64){
        return of(Double.doubleToLongBits(f64));
    }


}
