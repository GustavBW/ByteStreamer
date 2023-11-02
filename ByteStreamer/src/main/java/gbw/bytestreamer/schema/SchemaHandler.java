package gbw.bytestreamer.schema;

import gbw.bytestreamer.util.ValErr;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * A schema handler is but a runnable that pushes each byte from the input stream (source) to the schema <br>
 * It expects the schema to include its own error handling and thus ignores all exception escaping the schema.
 */
public class SchemaHandler implements Runnable{

    private InputStream source;
    private ByteSchema schema;

    public static SchemaHandler of(InputStream source, ByteSchema schema){
        return new SchemaHandler(source, schema);
    }
    public static ValErr<SchemaHandler, Exception> of(String uri, ByteSchema schema){
        return ValErr.encapsulate(() -> SchemaHandler.of(new FileInputStream(uri), schema));
    }
    public static ValErr<SchemaHandler, Exception> of(File file, ByteSchema schema){
        return ValErr.encapsulate(() -> SchemaHandler.of(new FileInputStream(file), schema));
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
                schema.push((byte) nextByteUnsigned);
            }
            schema.close();
        }catch(Exception e){
            schema.close(e);
        }
    }
}
