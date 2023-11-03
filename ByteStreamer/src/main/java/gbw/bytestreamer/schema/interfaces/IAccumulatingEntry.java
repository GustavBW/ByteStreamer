package gbw.bytestreamer.schema.interfaces;

import gbw.bytestreamer.schema.exceptions.EarlyOut;
import gbw.bytestreamer.util.FailingConsumer;

import java.util.Collection;

public interface IAccumulatingEntry<T> extends IMultiEntry {
    /**
     * If this completes the entry, the entry will execute the handling function - thus this may throw an EarlyOut
     * @param element
     */
    void push(T element);
}
