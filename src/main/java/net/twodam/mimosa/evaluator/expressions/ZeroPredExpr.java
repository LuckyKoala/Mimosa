package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;

/**
 * (zero? s)
 *
 * Created by luckykoala on 19-4-5.
 */
public class ZeroPredExpr {
    public static final MimosaSymbol TAG = MimosaSymbol.strToSymbol("zero?");

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaType predicate(MimosaPair expr) {
        return MimosaListUtil.cadr(expr);
    }
}
