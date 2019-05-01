package net.twodam.mimosa.types;

import net.twodam.mimosa.utils.TypeUtil;

import java.util.List;

/**
 * Created by luckykoala on 19-4-5.
 */
public class MimosaNumber extends MimosaVal {
    public static final MimosaNumber ZERO = new MimosaNumber(0);

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
        return valToNum(a) == valToNum(b);
    }

    public static boolean isGreater(MimosaType a, MimosaType b) {
        return valToNum(a) > valToNum(b);
    }

    public static boolean isLess(MimosaType a, MimosaType b) {
        return valToNum(a) < valToNum(b);
    }

    public static MimosaNumber add(List<MimosaType> vals) {
        return vals.stream().reduce(ZERO, (a, b) -> numToVal(valToNum(a) + valToNum(b)), (a, b) -> b);
    }

    public static MimosaNumber subtract(List<MimosaType> vals) {
        if(vals.size() == 1) {
            return numToVal(-valToNum(vals.get(0)));
        } else {
            return vals.subList(1, vals.size()).stream().reduce((MimosaNumber) vals.get(0), (a, b) -> numToVal(valToNum(a) - valToNum(b)), (a, b) -> b);
        }
    }

    public static MimosaNumber multiply(List<MimosaType> vals) {
        return vals.stream().reduce(numToVal(1), (a, b) -> numToVal(valToNum(a) * valToNum(b)), (a, b) -> b);
    }

    public static MimosaNumber divide(List<MimosaType> vals) {
        if(vals.size() == 1) {
            return numToVal(1 / valToNum(vals.get(0)));
        } else {
            return vals.subList(1, vals.size()).stream().reduce((MimosaNumber) vals.get(0), (a, b) -> numToVal(valToNum(a) / valToNum(b)), (a, b) -> b);
        }
    }
}
