package utils;

import gbw.bytestreamer.ValErr;

import java.io.File;
import java.io.FileOutputStream;

public class FileEncoder {

    /**
     * @param fileName File to create or write to if exists
     * @param data to write to the file
     * @return either the file written to, or an error
     */
    public static ValErr<File,Exception> encode(String fileName, byte[] data){
        File file = new File(fileName);
        if(!file.exists()){
            try{
                file.createNewFile();
            }catch(Exception e){
                return ValErr.error(e);
            }
        }

        try(FileOutputStream fos = new FileOutputStream(new File(fileName))){
            fos.write(data);
        }catch(Exception e){
            return ValErr.error(e);
        }

        return ValErr.value(file);
    }

    public static void main(String[] args) {
        //Create a file with some encoded binary
        encode("./test.bin", new byte[]{100});
    }

}
