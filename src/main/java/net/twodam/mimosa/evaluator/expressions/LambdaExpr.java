package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.exceptions.MimosaIllegalExprException;
import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;
import net.twodam.mimosa.utils.TypeUtil;

/**
 * (lambda a (+ a 1))
 *
 * Created by luckykoala on 19-4-13.
 */
public class LambdaExpr {
    public static final MimosaSymbol TAG = MimosaSymbol.strToSymbol("lambda");

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaSymbol parameter(MimosaPair expr) {
        MimosaType val = MimosaListUtil.cadr(expr);
        if(TypeUtil.isCompatibleType(MimosaSymbol.class, val)) {
            return (MimosaSymbol) val;
        } else {
            throw MimosaIllegalExprException.nonSymbolInLet(expr);
        }
    }

    public static MimosaType body(MimosaPair expr) {
        return MimosaListUtil.caddr(expr);
    }
}
