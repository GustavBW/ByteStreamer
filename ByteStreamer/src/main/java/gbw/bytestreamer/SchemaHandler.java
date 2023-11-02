package gbw.bytestreamer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class SchemaHandler implements Runnable{

    private InputStream source;
    private ByteSchema schema;

    public static ValErr<SchemaHandler, Exception> of(File file, ByteSchema schema){
        return ValErr.encapsulate(() -> new SchemaHandler(new FileInputStream(file), schema));
    }

    private SchemaHandler(InputStream source, ByteSchema schema){
        this.source = source;
        this.schema = schema;
    }

    @Override
    public void run() {
        try{
            int nextByteUnsigned;
            while((nextByteUnsigned = source.read()) != -1){
                if(schema.push((byte) nextByteUnsigned)){
                    break;
                }
            }
        }catch(Exception ignored){
            ignored.printStackTrace();
        }
    }
}
