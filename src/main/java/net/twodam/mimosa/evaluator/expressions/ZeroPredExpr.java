package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;
import net.twodam.mimosa.utils.TypeUtil;

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

    public static MimosaPair predicate(MimosaPair expr) {
        MimosaType val = MimosaListUtil.cadr(expr);
        TypeUtil.checkType(MimosaPair.class, val);
        return (MimosaPair) val;
    }
}
