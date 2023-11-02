package gbw.bytestreamer.file;

import gbw.bytestreamer.schema.SingleTypeEntry;
import gbw.bytestreamer.util.Ref;
import gbw.bytestreamer.util.ValErr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The reverse of a ByteSchema. Can be used to obtain the corresponding ByteSchema <br>
 * This class takes in one object of some type at a time, transforms them to a byte-representation and writes them to a file. <br>
 * The way each object is written to the file, is as the ByteSchema expects to read them. <br>
 */
public class BinaryEncodeHelper {

    private final OutputStream out;
    private IOException writeErr;

    private final List<SingleTypeEntry<?>> correspondingEntriesInOrder = new ArrayList<>();

    public static ValErr<BinaryEncodeHelper,Exception> to(File file){
        return ValErr.encapsulate(() -> new BinaryEncodeHelper(new FileOutputStream(file)));
    }
    public BinaryEncodeHelper(OutputStream out){
        this.out = out;
    }

    public BinaryEncodeHelper integer(int i){
        ValErr.encapsulate(() -> out.write(i));
        correspondingEntriesInOrder.add(new SingleTypeEntry<>(4, Integer.class, new Ref<>()));
        return this;
    }
    public BinaryEncodeHelper bytes(byte[] bytes){
        ValErr.encapsulate(() -> out.write(bytes));
        correspondingEntriesInOrder.add(new SingleTypeEntry<>(4, Integer.class, new Ref<>()));
        return this;
    }
    public BinaryEncodeHelper bytes(byte b){
        ValErr.encapsulate(() -> out.write(b));
        return this;
    }



}
