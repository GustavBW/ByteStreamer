package gbw.bytestreamer.schema.entries;

import gbw.bytestreamer.schema.EntryConfigurator;
import gbw.bytestreamer.schema.EntryType;
import gbw.bytestreamer.schema.interfaces.IMultiEntry;
import gbw.bytestreamer.schema.interfaces.IOnEntryHandlingDoFirst;
import gbw.bytestreamer.util.FailingConsumer;
import gbw.bytestreamer.util.Ref;
import gbw.bytestreamer.util.Size;

public class ManyTypeEntry<T> extends AbstractSchemaEntry<T,T> implements IOnEntryHandlingDoFirst, IMultiEntry {
    private final int length;
    private final Ref<FailingConsumer<T>> exec;
    private final int byteSize;
    private final Ref<Integer> indexOfThis = new Ref<>(0);
    private final Runnable onExecAcceptDoFirst = () -> indexOfThis.set(indexOfThis.get() + 1);

    public ManyTypeEntry(int length, Class<T> as, Ref<FailingConsumer<T>> exec){
        super(as, EntryType.MULTI);
        this.length = length;
        this.byteSize = Size.of(as);
        this.exec = exec;
    }

    /**
     * @return byte size of each element in array - just like SingleTypeEntry
     */
    @Override
    public int amount() {return byteSize;}

    @Override
    public T transform(T data) {
        return data;
    }

    @Override
    public Ref<FailingConsumer<T>> exec() {return exec;}
    @Override
    public boolean isComplete() {
        return indexOfThis.get() >= length;
    }

    /**
     * Used to append the "index++" operation, but when the handling function for the entry is set in the {@link EntryConfigurator}. <br>
     * Might also get useful later.
     */
    @Override
    public Runnable getOnExecAcceptDoFirst(){
        return onExecAcceptDoFirst;
    }




}
