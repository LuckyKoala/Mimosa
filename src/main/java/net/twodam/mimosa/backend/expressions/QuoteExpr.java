package net.twodam.mimosa.backend.expressions;

import net.twodam.mimosa.types.MimosaList;
import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;

/**
 * (quote (2 3))
 */
public class QuoteExpr {
    public static final MimosaSymbol TAG = MimosaSymbol.strToSymbol("quote");

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaType value(MimosaPair expr) {
        return MimosaListUtil.cadr(expr);
    }

    public static MimosaType makeQuote(MimosaType value) {
        return MimosaList.list(TAG, value);
    }
}
