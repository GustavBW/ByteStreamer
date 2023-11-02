package gbw.bytestreamer;

public class EarlyOut extends Exception {

    public EarlyOut(){}
    public EarlyOut(String message){
        super(message);
    }
}
