package net.twodam.mimosa.types;

import net.twodam.mimosa.utils.TypeUtil;

import java.util.List;

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

    public static boolean isZero(MimosaType val) {
        return ZERO.equals(val);
    }

    public static boolean isEqual(MimosaType a, MimosaType b) {
        TypeUtil.checkType(MimosaNumber.class, a);
        TypeUtil.checkType(MimosaNumber.class, b);

        return a.equals(b);
    }

    public static MimosaNumber add(List<MimosaType> vals) {
        return vals.stream().reduce(ZERO, (a, b) -> numToVal(valToNum(a) + valToNum(b)), (a, b) -> b);
    }

    public static MimosaNumber subtract(List<MimosaType> vals) {
        if(vals.size() == 1) {
            return numToVal(0 - valToNum(vals.get(0)));
        } else {
            return vals.subList(1, vals.size()).stream().reduce((MimosaNumber) vals.get(0), (a, b) -> numToVal(valToNum(a) - valToNum(b)), (a, b) -> b);
        }
    }
}
