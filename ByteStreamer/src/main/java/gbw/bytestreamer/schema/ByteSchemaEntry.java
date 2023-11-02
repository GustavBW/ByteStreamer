package gbw.bytestreamer.schema;

import gbw.bytestreamer.util.FailingConsumer;
import gbw.bytestreamer.util.Ref;

public interface ByteSchemaEntry<T> {
    int amount();
    Class<T> as();
    Ref<FailingConsumer<T>> exec();
    void setHandlerOf(EntryHandlingEvents event, Runnable func);
    Runnable getHandlerOf(EntryHandlingEvents event);
    EntryType getType();

}
