package gbw.io;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Abstract description of what should happen for some byte(s) from a SchemaHandler <br>
 * Reactive.
 */
public class ByteSchema {

    private record Entry<T>(int amount, Class<T> as, Ref<FailingConsumer<T>> exec){}

    private int programCounter = 0;
    private byte[] currentBuffer;

    private int bufferIndexOfNext = 0;
    private final List<Consumer<EarlyOut>> onEarlyOut = new ArrayList<>(
            List.of(Throwable::printStackTrace)
    );
    private final List<Entry<Object>> entries = new ArrayList<>();

    private ByteSchema(){}

    public static <T> HandlerConfigurator<T> first(int amount, Class<T> as){
            final Ref<FailingConsumer<T>> execRef = new Ref<>();
            ByteSchema schema = new ByteSchema();
            schema.addEntry((Entry<Object>) new Entry<>(amount, as, execRef));
            schema.currentBuffer = new byte[amount];
            return new HandlerConfigurator<T>(execRef, schema);
    }

    public <T> HandlerConfigurator<T> next(int amount, Class<T> as){
        final Ref<FailingConsumer<T>> execRef = new Ref<>();
        this.addEntry((Entry<Object>) new Entry<T>(amount, as, execRef));
        return new HandlerConfigurator<>(execRef, this);
    }

    public void push(byte b){
        Entry<?> current = entries.get(programCounter);
        if (currentBuffer == null){
            currentBuffer = new byte[current.amount()];
            bufferIndexOfNext = 0;
        }
        if (bufferIndexOfNext >= current.amount()){
            execEntry(current);
        }
        addToBuffer(b);

    }

    public ByteSchema onEarlyOutAppend(Consumer<EarlyOut> func){
        onEarlyOut.add(func);
        return this;
    }

    private <T> void execEntry(Entry<T> entry){
        try{
            FailingConsumer<T> func = entry.exec().get();
            T data = abstractParse(entry.as(), currentBuffer);
            func.accept(data);
        }catch(EarlyOut e){
            onEarlyOut.forEach(func -> func.accept(e));
        }

        currentBuffer = null;
    }

    private <T> T abstractParse(Class<T> clazz, byte[] buffer){
        ByteBuffer wrapped = ByteBuffer.wrap(buffer);
        switch (clazz.toString()){
            case "float" -> {
                return (T) (Object) wrapped.getFloat();
            }
            case "int" -> {
                return (T) (Object) wrapped.getInt();
            }
            case "long" -> {
                return (T) (Object) wrapped.getLong();
            }
            default -> {
                System.out.println("Not covered: " + clazz.toString());
            }
        }
        return null;
    }

    private void addToBuffer(byte b){
        currentBuffer[bufferIndexOfNext] = b;
        bufferIndexOfNext++;
    }

    private <T> void addEntry(Entry<Object> entry){
        entries.add(entry);
    }

}
