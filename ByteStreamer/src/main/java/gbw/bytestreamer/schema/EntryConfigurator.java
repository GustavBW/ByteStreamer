package gbw.bytestreamer.schema;

import gbw.bytestreamer.schema.exceptions.EarlyOut;
import gbw.bytestreamer.schema.interfaces.ByteSchemaEntry;
import gbw.bytestreamer.util.BufferParser;
import gbw.bytestreamer.util.FailingConsumer;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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

    public ByteSchema exec(FailingConsumer<R> exec){
        FailingConsumer<R> possiblyRedirected = checkRedirects(exec);
        FailingConsumer<R> possiblyPrepended = prependFunctions(possiblyRedirected);
        FailingConsumer<R> maybeWithErrorHandling = appendErrorHandling(possiblyPrepended);

        entry.setOnExec(maybeWithErrorHandling);
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
        return execFunc;
    }

    private FailingConsumer<R> checkRedirects(FailingConsumer<R> execFunc) {
        return execFunc;
    }
    /**
     * @param onConsumptionError A function receiving said error, and returning true if the error was expected and taken care of.
     */
    public EntryConfigurator<T,R> onConsumptionError(Function<Exception,Boolean> onConsumptionError){
        entry.getEventManager().setOnConsumptionError(onConsumptionError);
        return this;
    }
    /**
     * @param onBufferParsingError a function receiving said error, and returning true if the error was expected and taken care of.
     */
    public EntryConfigurator<T,R> setOnBufferParsingError(Function<BufferParser.ParsingException,Boolean> onBufferParsingError){
        entry.getEventManager().setOnBufferParsingError(onBufferParsingError);
        return this;
    }
    public EntryConfigurator<T,R> setOnCompletion(Runnable onCompletion){
        entry.getEventManager().setOnCompletion(onCompletion);
        return this;
    }


}
