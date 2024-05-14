package voltron.coresys;

public class SculptorException extends Exception {

    public SculptorException(Throwable cause) {
        super(cause);
    }

    public SculptorException(String message, Throwable cause) {
        super(message, cause);
    }

    public SculptorException(final String eMsg) {
        super(eMsg);
    }
}
