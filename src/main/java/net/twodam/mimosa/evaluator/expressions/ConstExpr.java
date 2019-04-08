package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.types.*;
import net.twodam.mimosa.utils.MimosaListUtil;
import net.twodam.mimosa.utils.TypeUtil;

/**
 * 1
 *
 * Created by luckykoala on 19-4-5.
 */
public class ConstExpr {
    private static MimosaSymbol TAG = MimosaSymbol.strToSymbol("#const");

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaPair wrap(MimosaNumber val) {
        return MimosaPair.cons(TAG, val);
    }

    public static MimosaNumber constant(MimosaPair expr) {
        MimosaType val = MimosaListUtil.cdr(expr);
        TypeUtil.checkType(MimosaNumber.class, val);
        return (MimosaNumber) val;
    }
}
