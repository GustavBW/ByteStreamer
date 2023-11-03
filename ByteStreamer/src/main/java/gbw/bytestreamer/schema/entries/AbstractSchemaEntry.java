package gbw.bytestreamer.schema.entries;

import gbw.bytestreamer.schema.EntryHandlingEvents;
import gbw.bytestreamer.schema.EntryType;
import gbw.bytestreamer.schema.interfaces.ByteSchemaEntry;
import gbw.bytestreamer.util.FailingConsumer;
import gbw.bytestreamer.util.Ref;

import java.util.HashMap;
import java.util.Map;

//Certified OOP moment
public abstract class AbstractSchemaEntry<T,R> implements ByteSchemaEntry<T,R> {

    private final Class<T> as;
    private final EntryType type;
    private Map<EntryHandlingEvents, Runnable> eventHandlers;

    protected AbstractSchemaEntry(Class<T> as, EntryType type){
        this.as = as;
        this.type = type;
    }

    @Override
    public abstract Ref<FailingConsumer<R>> exec();
    @Override
    public abstract int amount();
    @Override
    public abstract R transform(T data);

    @Override
    public Class<T> as() {
        return as;
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
    @Override
    public EntryType getType() {
        return type;
    }
}
