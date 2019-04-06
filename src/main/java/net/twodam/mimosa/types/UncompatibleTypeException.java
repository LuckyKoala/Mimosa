package net.twodam.mimosa.types;

/**
 * Created by luckykoala on 19-4-5.
 */
public class UncompatibleTypeException extends RuntimeException {
    public UncompatibleTypeException(String message) {
        super(message);
    }

    public UncompatibleTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
