package net.twodam.mimosa.backend.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;

import static net.twodam.mimosa.types.MimosaList.list;
import static net.twodam.mimosa.utils.MimosaListUtil.append;

/**
 * (lambda (a) (+ a 1))
 *
 * Created by luckykoala on 19-4-13.
 */
public class LambdaExpr {
    public static final MimosaSymbol TAG = MimosaSymbol.strToSymbol("lambda");

    public static MimosaType makeLambda(MimosaType params, MimosaType body) {
        return append(list(TAG, params), body);
    }

    public static boolean check(MimosaPair expr) {
        return TAG.equals(expr.car());
    }

    public static MimosaType params(MimosaPair expr) {
        return MimosaListUtil.cadr(expr);
    }

    public static MimosaType body(MimosaPair expr) {
        return MimosaListUtil.cddr(expr);
    }
}
