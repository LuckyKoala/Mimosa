package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;

/**
 * (- exp1 exp2)
 *
 * Created by luckykoala on 19-4-8.
 */
public class DiffExpr {
    public static MimosaSymbol TAG = MimosaSymbol.strToSymbol("-");

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaType exp1(MimosaPair expr) {
        return MimosaListUtil.cadr(expr);
    }

    public static MimosaType exp2(MimosaPair expr) {
        return MimosaListUtil.caddr(expr);
    }
}
