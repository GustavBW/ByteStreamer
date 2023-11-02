package gbw.bytestreamer.schema;

import gbw.bytestreamer.util.FailingConsumer;

import java.util.function.Consumer;

public class EntryConfigurator<T> {
    private Consumer<EarlyOut> onExceptionDo = e -> {};
    private boolean hasErrorHandling = false;
    private final ByteSchemaEntry<T> entry;
    private final ByteSchema schema;

    public EntryConfigurator(ByteSchemaEntry<T> entry, ByteSchema schema){
        this.entry = entry;
        this.schema = schema;
    }

    public EntryConfigurator<T> onEarlyOut(Consumer<EarlyOut> onExceptionDo){
        this.onExceptionDo = onExceptionDo;
        hasErrorHandling = true;
        return this;
    }

    public EntryConfigurator<T> on(EntryHandlingEvents event, Runnable func){
        entry.setHandlerOf(event,func);
        return this;
    }

    public ByteSchema exec(FailingConsumer<T> exec){
        FailingConsumer<T> finalExec;
        if(entry instanceof IOnEntryHandlingDoFirst){
            finalExec = t -> {
                ((IOnEntryHandlingDoFirst) entry).getOnExecAcceptDoFirst().run();
                exec.accept(t);
            };
        } else {
            finalExec = exec;
        }

        if(hasErrorHandling){
            entry.exec().set(t -> {
                try{
                    finalExec.accept(t);
                }catch (EarlyOut e){
                    onExceptionDo.accept(e);
                }
            });
        }else{
            entry.exec().set(finalExec);
        }
        return schema;
    }

}
