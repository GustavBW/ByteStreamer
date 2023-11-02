package gbw.bytestreamer.util;

public interface IRef<T> {
    void set(T o);
    T get();
}
