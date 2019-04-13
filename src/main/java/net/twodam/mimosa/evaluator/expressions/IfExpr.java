package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;
/**
 * (if predicate trueExpr falseExpr)
 *
 * Created by luckykoala on 19-4-5.
 */
public class IfExpr {
    public static final MimosaSymbol TAG = MimosaSymbol.strToSymbol("if");

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaType predicate(MimosaPair expr) {
        return MimosaListUtil.cadr(expr);
    }

    public static MimosaType trueExpr(MimosaPair expr) {
        return MimosaListUtil.caddr(expr);
    }

    public static MimosaType falseExpr(MimosaPair expr) {
        return MimosaListUtil.cadddr(expr);
    }
}
