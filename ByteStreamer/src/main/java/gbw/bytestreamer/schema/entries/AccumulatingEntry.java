package gbw.bytestreamer.schema.entries;

import gbw.bytestreamer.schema.EntryType;
import gbw.bytestreamer.schema.exceptions.EarlyOut;
import gbw.bytestreamer.schema.interfaces.IAccumulatingEntry;
import gbw.bytestreamer.util.FailingConsumer;
import gbw.bytestreamer.util.Ref;
import gbw.bytestreamer.util.Size;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AccumulatingEntry<T> extends AbstractSchemaEntry<T, List<T>> implements IAccumulatingEntry<T> {

    private final List<T> accumulated;
    private final Ref<FailingConsumer<List<T>>> exec;
    private final int length;
    private final int byteSize;
    private int elementsPushed = 0;

    public AccumulatingEntry(int length, Class<T> as, Ref<FailingConsumer<List<T>>> exec){
        super(as, EntryType.MULTI);
        this.length = length;
        this.byteSize = Size.of(as);
        this.exec = exec;
        this.accumulated = new ArrayList<>(length);
    }

    @Override
    public void push(T element) {
        accumulated.add(element);
        elementsPushed++;
    }

    @Override
    public int amount() {
        return byteSize;
    }

    @Override
    public List<T> transform(T data) {
        return accumulated;
    }

    @Override
    public Ref<FailingConsumer<List<T>>> exec() {
        return exec;
    }

    @Override
    public boolean isComplete() {
        return elementsPushed >= length;
    }
}
