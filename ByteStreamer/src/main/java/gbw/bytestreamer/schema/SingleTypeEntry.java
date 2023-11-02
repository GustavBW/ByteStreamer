package gbw.bytestreamer.schema;

import gbw.bytestreamer.util.FailingConsumer;
import gbw.bytestreamer.util.Ref;

import java.util.HashMap;
import java.util.Map;

public class SingleTypeEntry<T> implements ByteSchemaEntry<T> {

    private final int amount;
    private final Class<T> as;
    private final Ref<FailingConsumer<T>> exec;
    private Map<EntryHandlingEvents, Runnable> eventHandlers;
    public SingleTypeEntry(int amount, Class<T> as, Ref<FailingConsumer<T>> exec){
        this.amount = amount;
        this.as = as;
        this.exec = exec;
    }
    @Override
    public int amount(){return amount;}
    @Override
    public Class<T> as(){return as;}
    @Override
    public Ref<FailingConsumer<T>> exec(){return exec;}
    @Override
    public EntryType getType(){return EntryType.SINGLE;}
    @Override
    public void setHandlerOf(EntryHandlingEvents event, Runnable func){
        if(eventHandlers == null){
            eventHandlers = new HashMap<>();
        }
        eventHandlers.put(event,func);
    }
    @Override
    public Runnable getHandlerOf(EntryHandlingEvents event){
        if(eventHandlers == null){
            return null;
        }
        return eventHandlers.get(event);
    }
}
