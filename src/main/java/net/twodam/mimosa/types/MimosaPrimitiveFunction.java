package net.twodam.mimosa.types;

/**
 * 原始函数包装类，让语言使用者对其感知如定义函数一样。
 *
 * Created by luckykoala on 19-4-14.
 */
public class MimosaPrimitiveFunction extends MimosaType {
    //static Map<MimosaSymbol, MimosaPrimitiveFunction> cache = new HashMap<>();

    MimosaSymbol primitiveSymbol;

    MimosaPrimitiveFunction(MimosaSymbol primitiveSymbol) {
        this.primitiveSymbol = primitiveSymbol;
    }

    public static MimosaPrimitiveFunction wrap(MimosaSymbol symbol) {
        return new MimosaPrimitiveFunction(symbol);
        //return cache.putIfAbsent(symbol, new MimosaPrimitiveFunction(symbol));
    }

    public MimosaSymbol primitiveSymbol() {
        return primitiveSymbol;
    }

    @Override
    public String toString() {
        return "[#Primitive " + primitiveSymbol + "]";
    }
}
