package gbw.io;

import java.util.function.Consumer;

public class HandlerConfigurator<T> {
    private Consumer<Throwable> onExceptionDo = e -> {};
    private boolean hasErrorHandling = false;
    private final Ref<FailingConsumer<T>> execRef;
    private final ByteSchema schema;

    public HandlerConfigurator(Ref<FailingConsumer<T>> execRef, ByteSchema schema){
        this.execRef = execRef;
        this.schema = schema;
    }

    public HandlerConfigurator<T> onError(Consumer<Throwable> onExceptionDo){
        this.onExceptionDo = onExceptionDo;
        hasErrorHandling = true;
        return this;
    }

    /**
     * Requires that this definition is placed within a try-catch that catches EarlyOut specifically.
     * @return
     */
    public HandlerConfigurator<T> onErrorEarlyOut(){

        return this;
    }

    public ByteSchema exec(FailingConsumer<T> exec){
        if(hasErrorHandling){
            execRef.set(t -> {
                try{
                    exec.accept(t);
                }catch (Throwable e){
                    onExceptionDo.accept(e);
                }
            });
        }else{
            execRef.set(exec);
        }
        return schema;
    }

}
