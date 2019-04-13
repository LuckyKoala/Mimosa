package net.twodam.mimosa.exceptions;

import net.twodam.mimosa.types.MimosaType;

/**
 * Created by luckykoala on 19-4-13.
 */
public class MimosaEvaluatorException extends MimosaException {
    private static final String UNSUPPORTED_SYNTAX = "Unsupported syntax: %s";

    private MimosaEvaluatorException(String message) {
        super(message);
    }

    public static MimosaEvaluatorException unsupportedSyntax(MimosaType val) {
        return new MimosaEvaluatorException(String.format(UNSUPPORTED_SYNTAX, val));
    }
}
