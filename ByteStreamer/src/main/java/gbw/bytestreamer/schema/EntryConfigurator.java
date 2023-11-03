package gbw.bytestreamer.schema;

import gbw.bytestreamer.schema.exceptions.EarlyOut;
import gbw.bytestreamer.schema.interfaces.ByteSchemaEntry;
import gbw.bytestreamer.schema.interfaces.IAccumulatingEntry;
import gbw.bytestreamer.schema.interfaces.IOnEntryHandlingDoFirst;
import gbw.bytestreamer.util.FailingConsumer;

import java.util.List;
import java.util.function.Consumer;

public class EntryConfigurator<T,R> {
    private Consumer<EarlyOut> onExceptionDo = e -> {};
    private boolean hasErrorHandling = false;
    private final ByteSchemaEntry<T,R> entry;
    private final ByteSchema schema;

    public EntryConfigurator(ByteSchemaEntry<T,R> entry, ByteSchema schema){
        this.entry = entry;
        this.schema = schema;
    }

    public EntryConfigurator<T,R> onEarlyOut(Consumer<EarlyOut> onExceptionDo){
        this.onExceptionDo = onExceptionDo;
        hasErrorHandling = true;
        return this;
    }

    public EntryConfigurator<T,R> on(EntryHandlingEvents event, Runnable func){
        entry.setHandlerOf(event,func);
        return this;
    }

    public ByteSchema exec(FailingConsumer<R> exec){
        FailingConsumer<R> possiblyRedirected = checkRedirects(exec);
        FailingConsumer<R> possiblyPrepended = prependFunctions(possiblyRedirected);
        FailingConsumer<R> maybeWithErrorHandling = appendErrorHandling(possiblyPrepended);

        entry.exec().set(maybeWithErrorHandling);
        return schema;
    }
    private FailingConsumer<R> appendErrorHandling(FailingConsumer<R> execFunc){
        if(hasErrorHandling){
            return t -> {
                try{
                    execFunc.accept(t);
                }catch (EarlyOut e){
                    onExceptionDo.accept(e);
                }
            };
        }
        return execFunc;
    }

    private FailingConsumer<R> prependFunctions(FailingConsumer<R> execFunc){
        if(entry instanceof IOnEntryHandlingDoFirst){
            return t -> {
                ((IOnEntryHandlingDoFirst) entry).getOnExecAcceptDoFirst().run();
                execFunc.accept(t);
            };
        }
        return execFunc;
    }

    private FailingConsumer<R> checkRedirects(FailingConsumer<R> execFunc) {
        return execFunc;
    }

}
