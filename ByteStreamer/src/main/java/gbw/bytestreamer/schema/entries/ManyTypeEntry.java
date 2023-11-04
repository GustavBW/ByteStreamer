package gbw.bytestreamer.schema.entries;

import gbw.bytestreamer.schema.exceptions.EarlyOut;
import gbw.bytestreamer.schema.interfaces.ByteSchemaEntry;
import gbw.bytestreamer.util.FailingConsumer;
import gbw.bytestreamer.util.Ref;
import gbw.bytestreamer.util.Size;

public class ManyTypeEntry<T> implements ByteSchemaEntry<T,T> {
    private final int length;
    private FailingConsumer<T> forEachElementDo;
    private final int byteSize;
    private final Class<T> as;
    private final Ref<Integer> indexOfThis = new Ref<>(0);
    private final EntryEventManager eventManager = new EntryEventManager();

    public ManyTypeEntry(int length, Class<T> as){
        this.as = as;
        this.length = length;
        this.byteSize = Size.of(as);
    }

    /**
     * @return byte size of each element in array - just like SingleTypeEntry
     */
    @Override
    public int amount() {return byteSize;}

    @Override
    public Class<?> as() {return as;}

    @Override
    public void push(Object element) throws EarlyOut {
        T casted = (T) element;
        forEachElementDo.accept(casted);
    }
    @Override
    public void setOnExec(FailingConsumer<T> func){
        this.forEachElementDo = func;
    }
    @Override
    public boolean isComplete() {
        return indexOfThis.get() >= length;
    }
    @Override
    public EntryEventManager getEventManager() {
        return eventManager;
    }
}
