package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.exceptions.MimosaIllegalExprException;
import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;
import net.twodam.mimosa.utils.TypeUtil;

import static net.twodam.mimosa.types.MimosaList.list;
import static net.twodam.mimosa.utils.MimosaListUtil.append;

/**
 * (let (var val) exp1 exp2)
 * =>
 * ((lambda (var) exp1 exp2) val)
 * Created by luckykoala on 19-4-5.
 */
public class LetExpr {
    public static final MimosaSymbol TAG = MimosaSymbol.strToSymbol("let");

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaType toLambdaExpr(MimosaPair expr) {
        return list(
                append(list(LambdaExpr.TAG, list(bindingKey(expr))), body(expr)),
                bindingValue(expr)
        );
    }

    public static MimosaSymbol bindingKey(MimosaPair expr) {
        MimosaType val = MimosaListUtil.caadr(expr);
        if(TypeUtil.isCompatibleType(MimosaSymbol.class, val)) {
            return (MimosaSymbol) val;
        } else {
            throw MimosaIllegalExprException.nonSymbolInLet(expr);
        }
    }

    public static MimosaType bindingValue(MimosaPair expr) {
        return MimosaListUtil.cadadr(expr);
    }

    public static MimosaType body(MimosaPair expr) {
        return MimosaListUtil.cddr(expr);
    }
}
