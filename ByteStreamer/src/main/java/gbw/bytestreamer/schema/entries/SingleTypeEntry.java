package gbw.bytestreamer.schema.entries;

import gbw.bytestreamer.schema.EntryType;
import gbw.bytestreamer.schema.exceptions.EarlyOut;
import gbw.bytestreamer.schema.interfaces.ByteSchemaEntry;
import gbw.bytestreamer.util.FailingConsumer;
import gbw.bytestreamer.util.Ref;

public class SingleTypeEntry<T> implements ByteSchemaEntry<T, T> {
    private final Class<T> as;
    private final int amount;
    private FailingConsumer<T> onExec;
    private boolean complete = false;
    private final EntryEventManager eventManager = new EntryEventManager();
    public SingleTypeEntry(int amount, Class<T> as){
        this.amount = amount;
        this.as = as;
    }
    @Override
    public int amount(){return amount;}

    @Override
    public void push(Object element) throws EarlyOut {
       complete = true;
       onExec.accept((T) element);
    }
    @Override
    public void setOnExec(FailingConsumer<T> func){
        this.onExec = func;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public EntryEventManager getEventManager() {
        return eventManager;
    }

    @Override
    public Class<T> as() {
        return as;
    }

}
