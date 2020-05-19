package unitech.unicorn.exception;

public class AlreadyExistsException extends DataAccessException {
    private static final String DEFAULT_MESSAGE = "Object %s already exists.";

    public AlreadyExistsException(Object object) {
        this(String.format(DEFAULT_MESSAGE, object.getClass().getName()));
    }

    public AlreadyExistsException(String message) {
        super(message);
    }
}
