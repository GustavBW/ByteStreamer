package gbw.bytestreamer.schema;

import gbw.bytestreamer.schema.entries.AccumulatingEntry;
import gbw.bytestreamer.schema.entries.ManyTypeEntry;
import gbw.bytestreamer.schema.entries.SingleTypeEntry;
import gbw.bytestreamer.schema.exceptions.EarlyOut;
import gbw.bytestreamer.schema.exceptions.SchemaException;
import gbw.bytestreamer.schema.interfaces.ByteSchemaEntry;
import gbw.bytestreamer.schema.interfaces.IAccumulatingEntry;
import gbw.bytestreamer.schema.interfaces.IMultiEntry;
import gbw.bytestreamer.util.*;
import jdk.jshell.spi.ExecutionControl;

import java.util.*;
import java.util.function.Consumer;

/**
 * Abstract description of what should happen for some byte(s) from a SchemaHandler <br>
 * Usecase example:
 * <pre>
 * {@code
 *     ByteSchema schema = ByteSchema
 *          .first(4, Integer.class)
 *          .exec(i -> System.out.println("We got an int: " + i));
 * }
 * </pre>
 */
public class ByteSchema implements AutoCloseable{

    private int programCounter = 0;
    private byte[] currentBuffer;
    private int bufferIndexOfNext = 0;
    private final List<Consumer<EarlyOut>> onEarlyOut = new ArrayList<>(
            List.of(Throwable::printStackTrace)
    );
    private final List<Consumer<Byte>> onPushAny = new ArrayList<>();
    private Consumer<Byte> onSchemaOverflow = b -> {};
    private final List<ByteSchemaEntry<?,?>> entries = new ArrayList<>();
    private boolean isComplete = false, isPopulated = false;
    private Runnable onSchemaComplete = () -> {};
    private Consumer<Exception> onSchemaIncomplete = Exception::printStackTrace;

    private ByteSchema(){}

    /**
     * Initializes the ByteSchema and prepares the first expected element/entry
     * @param amount how many bytes to read
     * @param as what to parse those bytes as
     * @return an EntryConfigurator which allows for configuration of this specific {@link SingleTypeEntry}. <br>
     * This in turn returns the schema when the handling function is set {@link EntryConfigurator#exec}
     */
    public static <T> EntryConfigurator<T,T> first(int amount, Class<T> as){
        final Ref<FailingConsumer<T>> execRef = new Ref<>();
        ByteSchema schema = new ByteSchema();
        SingleTypeEntry<T> entry = new SingleTypeEntry<>(amount, as, execRef);
        schema.addEntry(entry);
        init(entry,schema);
        return new EntryConfigurator<>(entry, schema);
    }

    private static <T,R> void init(ByteSchemaEntry<T,R> firstEntry, ByteSchema schema){
        schema.currentBuffer = new byte[firstEntry.amount()];
        schema.isPopulated = true;
    }

    public static ByteSchema create(){
        return new ByteSchema();
    }

    /**
     * Requires the class to be representing a primitive class. <br>
     * I.e. float, double, int, short... etc.
     */
    public <T> EntryConfigurator<T,T> next(Class<T> as){
        int sizeOfAs = Size.of(as);
        if(sizeOfAs == -1){
            throw new SchemaException("Class provided is not known and cannot be found.");
        }
        return next(sizeOfAs, as);
    }

    /**
     * Ignores the next N amount of bytes.
     */
    public ByteSchema skip(int amount){
        return next(amount, NoParse.class).exec(NoParse.NOOP);
    }

    /**
     * Same as {@link ByteSchema#first} - however, it does not initialize a new schema
     */
    public <T> EntryConfigurator<T,T> next(int amount, Class<T> as){
        final Ref<FailingConsumer<T>> execRef = new Ref<>();
        SingleTypeEntry<T> entry = new SingleTypeEntry<>(amount, as, execRef);
        this.addEntry(entry);
        if(!this.isPopulated) {
            init(entry, this);
        }
        return new EntryConfigurator<>(entry, this);
    }

