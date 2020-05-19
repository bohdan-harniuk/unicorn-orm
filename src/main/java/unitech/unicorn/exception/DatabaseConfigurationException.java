package unitech.unicorn.exception;

public class DatabaseConfigurationException extends RuntimeException {
    public DatabaseConfigurationException() {}

    public DatabaseConfigurationException(String message) {
        super(message);
    }
}
