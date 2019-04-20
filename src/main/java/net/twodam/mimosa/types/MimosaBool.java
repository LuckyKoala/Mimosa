package net.twodam.mimosa.types;

import net.twodam.mimosa.utils.TypeUtil;

/**
 * Created by luckykoala on 19-4-5.
 */
public class MimosaBool extends MimosaVal {
    public static final MimosaSymbol TRUE_SYM = MimosaSymbol.strToSymbol("#t");
    public static final MimosaSymbol FALSE_SYM = MimosaSymbol.strToSymbol("#f");

    public static final MimosaBool TRUE = new MimosaBool(true);
    public static final MimosaBool FALSE = new MimosaBool(false);

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

    public static boolean isTrue(MimosaType val) {
        if(TypeUtil.isCompatibleType(MimosaSymbol.class, val)) {
            return val.equals(TRUE_SYM);
        }

        TypeUtil.checkType(MimosaBool.class, val);
        return TRUE==val;
    }

    @Override
    public String toString() {
        return this==TRUE ? "#t" : "#f";
    }
}
