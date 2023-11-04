package gbw.bytestreamer.schema;

public enum TerminationPolicy {
    /*
    A specific set of bytes.
     */
    BYTE_PATTERN,
    /*
    End Of Source - be it a file, stream, whenever close(...) is called on the schema
     */
    EOS,
    /*
    A specific amount of elements to collect
     */
    LENGTH;
}
