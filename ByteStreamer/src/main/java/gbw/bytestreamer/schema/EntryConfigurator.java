package gbw.bytestreamer.schema;

import gbw.bytestreamer.util.FailingConsumer;

import java.util.function.Consumer;

public class EntryConfigurator<T> {
    private Consumer<Exception> onExceptionDo = e -> {};
    private boolean hasErrorHandling = false;
    private final ByteSchemaEntry<T> entry;
    private final ByteSchema schema;

    public EntryConfigurator(ByteSchemaEntry<T> entry, ByteSchema schema){
        this.entry = entry;
        this.schema = schema;
    }

    public EntryConfigurator<T> onError(Consumer<Exception> onExceptionDo){
        this.onExceptionDo = onExceptionDo;
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
