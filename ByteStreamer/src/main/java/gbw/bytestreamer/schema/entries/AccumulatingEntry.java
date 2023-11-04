package gbw.bytestreamer.schema.entries;

import gbw.bytestreamer.schema.TerminationPolicy;
import gbw.bytestreamer.schema.exceptions.EarlyOut;
import gbw.bytestreamer.schema.interfaces.ByteSchemaEntry;
import gbw.bytestreamer.util.FailingConsumer;
import gbw.bytestreamer.util.Ref;
import gbw.bytestreamer.util.Size;

import java.util.ArrayList;
import java.util.List;

public class AccumulatingEntry<T> implements ByteSchemaEntry<T, List<T>> {

    private final List<T> accumulated;
    private FailingConsumer<List<T>> forListDo;
    private final int length;
    private final int byteSize;
    private final Class<T> as;
    private int elementsPushed = 0;
    private final TerminationPolicy policy;
    private final EntryEventManager eventManager = new EntryEventManager();

    public AccumulatingEntry(int length, Class<T> as, TerminationPolicy policy){
        this.length = length;
        this.byteSize = Size.of(as);
        this.as = as;
        this.policy = policy;
        this.accumulated = new ArrayList<>(length);
    }

    @Override
    public void push(Object element) throws EarlyOut {
        accumulated.add((T) element);
        elementsPushed++;
        if(elementsPushed >= length){
            forListDo.accept(accumulated);
        }
    }

    @Override
    public int amount() {
        return byteSize;
    }

    @Override
    public Class<T> as(){
        return as;
    }

    @Override
    public boolean isComplete() {
        return elementsPushed >= length;
    }

    @Override
    public void setOnExec(FailingConsumer<List<T>> func) {
        this.forListDo = func;
    }

    @Override
    public EntryEventManager getEventManager() {
        return null;
    }
}
