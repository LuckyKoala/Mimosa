package net.twodam.mimosa.parser;

public class ExtractSListException extends RuntimeException {
    public static final String EXTRACT_FROM_NON_SLIST_MESSAGE = "Can't extract from non-MimosaList";
    public static final String EXTRACT_FROM_EMPTY_SLIST_MESSAGE = "Can't extract from empty-MimosaList, aka nil";

    public ExtractSListException(String message) {
        super(message);
    }

    public ExtractSListException(String message, Throwable cause) {
        super(message, cause);
    }
}
