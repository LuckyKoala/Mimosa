package net.twodam.mimosa.backend.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;

import static net.twodam.mimosa.backend.expressions.ApplicationExpr.makeApplication;
import static net.twodam.mimosa.backend.expressions.LambdaExpr.makeLambda;
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

    public static MimosaType sequenceToExp(MimosaType exprs) {
        return append(list(TAG), exprs);
    }

    public static MimosaType toLambdaApplication(MimosaPair expr) {
        return makeApplication(makeLambda(list(), body(expr)), list());
    }

    public static MimosaType body(MimosaPair expr) {
        return MimosaListUtil.cdr(expr);
    }
}
