package gbw.bytestreamer;


@FunctionalInterface
public interface FailingConsumer<T> {
    /**
     * A FailingConsumer may, at any time, throw an EarlyOut - this is safe, handled, and expected.
     * In essence, it allows any environment currently executing the consumer to skip it and what it represents.
     */
    void accept(T data) throws EarlyOut;
}
