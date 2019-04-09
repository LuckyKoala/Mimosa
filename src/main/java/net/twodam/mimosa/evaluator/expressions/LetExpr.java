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
        TypeUtil.checkType(MimosaPair.class, val);
        MimosaPair symbolExpr = (MimosaPair) val;

        if(SymbolExpr.check(symbolExpr)) {
            return SymbolExpr.symbol(symbolExpr);
        } else {
            throw MimosaIllegalExprException.nonSymbolInLet(symbolExpr);
        }
    }

    public static MimosaPair bindingValue(MimosaPair expr) {
        MimosaType val = MimosaListUtil.cadadr(expr);
        TypeUtil.checkType(MimosaPair.class, val);
        return (MimosaPair) val;
    }

    public static MimosaPair body(MimosaPair expr) {
        MimosaType val = MimosaListUtil.caddr(expr);
        TypeUtil.checkType(MimosaPair.class, val);
        return (MimosaPair) val;
    }
}
