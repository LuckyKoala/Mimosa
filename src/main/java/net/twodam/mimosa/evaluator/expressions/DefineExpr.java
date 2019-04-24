package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.types.MimosaList;
import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;
import net.twodam.mimosa.utils.TypeUtil;

/**
 * (define a 1)
 *
 * (define (f x) (+ x 1)) => (define f (lambda (x) (+ x 1)))
 */
public class DefineExpr {
    public static final MimosaSymbol TAG = MimosaSymbol.strToSymbol("define");

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaSymbol symbol(MimosaPair expr) {
        MimosaType val = MimosaListUtil.cadr(expr);
        if(TypeUtil.isCompatibleType(MimosaList.class, val)) {
            MimosaType symbol = MimosaListUtil.car(val);
            TypeUtil.checkType(MimosaSymbol.class, symbol);
            return (MimosaSymbol) symbol;
        }

        TypeUtil.checkType(MimosaSymbol.class, val);
        return (MimosaSymbol) val;
    }

    public static MimosaType value(MimosaPair expr) {
        MimosaType val = MimosaListUtil.cadr(expr);
        if(TypeUtil.isCompatibleType(MimosaList.class, val)) {
            return makeLambda(MimosaListUtil.cdr(val), MimosaListUtil.caddr(expr));
        }

        TypeUtil.checkType(MimosaSymbol.class, val);
        return MimosaListUtil.caddr(expr);
    }

    private static MimosaList makeLambda(MimosaType params, MimosaType expression) {
        TypeUtil.checkType(MimosaList.class, params);
        TypeUtil.checkType(MimosaList.class, expression);

        return MimosaList.list(LambdaExpr.TAG, params, expression);
    }
}
