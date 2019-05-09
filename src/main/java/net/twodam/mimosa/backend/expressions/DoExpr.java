package net.twodam.mimosa.backend.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;

import static net.twodam.mimosa.backend.expressions.ApplicationExpr.makeApplication;
import static net.twodam.mimosa.backend.expressions.BeginExpr.sequenceToExp;
import static net.twodam.mimosa.backend.expressions.DefineExpr.makeDefine;
import static net.twodam.mimosa.backend.expressions.IfExpr.makeIf;
import static net.twodam.mimosa.backend.expressions.LambdaExpr.makeLambda;
import static net.twodam.mimosa.types.MimosaList.list;
import static net.twodam.mimosa.types.MimosaSymbol.strToSymbol;
import static net.twodam.mimosa.utils.MimosaListUtil.*;

/**
 * (do ([id init-expr step-expr-maybe] ...)
 *     (stop?-expr finish-expr ...)
 *   commands ...)
 * =>
 * (lambda ()
 *   (define iter
 *     (lambda (id ...)
 *       (if stop?-expr
 *         (begin finish-expr ...)
 *         (begin
 *           (begin commands ...)
 *           (iter step-expr-maybe ...)))))
 *   (iter init-expr ...))
 *
 * Created by luckykoala on 19-5-8.
 */
public class DoExpr {
    public static final MimosaSymbol TAG = strToSymbol("do");

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaType toLambdaApplication(MimosaPair expr) {
        return makeApplication(makeLambda(list(), list(
                makeDefine(strToSymbol("__iter"),
                        makeLambda(doVars(expr),
                                list(makeIf(doStopPredicate(expr),
                                        sequenceToExp(doFinishExprs(expr)),
                                        sequenceToExp(list(
                                                sequenceToExp(doCommands(expr)),
                                                makeApplication(strToSymbol("__iter"), doSteps(expr))
                                        )))))),
                makeApplication(strToSymbol("__iter"), doInits(expr))
        )), list());
    }

    public static MimosaType doVars(MimosaType expr) {
        MimosaType idExprs = cadr(expr);
        return map(MimosaListUtil::car, idExprs);
    }

    public static MimosaType doInits(MimosaType expr) {
        MimosaType idExprs = cadr(expr);
        return map(MimosaListUtil::cadr, idExprs);
    }

    public static MimosaType doSteps(MimosaType expr) {
        MimosaType idExprs = cadr(expr);
        return map(MimosaListUtil::caddr, idExprs);
    }

    public static MimosaType doStopPredicate(MimosaType expr) {
        MimosaType exprs = caddr(expr);
        return car(exprs);
    }

    public static MimosaType doFinishExprs(MimosaType expr) {
        MimosaType exprs = caddr(expr);
        return cdr(exprs);
    }

    public static MimosaType doCommands(MimosaType expr) {
        return cdddr(expr);
    }
}
