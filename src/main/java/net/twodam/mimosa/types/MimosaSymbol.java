package net.twodam.mimosa.types;

import net.twodam.mimosa.utils.TypeUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luckykoala on 19-4-5.
 */
public class MimosaSymbol extends MimosaVal {
    private static final Map<String, MimosaSymbol> SYMBOL_POOL = new HashMap<>();

    MimosaSymbol(String str) {
        super(str);
    }

    public static MimosaSymbol strToSymbol(String str) {
        return SYMBOL_POOL.computeIfAbsent(str, MimosaSymbol::new);
    }

    public static String symbolToStr(MimosaVal mimosaVal) {
        TypeUtil.checkType(MimosaSymbol.class, mimosaVal);
        return (String) mimosaVal.val;
    }
}
