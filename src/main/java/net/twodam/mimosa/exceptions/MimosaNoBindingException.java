package net.twodam.mimosa.exceptions;

import net.twodam.mimosa.types.MimosaSymbol;

/**
 * Created by luckykoala on 19-4-8.
 */
public class MimosaNoBindingException extends MimosaException {
    private static final String NO_BINDING_TEMPLATE = "Can't find the binding of symbol %s";

    public MimosaNoBindingException(String message) {
        super(message);
    }

    public static MimosaNoBindingException noBindingOf(MimosaSymbol symbol) {
        return new MimosaNoBindingException(String.format(NO_BINDING_TEMPLATE, MimosaSymbol.symbolToStr(symbol)));
    }
}
