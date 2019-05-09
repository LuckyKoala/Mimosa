package net.twodam.mimosa.backend.expressions;

import net.twodam.mimosa.types.MimosaList;
import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;
import net.twodam.mimosa.utils.TypeUtil;

import static net.twodam.mimosa.backend.expressions.LambdaExpr.makeLambda;
import static net.twodam.mimosa.types.MimosaList.list;
import static net.twodam.mimosa.utils.MimosaListUtil.append;

/**
 * (define a 1)
 *
 * (define (f x) (+ x 1)) => (define f (lambda (x) (+ x 1)))
 *
 * TODO 嵌套define
 */
public class DefineExpr {
    public static final MimosaSymbol TAG = MimosaSymbol.strToSymbol("define");

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaType makeDefine(MimosaSymbol symbol, MimosaType value) {
        return list(TAG, symbol, value);
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
            return makeLambda(MimosaListUtil.cdr(val), MimosaListUtil.cddr(expr));
        }

        TypeUtil.checkType(MimosaSymbol.class, val);
        return MimosaListUtil.caddr(expr);
    }
}
