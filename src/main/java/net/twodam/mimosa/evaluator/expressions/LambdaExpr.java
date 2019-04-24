package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;

/**
 * (lambda (a) (+ a 1))
 *
 * Created by luckykoala on 19-4-13.
 */
public class LambdaExpr {
    public static final MimosaSymbol TAG = MimosaSymbol.strToSymbol("lambda");

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaType params(MimosaPair expr) {
        return MimosaListUtil.cadr(expr);
    }

    public static MimosaType body(MimosaPair expr) {
        return MimosaListUtil.cddr(expr);
    }
}
