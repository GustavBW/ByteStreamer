package gbw.bytestreamer.schema.interfaces;

import gbw.bytestreamer.schema.EntryHandlingEvents;
import gbw.bytestreamer.schema.EntryType;
import gbw.bytestreamer.util.FailingConsumer;
import gbw.bytestreamer.util.Ref;

/**
 *
 * @param <T> Type to parse each element as
 * @param <R> Input to the handling function
 */
public interface ByteSchemaEntry<T,R> {
    /**
     * @return The amount of bytes to collect
     */
    int amount();
    /**
     * @return What type to parse those bytes as
     */
    Class<T> as();

    /**
     * For some entires T != R - and so a way of transforming the value must be provided. <br>
     * The default implementation for most is NOOP.
     */
    R transform(T data);
    /**
     * @return
     */
    Ref<FailingConsumer<R>> exec();
    void setHandlerOf(EntryHandlingEvents event, Runnable func);
    Runnable getHandlerOf(EntryHandlingEvents event);
    EntryType getType();

}
