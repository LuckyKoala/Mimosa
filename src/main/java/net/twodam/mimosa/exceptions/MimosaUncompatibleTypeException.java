package net.twodam.mimosa.exceptions;

/**
 * Created by luckykoala on 19-4-5.
 */
public class MimosaUncompatibleTypeException extends MimosaException {
    public MimosaUncompatibleTypeException(String message) {
        super(message);
    }

    public MimosaUncompatibleTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
