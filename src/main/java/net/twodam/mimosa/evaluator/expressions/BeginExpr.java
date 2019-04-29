package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;

import static net.twodam.mimosa.types.MimosaList.list;
import static net.twodam.mimosa.utils.MimosaListUtil.append;

/**
 * (begin
 *   exp1
 *   ...)
 *
 *  =>
 *
 *  (lambda ()
 *    exp1
 *    ...)
 */
public class BeginExpr {
    public static final MimosaSymbol TAG = MimosaSymbol.strToSymbol("begin");

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaType toLambdaExpr(MimosaPair expr) {
        return list(
                append(list(LambdaExpr.TAG, list()), body(expr))
        );
    }

    public static MimosaType body(MimosaPair expr) {
        return MimosaListUtil.cdr(expr);
    }
}
