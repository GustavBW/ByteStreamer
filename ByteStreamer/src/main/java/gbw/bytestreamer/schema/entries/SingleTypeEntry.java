package gbw.bytestreamer.schema.entries;

import gbw.bytestreamer.schema.EntryType;
import gbw.bytestreamer.schema.entries.AbstractSchemaEntry;
import gbw.bytestreamer.util.FailingConsumer;
import gbw.bytestreamer.util.Ref;

public class SingleTypeEntry<T> extends AbstractSchemaEntry<T,T> {

    private final int amount;
    private final Ref<FailingConsumer<T>> exec;
    public SingleTypeEntry(int amount, Class<T> as, Ref<FailingConsumer<T>> exec){
        super(as, EntryType.SINGLE);
        this.amount = amount;
        this.exec = exec;
    }
    @Override
    public T transform(T data){
        return data;
    }
    @Override
    public int amount(){return amount;}
    @Override
    public Ref<FailingConsumer<T>> exec(){return exec;}

}
