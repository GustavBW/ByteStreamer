package gbw.io;

public class EarlyOut extends Throwable {

    public EarlyOut(){}
    public EarlyOut(String message){
        super(message);
    }
}
