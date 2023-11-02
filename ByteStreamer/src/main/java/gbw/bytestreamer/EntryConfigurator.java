package gbw.bytestreamer;

import java.util.function.Consumer;

public class EntryConfigurator<T> {
    private Consumer<Throwable> onExceptionDo = e -> {};
    private boolean hasErrorHandling = false;
    private final ByteSchemaEntry<T> entry;
    private final ByteSchema schema;

    public EntryConfigurator(ByteSchemaEntry<T> entry, ByteSchema schema){
        this.entry = entry;
        this.schema = schema;
    }

    public EntryConfigurator<T> onError(Consumer<Throwable> onExceptionDo){
        this.onExceptionDo = onExceptionDo;
        hasErrorHandling = true;
        return this;
    }

    /**
     * Requires that this definition is placed within a try-catch that catches EarlyOut specifically.
     * @return
     */
    public EntryConfigurator<T> onErrorEarlyOut(){
        hasErrorHandling = true;
        return this;
    }

    public EntryConfigurator<T> on(EntryHandlingEvents event, Runnable func){
        entry.setHandlerOf(event,func);
        return this;
    }

    public ByteSchema exec(FailingConsumer<T> exec){
        if(hasErrorHandling){
            entry.exec().set(t -> {
                try{
                    exec.accept(t);
                }catch (EarlyOut e){
                    onExceptionDo.accept(e);
                }
            });
        }else{
            entry.exec().set(exec);
        }
        return schema;
    }

}
