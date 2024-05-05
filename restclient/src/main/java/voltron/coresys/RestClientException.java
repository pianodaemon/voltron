package voltron.coresys;

public class RestClientException extends Exception {

    public RestClientException(Throwable cause) {
        super(cause);
    }

    public RestClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
