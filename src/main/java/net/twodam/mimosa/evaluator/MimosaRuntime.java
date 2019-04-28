package net.twodam.mimosa.evaluator;

import net.twodam.mimosa.exceptions.MimosaEvaluatorException;
import net.twodam.mimosa.exceptions.MimosaNoBindingException;
import net.twodam.mimosa.types.*;
import net.twodam.mimosa.utils.TypeUtil;

import java.util.*;
import java.util.function.Function;

import static net.twodam.mimosa.evaluator.Evaluator.apply;
import static net.twodam.mimosa.types.MimosaBool.boolToVal;
import static net.twodam.mimosa.types.MimosaList.isNil;
import static net.twodam.mimosa.types.MimosaNumber.isZero;
import static net.twodam.mimosa.types.MimosaSymbol.strToSymbol;
import static net.twodam.mimosa.utils.MimosaListUtil.*;

/**
 * Created by luckykoala on 19-4-14.
 */
public class MimosaRuntime {
    static Environment baseEnvironment = Environment.empty();
    private static final Map<MimosaSymbol,
            Function<List<MimosaType>, MimosaType>> primitiveFunctionMap = new HashMap<>();

    static {
        registerSymbol(MimosaBool.TRUE_SYM, MimosaBool.TRUE);
        registerSymbol(MimosaBool.FALSE_SYM, MimosaBool.FALSE);

        //=== Boolean operation ===
        registerPrimitiveFunction(strToSymbol("not"), params -> {
            paramsLengthEq(1, params.size());
            return MimosaBool.not(params.get(0));
        });
        registerPrimitiveFunction(strToSymbol("and"), MimosaBool::and);
        registerPrimitiveFunction(strToSymbol("or"), MimosaBool::or);
        registerPrimitiveFunction(strToSymbol("xor"), params -> {
            paramsLengthEq(2, params.size());
            return MimosaBool.xor(params);
        });

        //=== Number predicate ===
        registerPrimitiveFunction(strToSymbol("zero?"), params -> {
            paramsLengthEq(1, params.size());
            return boolToVal(isZero(params.get(0)));
        });

        //=== Type predicate ===
        registerPrimitiveFunction(strToSymbol("null?"), params -> {
            paramsLengthEq(1, params.size());
            return boolToVal(isNil(params.get(0)));
        });
        registerPrimitiveFunction(strToSymbol("eq?"), params -> {
            paramsLengthEq(2, params.size());
            return boolToVal(params.get(0).equals(params.get(1)));
        });
        registerPrimitiveFunction(strToSymbol("number?"), params -> {
            paramsLengthEq(1, params.size());
            return boolToVal(TypeUtil.isCompatibleType(MimosaNumber.class, params.get(0)));
        });
        registerPrimitiveFunction(strToSymbol("symbol?"), params -> {
            paramsLengthEq(1, params.size());
            return boolToVal(TypeUtil.isCompatibleType(MimosaSymbol.class, params.get(0)));
        });

        registerPrimitiveFunction(strToSymbol("pair?"), params -> {
            paramsLengthEq(1, params.size());
            return boolToVal(TypeUtil.isCompatibleType(MimosaPair.class, params.get(0)));
        });

        //=== Number operation ===
        registerPrimitiveFunction(strToSymbol("="), params -> {
            paramsLengthEq(2, params.size());
            return boolToVal(MimosaNumber.isEqual(params.get(0), params.get(1)));
        });
        registerPrimitiveFunction(strToSymbol("+"), MimosaNumber::add);
        registerPrimitiveFunction(strToSymbol("-"), params -> {
            paramsLengthGe(1, params.size());
            return MimosaNumber.subtract(params);
        });

        //=== Pair ===
        registerPrimitiveFunction(strToSymbol("cons"), params -> MimosaPair.cons(params.get(0), params.get(1)));
        registerPrimitiveFunction(strToSymbol("car"), params -> car(params.get(0)));
        registerPrimitiveFunction(strToSymbol("cdr"), params -> cdr(params.get(0)));

        //=== List ===
        registerPrimitiveFunction(strToSymbol("list"), MimosaList::list);
        registerPrimitiveFunction(strToSymbol("length"), params -> {
            paramsLengthEq(1, params.size());
            return length(params.get(0));
        });
        registerPrimitiveFunction(strToSymbol("list-ref"), params -> {
            paramsLengthEq(2, params.size());
            return listRef(params.get(0), params.get(1));
        });

        //=== List operation ===
        registerPrimitiveFunction(strToSymbol("map"), params -> {
            paramsLengthEq(2, params.size());
            MimosaType function = params.get(0);
            MimosaType args = params.get(1);
            return map(v -> apply(function, Collections.singletonList(v)), args);
        });
        registerPrimitiveFunction(strToSymbol("foreach"), params -> {
            paramsLengthEq(2, params.size());
            MimosaType function = params.get(0);
            MimosaType args = params.get(1);
            foreach(v -> apply(function, Collections.singletonList(v)), args);
            return MimosaList.nil();
        });
        registerPrimitiveFunction(strToSymbol("foldl"), params -> {
            paramsLengthEq(3, params.size());
            MimosaType function = params.get(0);
            MimosaType initial = params.get(1);
            MimosaType args = params.get(2);
            return foldl((v1, v2) -> apply(function, Arrays.asList(v1, v2)), initial, args);
        });

        //=== System ===
        registerPrimitiveFunction(strToSymbol("display"), params -> {
            paramsLengthEq(1, params.size());
            System.out.println(params.get(0));
            return MimosaList.nil();
        });
        registerPrimitiveFunction(strToSymbol("newline"), params -> {
            System.out.println();
            return MimosaList.nil();
        });

        //registerPrimitiveFunction(strToSymbol("eval"), params -> env -> Evaluator.eval(car(params), env));
    }

    /**
     * ==
     */
    private static void paramsLengthEq(int expected, int actual) {
        if(actual != expected)
            throw MimosaEvaluatorException.paramsCountNotMatched("==" + expected, actual);
    }

    /**
     * >=
     */
    private static void paramsLengthGe(int expected, int actual) {
        if(actual < expected)
            throw MimosaEvaluatorException.paramsCountNotMatched(">=" + expected, actual);
    }

    public static void registerSymbol(MimosaSymbol symbol, MimosaType value) {
        baseEnvironment = Environment.extend(baseEnvironment, symbol, value);
    }

    private static void registerPrimitiveFunction(MimosaSymbol symbol,
                                                  Function<List<MimosaType>, MimosaType> function) {
        primitiveFunctionMap.put(symbol, function);
        //register primitive symbol to symbol table
        baseEnvironment = Environment.extend(baseEnvironment, symbol, MimosaPrimitiveFunction.wrap(symbol));
    }

    public static MimosaType applyPrimitive(MimosaSymbol name, List<MimosaType> params) {
        if(primitiveFunctionMap.containsKey(name)) {
            return primitiveFunctionMap.get(name).apply(params);
        } else {
            throw MimosaNoBindingException.noBindingOf(name);
        }
    }
}
