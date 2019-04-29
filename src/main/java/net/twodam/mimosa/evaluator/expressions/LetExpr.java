package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;

import static net.twodam.mimosa.types.MimosaList.list;
import static net.twodam.mimosa.utils.MimosaListUtil.append;
import static net.twodam.mimosa.utils.MimosaListUtil.map;

/**
 * (let ((var val) ...) exp1 exp2)
 * =>
 * ((lambda (var ...) exp1 exp2) val ...)
 * Created by luckykoala on 19-4-5.
 */
public class LetExpr {
    public static final MimosaSymbol TAG = MimosaSymbol.strToSymbol("let");

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaType toLambdaExpr(MimosaPair expr) {
        return append(list(append(list(LambdaExpr.TAG, bindingKeys(expr)), body(expr))), bindingValues(expr));
    }

    public static MimosaType bindingKeys(MimosaPair expr) {
        MimosaType entries = MimosaListUtil.cadr(expr);
        return map(MimosaListUtil::car, entries);
    }

    public static MimosaType bindingValues(MimosaPair expr) {
        MimosaType entries = MimosaListUtil.cadr(expr);
        return map(MimosaListUtil::cadr, entries);
    }

    public static MimosaType body(MimosaPair expr) {
        return MimosaListUtil.cddr(expr);
    }
}
