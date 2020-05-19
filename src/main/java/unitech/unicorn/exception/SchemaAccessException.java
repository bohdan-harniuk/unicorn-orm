package unitech.unicorn.exception;

public class SchemaAccessException extends RuntimeException {
    public SchemaAccessException() {}

    public SchemaAccessException(String message) {
        super(message);
    }
}
