package gbw.bytestreamer;

import java.util.HashMap;
import java.util.Map;

public class ByteSchemaEntry<T> {

    private final int amount;
    private final Class<T> as;
    private final Ref<FailingConsumer<T>> exec;
    private Map<EntryHandlingEvents, Runnable> eventHandlers;
    public ByteSchemaEntry(int amount, Class<T> as, Ref<FailingConsumer<T>> exec){
        this.amount = amount;
        this.as = as;
        this.exec = exec;
    }
    public int amount(){return amount;}
    public Class<T> as(){return as;}
    public Ref<FailingConsumer<T>> exec(){return exec;}
    public void setHandlerOf(EntryHandlingEvents event, Runnable func){
        if(eventHandlers == null){
            eventHandlers = new HashMap<>();
        }
        eventHandlers.put(event,func);
    }
    public Runnable getHandlerOf(EntryHandlingEvents event){
        if(eventHandlers == null){
            return null;
        }
        return eventHandlers.get(event);
    }
}
