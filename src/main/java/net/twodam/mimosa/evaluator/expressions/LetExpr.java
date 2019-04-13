package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.exceptions.MimosaIllegalExprException;
import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;
import net.twodam.mimosa.utils.TypeUtil;

/**
 * (let (var val) body)
 * Created by luckykoala on 19-4-5.
 */
public class LetExpr {
    public static final MimosaSymbol TAG = MimosaSymbol.strToSymbol("let");

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
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
        return MimosaListUtil.caddr(expr);
    }
}
