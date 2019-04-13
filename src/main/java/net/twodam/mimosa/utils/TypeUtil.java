package net.twodam.mimosa.utils;

import net.twodam.mimosa.exceptions.MimosaUncompatibleTypeException;
import net.twodam.mimosa.types.MimosaNumber;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;

import java.util.function.Function;

/**
 * Created by luckykoala on 19-4-5.
 */
public class TypeUtil {
    private static final String UNCOMPATIBLE_TYPE_MESSAGE_TEMPLATE = "%s is not a instance of type [%s]";

    public static boolean isNumber(MimosaType val) {
        return isCompatibleType(MimosaNumber.class, val);
    }

    public static boolean isSymbol(MimosaType val) {
        return isCompatibleType(MimosaSymbol.class, val);
    }

    public static boolean isCompatibleType(Class clazz, MimosaType val) {
        return clazz.isInstance(val);
    }

    public static void checkType(Class clazz, MimosaType val) {
        if(!isCompatibleType(clazz, val))
            throw new MimosaUncompatibleTypeException(String.format(UNCOMPATIBLE_TYPE_MESSAGE_TEMPLATE, val, clazz.getSimpleName()));
    }

    public static void checkType(Class clazz, MimosaType val, String message) {
        if(!isCompatibleType(clazz, val))
            throw new MimosaUncompatibleTypeException(message);
    }

    public static void checkType(Class clazz, MimosaType val, Function<MimosaType, String> messageMapper) {
        if(!isCompatibleType(clazz, val))
            throw new MimosaUncompatibleTypeException(messageMapper.apply(val));
    }
}
