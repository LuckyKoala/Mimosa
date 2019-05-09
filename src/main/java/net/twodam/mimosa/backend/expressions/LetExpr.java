package net.twodam.mimosa.backend.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;

import static net.twodam.mimosa.backend.expressions.ApplicationExpr.makeApplication;
import static net.twodam.mimosa.backend.expressions.LambdaExpr.makeLambda;
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

    public static MimosaType makeLet(MimosaType entries, MimosaType exprs) {
        return append(list(TAG, entries), exprs);
    }

    public static MimosaType toLambdaExpr(MimosaPair expr) {
        return makeApplication(makeLambda(bindingKeys(expr), body(expr)), bindingValues(expr));
    }

    public static MimosaType entries(MimosaPair expr) {
        return MimosaListUtil.cadr(expr);
    }

    public static MimosaType bindingKeys(MimosaPair expr) {
        return map(MimosaListUtil::car, entries(expr));
    }

    public static MimosaType bindingValues(MimosaPair expr) {
        return map(MimosaListUtil::cadr, entries(expr));
    }

    public static MimosaType body(MimosaPair expr) {
        return MimosaListUtil.cddr(expr);
    }
}
