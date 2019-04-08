package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;
import net.twodam.mimosa.utils.TypeUtil;

/**
 * (- exp1 exp2)
 *
 * Created by luckykoala on 19-4-8.
 */
public class DiffExpr {
    public static MimosaPair TAG = MimosaPair.cons(VarExpr.TAG, MimosaSymbol.strToSymbol("-"));

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaPair wrap(MimosaPair val) {
        return MimosaPair.cons(TAG, val);
    }

    public static MimosaPair exp1(MimosaPair expr) {
        MimosaType val = MimosaListUtil.cdr(expr);
        TypeUtil.checkType(MimosaPair.class, val);
        return (MimosaPair) val;
    }

    public static MimosaPair exp2(MimosaPair expr) {
        MimosaType val = MimosaListUtil.cdr(expr);
        TypeUtil.checkType(MimosaPair.class, val);
        return (MimosaPair) val;
    }
}
