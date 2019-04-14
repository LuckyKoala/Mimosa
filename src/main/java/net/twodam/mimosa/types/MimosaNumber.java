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

    public static MimosaNumber negative(MimosaType mimosaType) {
        return numToVal(-valToNum(mimosaType));
    }

    public static MimosaNumber numToVal(int number) {
        return new MimosaNumber(number);
    }

    public static int valToNum(MimosaType mimosaVal) {
        TypeUtil.checkType(MimosaNumber.class, mimosaVal);
        return (int) ((MimosaNumber)mimosaVal).val;
    }

    public static boolean isZero(MimosaType val) {
        return ZERO.equals(val);
    }

    public static MimosaNumber add(MimosaType val1, MimosaType val2) {
        int num1 = valToNum(val1);
        int num2 = valToNum(val2);
        return numToVal(num1 + num2);
    }
}
