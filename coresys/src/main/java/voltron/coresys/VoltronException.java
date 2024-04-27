package voltron.coresys;

public class VoltronException extends Exception {

    public VoltronException(Throwable cause) {
        super(cause);
    }

    public VoltronException(String message, Throwable cause) {
        super(message, cause);
    }
}
