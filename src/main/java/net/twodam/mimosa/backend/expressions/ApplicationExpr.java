package net.twodam.mimosa.backend.expressions;

import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.MimosaListUtil;

import static net.twodam.mimosa.types.MimosaList.list;
import static net.twodam.mimosa.utils.MimosaListUtil.append;

/**
 * (function params)
 *
 * Created by luckykoala on 19-4-13.
 */
public class ApplicationExpr {
    public static boolean check(MimosaPair expr) {
        return true;
    }

    public static MimosaType makeApplication(MimosaType function, MimosaType params) {
        return append(list(function), params);
    }

    public static MimosaType function(MimosaPair expr) {
        return MimosaListUtil.car(expr);
    }

    public static MimosaType params(MimosaPair expr) {
        return MimosaListUtil.cdr(expr);
    }
}
