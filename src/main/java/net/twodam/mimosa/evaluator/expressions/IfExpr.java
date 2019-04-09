package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;
import net.twodam.mimosa.utils.TypeUtil;

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

    public static MimosaPair predicate(MimosaPair expr) {
        MimosaType val = MimosaListUtil.cadr(expr);
        TypeUtil.checkType(MimosaPair.class, val);
        return (MimosaPair) val;
    }

    public static MimosaPair trueExpr(MimosaPair expr) {
        MimosaType val = MimosaListUtil.caddr(expr);
        TypeUtil.checkType(MimosaPair.class, val);
        return (MimosaPair) val;
    }

    public static MimosaPair falseExpr(MimosaPair expr) {
        MimosaType val = MimosaListUtil.cadddr(expr);
        TypeUtil.checkType(MimosaPair.class, val);
        return (MimosaPair) val;
    }
}
