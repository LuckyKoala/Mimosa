package net.twodam.mimosa.exceptions;

import net.twodam.mimosa.types.MimosaPair;

public class MimosaIllegalExprException extends MimosaException {
    private static final String NON_SYMBOL_IN_LET_TEMPLATE = "Found non-symbol in let/lambda %s";
    private static final String PARAMETERS_IN_LAMBDA_IS_NOT_A_PAIR_TEMPLATE = "Parameters in %s is not a pair";
    private static final String NON_PAIR_IN_APPLICATION_EXPR_TEMPLATE = "Found non-pair in application expr %s";

    private MimosaIllegalExprException(String message) {
        super(message);
    }

    public static MimosaIllegalExprException nonSymbolInLet(MimosaPair pair) {
        return new MimosaIllegalExprException(String.format(NON_SYMBOL_IN_LET_TEMPLATE, pair));
    }

    public static MimosaIllegalExprException parametersInLambdaIsNotAPair(MimosaPair pair) {
        return new MimosaIllegalExprException(String.format(PARAMETERS_IN_LAMBDA_IS_NOT_A_PAIR_TEMPLATE, pair));
    }

    public static MimosaIllegalExprException nonPairInApplicationExpr(MimosaPair pair) {
        return new MimosaIllegalExprException(String.format(NON_PAIR_IN_APPLICATION_EXPR_TEMPLATE, pair));
    }
}
