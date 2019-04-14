package net.twodam.mimosa.evaluator;

import net.twodam.mimosa.exceptions.MimosaEvaluatorException;
import net.twodam.mimosa.exceptions.MimosaNoBindingException;
import net.twodam.mimosa.types.*;
import net.twodam.mimosa.utils.MimosaListUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static net.twodam.mimosa.evaluator.Evaluator.eval;
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
        registerPrimitiveFunction(strToSymbol("zero?"), params ->
                env -> MimosaNumber.isZero(eval(car(params), env)) ?
                        MimosaBool.TRUE : MimosaBool.FALSE);

        registerPrimitiveFunction(strToSymbol("+"), params -> {
            checkType(MimosaList.class, params);
            int len = valToNum(length(params));
            if(len == 0) throw MimosaEvaluatorException.paramsCountNotMatched(">=1", len);

            if(len == 2) {
                return env -> numToVal(valToNum(eval(car(params), env)) + valToNum(eval(cadr(params), env)));
            } else {
                return env ->
                        MimosaListUtil.foldl((val, result) -> MimosaNumber.add(eval(result, env), eval(val, env)),
                                MimosaNumber.numToVal(0), params);
            }
        });

        registerPrimitiveFunction(strToSymbol("-"), params -> env -> {
            if(length(params).equals(numToVal(1))) {
                return negative(car(params));
            } else {
                //(- 1 2 3)
                // =>
                //(+ 1 negative(+ 2 3))
                return applyPrimitive(strToSymbol("+"), MimosaList.list(Arrays.asList(car(params),
                        MimosaNumber.negative(applyPrimitive(strToSymbol("+"), cdr(params), env)))), env);
            }
        });

        //registerPrimitiveFunction(strToSymbol("cons"), params -> env -> MimosaPair.cons(car(params), cdr(params)));
        //registerPrimitiveFunction(strToSymbol("eval"), params -> env -> Evaluator.eval(car(params), env));
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
