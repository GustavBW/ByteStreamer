package gbw.bytestreamer.util;

public class NoParse extends Number {
    public static final FailingConsumer<NoParse> NOOP = e -> {};

    @Override
    public int intValue() {
        return -1;
    }

    @Override
    public long longValue() {
        return -1L;
    }

    @Override
    public float floatValue() {
        return -1f;
    }

    @Override
    public double doubleValue() {
        return -1d;
    }
}
