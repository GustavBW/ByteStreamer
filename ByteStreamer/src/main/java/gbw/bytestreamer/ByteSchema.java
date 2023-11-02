package gbw.bytestreamer;

import java.util.*;
import java.util.function.Consumer;

/**
 * Abstract description of what should happen for some byte(s) from a SchemaHandler <br>
 * Reactive.
 */
public class ByteSchema {

    private int programCounter = 0;
    private byte[] currentBuffer;

    private int bufferIndexOfNext = 0;
    private final List<Consumer<EarlyOut>> onEarlyOut = new ArrayList<>(
            List.of(Throwable::printStackTrace)
    );
    private final List<Consumer<Byte>> onPushAny = new ArrayList<>();
    private final List<ByteSchemaEntry<Object>> entries = new ArrayList<>();

    private ByteSchema(){}

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

    public ByteSchema skip(int amount){
        next(amount, NoParse.class)
                .exec(NoParse.NOOP);
        return this;
    }

    public <T> EntryConfigurator<T> next(int amount, Class<T> as){
        final Ref<FailingConsumer<T>> execRef = new Ref<>();
        ByteSchemaEntry<T> entry = new ByteSchemaEntry<T>(amount, as, execRef);
        this.addEntry((ByteSchemaEntry<Object>) entry);
        return new EntryConfigurator<>(entry, this);
    }
    public ByteSchema appendOnPush(Consumer<Byte> func){
        onPushAny.add(func);
        return this;
    }

    /**
     * @return True when complete. Pushing anymore bytes to the schema will cause issues after this point.
     */
    public boolean push(byte b){
        onPushAny.forEach(cons -> cons.accept(b));
        if(programCounter >= entries.size()){
            return true;
        }
        ByteSchemaEntry<?> current = entries.get(programCounter);
        if (currentBuffer == null){
            currentBuffer = new byte[current.amount()];
            bufferIndexOfNext = 0;
        }
        addToBuffer(b);
        if (bufferIndexOfNext >= current.amount()){
            execEntry(current);
            programCounter++;
        }
        return false;
    }

    public ByteSchema onEarlyOutAppend(Consumer<EarlyOut> func){
        onEarlyOut.add(func);
        return this;
    }

    private <T> void execEntry(ByteSchemaEntry<T> entry){
        try{
            FailingConsumer<T> func = entry.exec().get();
            T data = UnsafeByteParser.parse(entry.as(), currentBuffer);
            func.accept(data);
        }catch(EarlyOut e){
            onEarlyOut.forEach(func -> func.accept(e));
        }catch(UnsafeByteParser.UncoveredTypeException e){
            Runnable eventHandler = entry.getHandlerOf(EntryHandlingEvents.BUFFER_PARSING_ERROR);
            if(eventHandler != null){
                e.printStackTrace();
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
