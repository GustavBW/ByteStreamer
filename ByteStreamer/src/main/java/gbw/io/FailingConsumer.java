package gbw.io;

@FunctionalInterface
public interface FailingConsumer<T> {
    void accept(T data) throws EarlyOut;
}
