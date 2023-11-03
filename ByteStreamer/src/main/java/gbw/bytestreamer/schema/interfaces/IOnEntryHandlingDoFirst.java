package gbw.bytestreamer.schema.interfaces;

import java.util.function.Consumer;

/**
 * When the handling function for a ByteSchemaEntry is set through the EntryConfigurator. <br>
 * Any ByteSchemaEntry implementation may implement this interface to "append" a function to be run first whenever the handling function is.
 */
@FunctionalInterface
public interface IOnEntryHandlingDoFirst {

    Runnable getOnExecAcceptDoFirst();
}
