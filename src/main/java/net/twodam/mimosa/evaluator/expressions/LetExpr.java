package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;
import net.twodam.mimosa.utils.TypeUtil;

/**
 * (let (var val) exp)
 * Created by luckykoala on 19-4-5.
 */
public class LetExpr {
    private static final MimosaSymbol TAG = MimosaSymbol.strToSymbol("let");

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaPair wrap(MimosaPair expr) {
        return MimosaPair.cons(TAG, expr);
    }

    public static MimosaSymbol bindingKey(MimosaPair expr) {
        MimosaType val = MimosaListUtil.cdr(expr);
        TypeUtil.checkType(MimosaSymbol.class, val);
        return (MimosaSymbol) val;
    }

    public static MimosaPair bindingValue(MimosaPair expr) {
        MimosaType val = MimosaListUtil.cdr(expr);
        TypeUtil.checkType(MimosaPair.class, val);
        return (MimosaPair) val;
    }

    public static MimosaPair expression(MimosaPair expr) {
        MimosaType val = MimosaListUtil.cdr(expr);
        TypeUtil.checkType(MimosaPair.class, val);
        return (MimosaPair) val;
    }
}
