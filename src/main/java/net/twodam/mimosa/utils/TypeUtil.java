package net.twodam.mimosa.utils;

import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.types.UncompatibleTypeException;

import java.util.function.Function;

/**
 * Created by luckykoala on 19-4-5.
 */
public class TypeUtil {
    private static final String UNCOMPATIBLE_TYPE_MESSAGE_TEMPLATE = "%s is not a instance of type [%s]";

    public static void checkType(Class clazz, MimosaType val) {
        if(!clazz.isInstance(val)) {
            throw new UncompatibleTypeException(String.format(UNCOMPATIBLE_TYPE_MESSAGE_TEMPLATE, val, clazz.getSimpleName()));
        }
    }

    public static void checkType(Class clazz, MimosaType val, String message) {
        if(!clazz.isInstance(val)) throw new UncompatibleTypeException(message);
    }

    public static void checkType(Class clazz, MimosaType val, Function<MimosaType, String> messageMapper) {
        if(!clazz.isInstance(val)) throw new UncompatibleTypeException(messageMapper.apply(val));
    }
}
