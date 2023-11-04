package gbw.bytestreamer.schema.entries;

import gbw.bytestreamer.util.BufferParser;

import java.util.function.Consumer;
import java.util.function.Function;

public class EntryEventManager {

    private static final Runnable DEFAULT_RUNNABLE = () -> {};
    private static final Function<BufferParser.ParsingException, Boolean> DEFAULT_CONSUMER_PARSING = any -> false;
    private static final Function<Exception,Boolean> DEFAULT_CONSUMER = any -> false;
    private static final Function<Object,Boolean> NOOP_CONSUMER_OBJ = any -> false;
    private Function<BufferParser.ParsingException, Boolean> onBufferParsingError = DEFAULT_CONSUMER_PARSING;
    private Function<Exception,Boolean> onConsumptionError = DEFAULT_CONSUMER;
    private Runnable onCompletion = DEFAULT_RUNNABLE;

    public EntryEventManager() {
    }

    public Function<BufferParser.ParsingException,Boolean> getOnBufferParsingError() {
        return onBufferParsingError;
    }

    /**
     * @param onBufferParsingError a function receiving said error, and returning true if the error was expected and taken care of.
     */
    public void setOnBufferParsingError(Function<BufferParser.ParsingException,Boolean> onBufferParsingError) {
        this.onBufferParsingError = onBufferParsingError;
    }

    public Function<Exception,Boolean> getOnConsumptionError() {
        return onConsumptionError;
    }

    /**
     * @param onConsumptionError A function receiving said error, and returning true if the error was expected and taken care of.
     */
    public void setOnConsumptionError(Function<Exception,Boolean> onConsumptionError) {
        this.onConsumptionError = onConsumptionError;
    }

    public Runnable getOnCompletion() {
        return onCompletion;
    }

    public void setOnCompletion(Runnable onCompletion) {
        this.onCompletion = onCompletion;
    }


}
