package gbw.bytestreamer.schema.interfaces;

import gbw.bytestreamer.schema.entries.EntryEventManager;
import gbw.bytestreamer.schema.exceptions.EarlyOut;
import gbw.bytestreamer.util.FailingConsumer;

/**
 *
 * @param <T> Type to parse each element as
 * @param <R> Input to the handling function
 */
public interface ByteSchemaEntry<T,R> {
    /**
     * @return The amount of bytes to collect for the next element this entry expects
     */
    int amount();
    /**
     * @return What type to parse the next element gathered as
     */
    Class<?> as();

    /**
     * After the first layer of accumulation and parsing in the ByteSchema, each entry's push is called.
     * The element pushed should, by all account, be of the type requested by the entry, or not called at all. See {@link gbw.bytestreamer.schema.ByteSchema#parseAndPush(ByteSchemaEntry)}
     * @param element - hopefully of the type returned by the latest query of ByteSchema.as().
     * @throws EarlyOut An entry may throw an EarlyOut outside itself as to prematurely halt the execution of the entire schema.
     * @throws Exception Any other exception thrown, will be handled as a "Consumption Error" and if no handler is provided, the schema closes as well.
     */
    void push(Object element) throws EarlyOut;
    boolean isComplete();
    void setOnExec(FailingConsumer<R> func);
    /**
     * The handler the Schema will run if an exception escapes the entries' error handling.
     */
    EntryEventManager getEventManager();

}
