package net.twodam.mimosa.evaluator.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;

/**
 * (function params)
 *
 * Created by luckykoala on 19-4-13.
 */
public class ApplicationExpr {
    public static boolean check(MimosaPair expr) {
        return true;
    }

    public static MimosaType function(MimosaPair expr) {
        return MimosaListUtil.car(expr);
    }

    public static MimosaType params(MimosaPair expr) {
        return MimosaListUtil.cdr(expr);
    }
}
