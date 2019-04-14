package net.twodam.mimosa.exceptions;

import net.twodam.mimosa.types.MimosaType;

/**
 * Created by luckykoala on 19-4-13.
 */
public class MimosaEvaluatorException extends MimosaException {
    private static final String UNSUPPORTED_SYNTAX = "Unsupported syntax: %s";
    private static final String PARAMS_COUNT_NOT_MATCHED = "Parameters count not matched, expect %s | actual %d";
    private static final String UNKNOWN_FUNCTION = "Unknown function: %s";

    private MimosaEvaluatorException(String message) {
        super(message);
    }

    public static MimosaEvaluatorException unsupportedSyntax(MimosaType val) {
        return new MimosaEvaluatorException(String.format(UNSUPPORTED_SYNTAX, val));
    }

    public static MimosaEvaluatorException paramsCountNotMatched(String expect, int actual) {
        return new MimosaEvaluatorException(String.format(PARAMS_COUNT_NOT_MATCHED, expect, actual));
    }

    public static MimosaEvaluatorException unknownFunction(MimosaType function) {
        return new MimosaEvaluatorException(String.format(UNKNOWN_FUNCTION, function));
    }
}
