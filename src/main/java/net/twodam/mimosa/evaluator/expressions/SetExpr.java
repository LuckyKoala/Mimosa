package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;
import net.twodam.mimosa.utils.TypeUtil;

/**
 * (define x 0)
 * (let (x 2)
 *   x
 *   (set! x 1)
 *   x)
 * x
 */
public class SetExpr {
    public static final MimosaSymbol TAG = MimosaSymbol.strToSymbol("set!");

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaSymbol symbol(MimosaPair expr) {
        MimosaType val = MimosaListUtil.cadr(expr);
        TypeUtil.checkType(MimosaSymbol.class, val);
        return (MimosaSymbol) val;
    }

    public static MimosaType value(MimosaPair expr) {
        return MimosaListUtil.caddr(expr);
    }
}
