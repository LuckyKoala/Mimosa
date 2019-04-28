package net.twodam.mimosa.types;

import net.twodam.mimosa.utils.TypeUtil;

import java.util.List;

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

    /**
     * @param val
     * @return if val is #f, return false, otherwise true
     */
    public static boolean isTrue(MimosaType val) {
        if(TypeUtil.isCompatibleType(MimosaSymbol.class, val)) {
            return !val.equals(FALSE_SYM);
        } else if(TypeUtil.isCompatibleType(MimosaBool.class, val)) {
            return TRUE == val;
        }

        return true;
    }

    public static MimosaBool not(MimosaType val) {
        return boolToVal(!isTrue(val));
    }

    public static MimosaBool and(List<MimosaType> vals) {
        return vals.stream().reduce(TRUE, (a, b) -> boolToVal(isTrue(a) && isTrue(b)), (a, b) -> b);
    }

    public static MimosaBool or(List<MimosaType> vals) {
        return vals.stream().reduce(FALSE, (a, b) -> boolToVal(isTrue(a) || isTrue(b)), (a, b) -> b);
    }

    public static MimosaBool xor(List<MimosaType> vals) {
        return boolToVal(isTrue(vals.get(0)) ^ isTrue(vals.get(1)));
    }

    @Override
    public String toString() {
        return this==TRUE ? "#t" : "#f";
    }
}
