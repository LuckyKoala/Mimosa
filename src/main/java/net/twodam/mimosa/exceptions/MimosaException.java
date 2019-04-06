package net.twodam.mimosa.exceptions;

/**
 * Created by luckykoala on 19-4-5.
 */
public class MimosaException extends RuntimeException {
    public MimosaException() {
    }

    public MimosaException(String message) {
        super(message);
    }

    public MimosaException(String message, Throwable cause) {
        super(message, cause);
    }

    public MimosaException(Throwable cause) {
        super(cause);
    }

    public MimosaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
