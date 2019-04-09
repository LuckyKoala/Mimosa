package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;
import net.twodam.mimosa.utils.TypeUtil;

/**
 * x
 *
 * Created by luckykoala on 19-4-5.
 */
public class SymbolExpr {
    public static MimosaSymbol TAG = MimosaSymbol.strToSymbol("#symbol");

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaPair wrap(MimosaSymbol val) {
        return MimosaPair.cons(TAG, val);
    }

    public static MimosaSymbol symbol(MimosaPair expr) {
        MimosaType val = MimosaListUtil.cdr(expr);
        TypeUtil.checkType(MimosaSymbol.class, val);
        return (MimosaSymbol) val;
    }
}
