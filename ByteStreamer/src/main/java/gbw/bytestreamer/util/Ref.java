package gbw.bytestreamer.util;

public class Ref<T> implements IRef<T> {
    private T data;
    public Ref(){}
    public Ref(T data){
        this.data = data;
    }
    public void set(T data){
        this.data = data;
    }
    public T get(){
        return data;
    }
}
