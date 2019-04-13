package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;

/**
 * (lambdaExpr valueExpr)
 *
 * Created by luckykoala on 19-4-13.
 */
public class ApplicationExpr {
    public static boolean check(MimosaPair expr) {
        return true;
    }

    public static MimosaType lambdaExpr(MimosaPair expr) {
        return MimosaListUtil.car(expr);
    }

    public static MimosaType valueExpr(MimosaPair expr) {
        return MimosaListUtil.cadr(expr);
    }
}
