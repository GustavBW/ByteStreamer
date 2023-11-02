package gbw.bytestreamer.schema;

import gbw.bytestreamer.util.FailingConsumer;
import gbw.bytestreamer.util.Ref;
import gbw.bytestreamer.util.Size;

import java.util.HashMap;
import java.util.Map;

public class ArrayTypeEntry<T> implements ByteSchemaEntry<T>, IOnEntryHandlingDoFirst, IMultiEntry {
    private final int length;
    private final Class<T> as;
    private final Ref<FailingConsumer<T>> exec;
    private final int byteSize;
    private final Ref<Integer> indexOfThis = new Ref<>(0);
    private final Runnable onExecAcceptDoFirst = () -> indexOfThis.set(indexOfThis.get() + 1);
    private Map<EntryHandlingEvents, Runnable> eventHandlers;
    public ArrayTypeEntry(int length, Class<T> as, Ref<FailingConsumer<T>> exec){
        this.length = length;
        this.as = as;
        this.byteSize = Size.of(as);
        this.exec = exec;
    }

    /**
     * @return byte size of each element in array - just like SingleTypeEntry
     */
    @Override
    public int amount() {return byteSize;}
    /**
     * @return length of array
     */
    public int length(){return length;}
    @Override
    public Class<T> as() {return as;}
    @Override
    public Ref<FailingConsumer<T>> exec() {return exec;}
    @Override
    public boolean isComplete() {
        return indexOfThis.get() >= length;
    }
    @Override
    public EntryType getType() {return EntryType.MULTI;}


    /**
     * Used to append the "index++" operation, but when the handling function for the entry is set in the {@link EntryConfigurator}. <br>
     * Might also get useful later.
     */
    @Override
    public Runnable getOnExecAcceptDoFirst(){
        return onExecAcceptDoFirst;
    }

    @Override
    public void setHandlerOf(EntryHandlingEvents event, Runnable func) {
        if(eventHandlers == null){
            eventHandlers = new HashMap<>();
        }
        eventHandlers.put(event,func);
    }

    @Override
    public Runnable getHandlerOf(EntryHandlingEvents event) {
        if(eventHandlers == null){
            return null;
        }
        return eventHandlers.get(event);
    }


}
