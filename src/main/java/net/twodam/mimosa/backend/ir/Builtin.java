package net.twodam.mimosa.backend.ir;

import net.twodam.mimosa.types.MimosaSymbol;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static net.twodam.mimosa.types.MimosaSymbol.strToSymbol;

/**
 * (add symbol/immediate target)
 *
 * Created by luckykoala on 19-4-30.
 */
public class Builtin {
    public static Map<MimosaSymbol, BuiltinFunction> map;

    public interface BuiltinFunction extends BiFunction<Integer, Integer, Integer> {}

    static {
        map = new HashMap<>();

        map.put(strToSymbol("+"), (value, origin) -> origin + value);
        map.put(strToSymbol("-"), (value, origin) -> origin - value);
        map.put(strToSymbol("*"), (value, origin) -> origin * value);
        map.put(strToSymbol("/"), (value, origin) -> origin / value);

        map.put(strToSymbol("="), Builtin::compareNumber);
        map.put(strToSymbol(">"), Builtin::compareNumber);
        map.put(strToSymbol("<"), Builtin::compareNumber);
    }

    static Integer compareNumber(Integer value, Integer origin) {
        return Integer.compare(origin, value);
    }

    static int boolToInt(boolean bool) {
        return bool ? 1 : 0;
    }
}
