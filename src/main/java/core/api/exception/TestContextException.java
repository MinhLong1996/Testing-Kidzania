package core.api.exception;

public class TestContextException extends RuntimeException {
    private static final long serialVersionUID = 0L;

    public TestContextException(String message) {
        super(message);
    }

    public TestContextException(String message, Throwable cause) {
        super(message, cause);
    }
}
