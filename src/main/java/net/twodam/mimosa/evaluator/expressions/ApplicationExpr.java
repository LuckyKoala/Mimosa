package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.exceptions.MimosaIllegalExprException;
import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;
import net.twodam.mimosa.utils.TypeUtil;

/**
 * (lambdaExpr valueExpr)
 *
 * Created by luckykoala on 19-4-13.
 */
public class ApplicationExpr {
    public static boolean check(MimosaPair expr) {
        MimosaType val = expr.car();
        if(TypeUtil.isCompatibleType(MimosaPair.class, val)) {
            return LambdaExpr.check((MimosaPair) val);
        } else {
            return false;
        }
    }

    public static MimosaPair lambdaExpr(MimosaPair expr) {
        MimosaType val = MimosaListUtil.car(expr);
        if(TypeUtil.isCompatibleType(MimosaPair.class, expr)) {
            return (MimosaPair) val;
        } else {
            throw  MimosaIllegalExprException.nonPairInApplicationExpr(expr);
        }
    }

    public static MimosaType valueExpr(MimosaPair expr) {
        return MimosaListUtil.cadr(expr);
    }
}