    /**
     * Behaves just like {@link ByteSchema#next}, however the provided handling function is executed FOR EACH ELEMENT individually <br>
     * <pre>
     *     {@code
     *     ByteSchema schema = ByteSchema
     *      {...}
     *      .next(4, Integer.class) //<- Entry specification
     *      .exec(int -> {..})      //<- Handling function
     *      {...}
     *     }
     * </pre>
     * @param length of array
     * @param as type of each element in array - must be known by Size.of(...)
     */
    public <T> EntryConfigurator<T,T> many(int length, Class<T> as){
        if(Size.of(as) == -1){
            throw new SchemaException("Only arrays of types known to gbw.bytestreamer.util.Size are valid - mostly arrays of primitive types that is.");
        }
        final Ref<FailingConsumer<T>> execRef = new Ref<>();
        ManyTypeEntry<T> entry = new ManyTypeEntry<>(length, as, execRef);
        this.addEntry(entry);
        if(!this.isPopulated) {
            init(entry, this);
        }
        return new EntryConfigurator<>(entry, this);
    }

    public <T> EntryConfigurator<T,T> string(byte terminator, Encodings encoding) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("");
    }

    /**
     * Collect a list of type T until terminatorPattern is reached, then handle that list.
     * @param terminatorPattern any serie of byte with the same length as the type of the elements
     * @param as - type of element
     * @param <T>
     * @param <R>
     * @throws ExecutionControl.NotImplementedException so far it does
     */
    public <T,R> EntryConfigurator<T,R> list(byte[] terminatorPattern, Class<T> as) throws ExecutionControl.NotImplementedException {
        assert Size.of(as) == terminatorPattern.length;
        throw new ExecutionControl.NotImplementedException("");
    }

    public <T> EntryConfigurator<T,List<T>> list(int length, Class<T> as){
        final Ref<FailingConsumer<List<T>>> execRef = new Ref<>();
        AccumulatingEntry<T> entry = new AccumulatingEntry<>(length, as, execRef);
        this.addEntry(entry);
        if(!this.isPopulated) {
            init(entry, this);
        }
        return new EntryConfigurator<>(entry, this);
    }
    public <T> EntryConfigurator<T,T> until(byte[] terminatorPattern, Class<T> as) throws ExecutionControl.NotImplementedException {

        throw new ExecutionControl.NotImplementedException("");
    }

    /**
     * @return True when complete. Pushing anymore bytes to the schema will be ignored.
     */
    public boolean push(byte b){
        isComplete = programCounter >= entries.size();
        if(isComplete) {
            onSchemaOverflow.accept(b);
            return true;
        }
        onPushAny.forEach(cons -> cons.accept(b));
        ByteSchemaEntry<?,?> current = entries.get(programCounter);
        if (currentBuffer == null){
            currentBuffer = new byte[current.amount()];
            bufferIndexOfNext = 0;
        }
        addToBuffer(b);
        //If there's the required amount of data in the buffer
        if (bufferIndexOfNext >= current.amount()){
            //If repeat == true, don't proceed. (In the case of Array types, the same entry may have to be executed many times).
            if(!handleEntry(current)){
                Runnable postProcess = current.getHandlerOf(EntryHandlingEvents.POST_ENTRY_COMPLETION);
                if(postProcess != null){
                    postProcess.run();
                }
                programCounter++;
                isComplete = programCounter >= entries.size();
                if(isComplete){
                    onSchemaComplete.run();
                }
            }
        }
        return isComplete;
    }
    /**
     * @return true if the current entry should be repeated.
     */
    private <T,R> boolean handleEntry(ByteSchemaEntry<T,R> entry){
        boolean repeat = false;
        try{
            T data = UnsafeBufferParser.parse(entry.as(), currentBuffer);
            //Not at all happy about placing this here. In a perfect world, .exec() func should be redirected.
            //However, my attempts failed.
            if(entry instanceof IAccumulatingEntry && !((IAccumulatingEntry<?>) entry).isComplete()){
                ((IAccumulatingEntry<T>) entry).push(data);
                if(((IAccumulatingEntry<?>) entry).isComplete()){
                    transformAndExec(entry, data);
                }
            }else{
                transformAndExec(entry, data);
            }
        }catch(EarlyOut e){
            //If the early out wasn't caught by the handler itself (as facilitated by EntryConfigurator)
            //We assume it's probably a systematic error not pertaining to the entry itself.
            onEarlyOut.forEach(func -> func.accept(e));
            //And close the schema to avoid further issues.
            close(e);
        }catch(UnsafeBufferParser.UncoveredTypeException e){
            Runnable eventHandler = entry.getHandlerOf(EntryHandlingEvents.BUFFER_PARSING_ERROR);
            if(eventHandler != null){
                eventHandler.run();
            }else{
                //If the error wasn't expected - i.e. an event handler wasn't provided
                close(e);
            }
        }catch(Exception e){
            //We assume it's a consumption error.
            Runnable eventHandler = entry.getHandlerOf(EntryHandlingEvents.CONSUMPTION_ERROR);
            if(eventHandler != null){
                eventHandler.run();
            }else{
                //If the error wasn't expected - i.e. an event handler wasn't provided
                close(e);
            }
        }
        //i.e. ManyTypeEntry is executed for the length of the array, so we need to ask it if it's done.
        if(entry.getType() == EntryType.MULTI){
            repeat = !((IMultiEntry) entry).isComplete();
        }
        //regardless, clear the buffer.
        currentBuffer = null;
        return repeat;
    }

    private static <T, R> void transformAndExec(ByteSchemaEntry<T, R> entry, T data) throws EarlyOut {
        FailingConsumer<R> func = entry.exec().get();
        R transformed = entry.transform(data);
        func.accept(transformed);
    }

    /**
     * Append any function to be run when a byte is pushed to the schema AND the schema are not complete.
     */
    public ByteSchema appendOnPush(Consumer<Byte> func){
        Objects.requireNonNull(func);
        onPushAny.add(func);
        return this;
    }

    /**
     * When the ByteSchema's own early out catch is triggered, any functions appended is run.
     */
    public ByteSchema onEarlyOutAppend(Consumer<EarlyOut> func){
        Objects.requireNonNull(func);
        onEarlyOut.add(func);
        return this;
    }

    /**
     * Triggered once when the last entry of the schema has been handled.
     */
    public ByteSchema onSchemaComplete(Runnable func){
        Objects.requireNonNull(func);
        onSchemaComplete = func;
        return this;
    }

    /**
     * Triggered when the schema is closed (either by the input ending, an uncaught exception occurring or other)
     * but it expected more bytes. The reason is conveyed in the exception.
     * Default: Exception::printStackTrace
     */
    public ByteSchema onSchemaIncomplete(Consumer<Exception> func){
        Objects.requireNonNull(func);
        onSchemaIncomplete = func;
        return this;
    }

    /**
     * Triggered whenever a byte is pushed but the schema has already completed. <br>
     * Default: NOOP
     */
    public ByteSchema onSchemaOverflow(Consumer<Byte> func){
        Objects.requireNonNull(func);
        onSchemaOverflow = func;
        return this;
    }

    /**
     * Sets "isComplete" to true thereby ígnoring any following bytes.
     * If isComplete wasn't true already, onSchemaIncomplete is triggered.
     */
    @Override
    public void close() {
        if(!isComplete) onSchemaIncomplete.accept(new Exception("The Schema has been closed before its completion."));
        isComplete = true;
    }
    public void close(Exception why){
        if (!isComplete) onSchemaIncomplete.accept(why);
    }

    private void addToBuffer(byte b){
        currentBuffer[bufferIndexOfNext] = b;
        bufferIndexOfNext++;
    }

    private <T,R> void addEntry(ByteSchemaEntry<T,R> entry){
        entries.add(entry);
    }


}
