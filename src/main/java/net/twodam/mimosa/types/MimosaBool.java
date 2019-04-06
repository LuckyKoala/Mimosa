package net.twodam.mimosa.types;

import net.twodam.mimosa.utils.TypeUtil;

/**
 * Created by luckykoala on 19-4-5.
 */
public class MimosaBool extends MimosaVal {
    private static final MimosaBool TRUE = new MimosaBool(true);
    private static final MimosaBool FALSE = new MimosaBool(false);

    MimosaBool(boolean bool) {
        super(bool);
    }

    public static MimosaBool boolToVal(boolean bool) {
        return bool ? TRUE : FALSE;
    }

    public static boolean valToBool(MimosaVal mimosaVal) {
        TypeUtil.checkType(MimosaBool.class, mimosaVal);
        return (boolean) mimosaVal.val;
    }

    public static boolean isTrue(MimosaVal val) {
        return TRUE==val;
    }
}
