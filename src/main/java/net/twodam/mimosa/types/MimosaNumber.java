package net.twodam.mimosa.types;

import net.twodam.mimosa.utils.TypeUtil;

/**
 * Created by luckykoala on 19-4-5.
 */
public class MimosaNumber extends MimosaVal {
    private static final MimosaNumber ZERO = new MimosaNumber(0);

    MimosaNumber(int number) {
        super(number);
    }

    public static MimosaNumber numToVal(int number) {
        return new MimosaNumber(number);
    }

    public static int valToNum(MimosaType mimosaVal) {
        TypeUtil.checkType(MimosaNumber.class, mimosaVal);
        return (int) ((MimosaNumber)mimosaVal).val;
    }

    public static boolean isZero(MimosaVal mimosaVal) {
        return ZERO.equals(mimosaVal);
    }
}
