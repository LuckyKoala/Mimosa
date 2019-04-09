package net.twodam.mimosa.exceptions;

import net.twodam.mimosa.types.MimosaPair;

public class MimosaIllegalExprException extends MimosaException {
    private static final String NON_SYMBOL_IN_LET_TEMPLATE = "Found non-symbol %s in let body";

    MimosaIllegalExprException(String message) {
        super(message);
    }

    public static MimosaIllegalExprException nonSymbolInLet(MimosaPair pair) {
        return new MimosaIllegalExprException(String.format(NON_SYMBOL_IN_LET_TEMPLATE, pair));
    }
}
