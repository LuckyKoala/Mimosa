package net.twodam.mimosa.evaluator;

import net.twodam.mimosa.exceptions.MimosaEvaluatorException;
import net.twodam.mimosa.exceptions.MimosaNoBindingException;
import net.twodam.mimosa.types.*;
import net.twodam.mimosa.utils.MimosaListUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static net.twodam.mimosa.evaluator.Evaluator.apply;
import static net.twodam.mimosa.evaluator.Evaluator.eval;
import static net.twodam.mimosa.types.MimosaList.list;
import static net.twodam.mimosa.types.MimosaNumber.*;
import static net.twodam.mimosa.types.MimosaSymbol.strToSymbol;
import static net.twodam.mimosa.utils.MimosaListUtil.*;
import static net.twodam.mimosa.utils.TypeUtil.checkType;

/**
 * Created by luckykoala on 19-4-14.
 */
public class MimosaRuntime {
    static Environment baseEnvironment = Environment.empty();
    static final Map<MimosaSymbol, Function<MimosaType, Function<Environment, MimosaType>>> primitiveFunctionMap = new HashMap<>();

    static {
        registerSymbol(MimosaBool.TRUE_SYM, MimosaBool.TRUE);
        registerSymbol(MimosaBool.FALSE_SYM, MimosaBool.FALSE);

        registerPrimitiveFunction(strToSymbol("quote"), params -> env -> car(params));

        //=== Number predicate ===
        registerPrimitiveFunction(strToSymbol("zero?"), params ->
                env -> MimosaNumber.isZero(eval(car(params), env)) ?
                        MimosaBool.TRUE : MimosaBool.FALSE);

        //=== Number operation ===
        registerPrimitiveFunction(strToSymbol("="), params -> {
            checkType(MimosaList.class, params);
            int len = valToNum(length(params));
            if(len != 2) throw MimosaEvaluatorException.paramsCountNotMatched("==2", len);

            return env ->
                    MimosaNumber.isEqual(eval(car(params), env), eval(cadr(params), env)) ?
                            MimosaBool.TRUE : MimosaBool.FALSE;
        });
        registerPrimitiveFunction(strToSymbol("+"), params -> {
            checkType(MimosaList.class, params);
            int len = valToNum(length(params));
            if(len == 0) throw MimosaEvaluatorException.paramsCountNotMatched(">=1", len);

            if(len == 2) {
                return env ->
                        MimosaNumber.add(eval(car(params), env), eval(cadr(params), env));
            } else {
                return env ->
                        MimosaListUtil.foldl((val, result) -> MimosaNumber.add(result, val),
                                MimosaNumber.numToVal(0), map(v -> eval(v, env), params));
            }
        });
        registerPrimitiveFunction(strToSymbol("-"), params -> env -> {
            if(length(params).equals(numToVal(1))) {
                return negative(car(params));
            } else {
                //(- 1 2 3)
                // =>
                //(+ 1 negative(+ 2 3))
                return applyPrimitive(strToSymbol("+"), list(car(params),
                        MimosaNumber.negative(applyPrimitive(strToSymbol("+"), cdr(params), env))), env);
            }
        });

        //=== Pair ===
        registerPrimitiveFunction(strToSymbol("cons"), params -> env ->
                MimosaPair.cons(eval(car(params), env), eval(cadr(params), env)));
        registerPrimitiveFunction(strToSymbol("car"), params -> env ->
                car(eval(car(params), env)));
        registerPrimitiveFunction(strToSymbol("cdr"), params -> env ->
                cdr(eval(car(params), env)));

        //=== List ===
        registerPrimitiveFunction(strToSymbol("list"), params -> env ->
                map(v -> eval(v, env), params));
        registerPrimitiveFunction(strToSymbol("length"), params -> env ->
                length(eval(car(params), env)));
        registerPrimitiveFunction(strToSymbol("list-ref"), params -> env ->
                listRef(eval(car(params), env), eval(cadr(params), env)));

        //=== List operation ===
        registerPrimitiveFunction(strToSymbol("map"), params -> env ->
                map(v -> apply(list(car(params), v), env), eval(cadr(params), env)));
        registerPrimitiveFunction(strToSymbol("foreach"), params -> env -> {
            foreach(v -> apply(list(car(params), v), env), eval(cdr(params), env));
            return MimosaList.nil();
        });
        registerPrimitiveFunction(strToSymbol("foldl"), params -> env ->
                foldl((v1, v2) -> apply(list(car(params), v1, v2), env), eval(cadr(params), env), eval(cddr(params), env)));

        //=== System ===
        registerPrimitiveFunction(strToSymbol("display"), params -> env -> {
            System.out.println(car(params));
            return MimosaList.nil();
        });
        registerPrimitiveFunction(strToSymbol("newline"), params -> env -> {
            System.out.println();
            return MimosaList.nil();
        });

        //registerPrimitiveFunction(strToSymbol("eval"), params -> env -> Evaluator.eval(car(params), env));
    }

    public static void registerSymbol(MimosaSymbol symbol, MimosaType value) {
        baseEnvironment = Environment.extend(baseEnvironment, symbol, value);
    }

    private static void registerPrimitiveFunction(MimosaSymbol symbol,
                                                  Function<MimosaType, Function<Environment, MimosaType>> function) {
        primitiveFunctionMap.put(symbol, function);
        //register primitive symbol to symbol table
        baseEnvironment = Environment.extend(baseEnvironment, symbol, MimosaPrimitiveFunction.wrap(symbol));
    }

    public static MimosaType applyPrimitive(MimosaSymbol name, MimosaType params, Environment env) {
        if(primitiveFunctionMap.containsKey(name)) {
            return primitiveFunctionMap.get(name).apply(params).apply(env);
        } else {
            throw MimosaNoBindingException.noBindingOf(name);
        }
    }
}
