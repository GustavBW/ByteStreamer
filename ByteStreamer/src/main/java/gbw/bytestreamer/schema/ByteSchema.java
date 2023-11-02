package gbw.bytestreamer.schema;

import gbw.bytestreamer.util.*;

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
    private final List<ByteSchemaEntry<Object>> entries = new ArrayList<>();
    private boolean isComplete = false;
    private Runnable onSchemaComplete = () -> {};
    private Consumer<Exception> onSchemaIncomplete = Exception::printStackTrace;

    private ByteSchema(){}

    /**
     * Initializes the ByteSchema and prepares the first expected element/entry
     * @param amount how many bytes to read
     * @param as what to parse those bytes as
     * @return an EntryConfigurator which allows for configuration of this specific {@link ByteSchemaEntry}. <br>
     * This in turn returns the schema when the handling function is set {@link EntryConfigurator#exec}
     */
    public static <T> EntryConfigurator<T> first(int amount, Class<T> as){
        final Ref<FailingConsumer<T>> execRef = new Ref<>();
        ByteSchema schema = new ByteSchema();
        ByteSchemaEntry<T> entry = new ByteSchemaEntry<>(amount, as, execRef);
        schema.addEntry((ByteSchemaEntry<Object>) entry);
        schema.currentBuffer = new byte[amount];
        return new EntryConfigurator<T>(entry, schema);
    }

    /**
     * Requires the class to be representing a primitive class. <br>
     * I.e. float, double, int, short... etc.
     */
    public <T> EntryConfigurator<T> next(Class<T> as){
        int sizeOfAs = Size.of(as);
        if(sizeOfAs == -1){
            throw new RuntimeException("Class provided is not known and cannot be found.");
        }
        return next(sizeOfAs, as);
    }

    /**
     * Ignores the next N amount of bytes.
     */
    public ByteSchema skip(int amount){
        next(amount, NoParse.class)
                .exec(NoParse.NOOP);
        return this;
    }

    /**
     * Same as {@link ByteSchema#first} - however, it does not initialize a new schema
     */
    public <T> EntryConfigurator<T> next(int amount, Class<T> as){
        final Ref<FailingConsumer<T>> execRef = new Ref<>();
        ByteSchemaEntry<T> entry = new ByteSchemaEntry<T>(amount, as, execRef);
        this.addEntry((ByteSchemaEntry<Object>) entry);
        return new EntryConfigurator<>(entry, this);
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
        ByteSchemaEntry<?> current = entries.get(programCounter);
        if (currentBuffer == null){
            currentBuffer = new byte[current.amount()];
            bufferIndexOfNext = 0;
        }
        addToBuffer(b);
        if (bufferIndexOfNext >= current.amount()){
            execEntry(current);
            programCounter++;
            isComplete = programCounter >= entries.size();
            if(isComplete){
                onSchemaComplete.run();
            }
        }
        return isComplete;
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

    private <T> void execEntry(ByteSchemaEntry<T> entry){
        try{
            FailingConsumer<T> func = entry.exec().get();
            T data = UnsafeBufferParser.parse(entry.as(), currentBuffer);
            func.accept(data);
        }catch(EarlyOut e){
            onEarlyOut.forEach(func -> func.accept(e));
        }catch(UnsafeBufferParser.UncoveredTypeException e){
            Runnable eventHandler = entry.getHandlerOf(EntryHandlingEvents.BUFFER_PARSING_ERROR);
            if(eventHandler != null){
                e.printStackTrace();
                eventHandler.run();
            }
        }catch(Exception e){
            //We assume it's a consumption error.
            Runnable eventHandler = entry.getHandlerOf(EntryHandlingEvents.CONSUMPTION_ERROR);
            if(eventHandler != null){
                eventHandler.run();
            }
        }
        currentBuffer = null;
    }

    private void addToBuffer(byte b){
        currentBuffer[bufferIndexOfNext] = b;
        bufferIndexOfNext++;
    }

    private <T> void addEntry(ByteSchemaEntry<Object> entry){
        entries.add(entry);
    }


}
