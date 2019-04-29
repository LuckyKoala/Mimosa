package net.twodam.mimosa.evaluator;

import net.twodam.mimosa.evaluator.expressions.*;
import net.twodam.mimosa.exceptions.MimosaEvaluatorException;
import net.twodam.mimosa.types.*;
import net.twodam.mimosa.utils.TypeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.twodam.mimosa.types.MimosaList.isNil;
import static net.twodam.mimosa.utils.MimosaListUtil.*;

/**
 */
public class Evaluator {
    /**
     * Separating syntactic analysis from execution.
     * So recursive function call will not analyze all the time while evaluating.
     *
     * @param val
     * @return
     */
    public static Analyzed analyze(MimosaType val) {
        //No more analyzing for these "simple" types.
        if (TypeUtil.isNumber(val)) {
            return env -> val;
        } else if (TypeUtil.isSymbol(val)) {
            return env -> Environment.search(env, (MimosaSymbol) val);
        } else if (TypeUtil.isCompatibleType(MimosaPrimitiveFunction.class, val)
                || TypeUtil.isCompatibleType(MimosaFunction.class, val)) {
            return env -> val;
        }

        //Analyze these "complex" types.
        TypeUtil.checkType(MimosaPair.class, val);
        MimosaPair expr = (MimosaPair) val;

        //Transform stage
        if(LetExpr.check(expr)) {
            return analyze(LetExpr.toLambdaExpr(expr));
        } else if(BeginExpr.check(expr)) {
            return analyze(BeginExpr.toLambdaExpr(expr));
        }

        //Actual analyze stage
        if(QuoteExpr.check(expr)) {
            MimosaType value = QuoteExpr.value(expr);
            return env -> value;
        }
        else if(DefineExpr.check(expr)) {
            MimosaSymbol symbol = DefineExpr.symbol(expr);
            Analyzed value = analyze(DefineExpr.value(expr));

            return env -> {
                MimosaRuntime.registerSymbol(symbol, value.apply(env));
                return MimosaList.nil();
            };
        }
        else if(IfExpr.check(expr)) {
            Analyzed predicate = analyze(IfExpr.predicate(expr));
            Analyzed trueExpr = analyze(IfExpr.trueExpr(expr));
            Analyzed falseExpr = analyze(IfExpr.falseExpr(expr));

            return env -> MimosaBool.isTrue(predicate.apply(env)) ?
                    trueExpr.apply(env) : falseExpr.apply(env);
        }
        else if(LambdaExpr.check(expr)) {
            MimosaType params = LambdaExpr.params(expr);
            MimosaType body = LambdaExpr.body(expr);

            return env -> MimosaFunction.wrap(params, body, env, expr.toString());
        }
        else if(ApplicationExpr.check(expr)) {
            Analyzed function = analyze(ApplicationExpr.function(expr));
            List<Analyzed> params = new ArrayList<>();
            foreach(v -> params.add(analyze(v)), ApplicationExpr.params(expr));

            return env -> apply(function.apply(env), params.stream().map(a -> a.apply(env)).collect(Collectors.toList()));
        }

        throw MimosaEvaluatorException.unsupportedSyntax(expr);
    }

    public static MimosaType eval(MimosaType val) {
        return eval(val, MimosaRuntime.baseEnvironment);
    }

    public static MimosaType eval(MimosaType val, Environment env) {
        return analyze(val).apply(env);
    }

    public static MimosaType apply(MimosaType function, List<MimosaType> params) {
        if(TypeUtil.isCompatibleType(MimosaPrimitiveFunction.class, function)) {
            //primitive function?
            return MimosaRuntime.applyPrimitive(((MimosaPrimitiveFunction) function).primitiveSymbol(),
                    params);
        } else if(TypeUtil.isCompatibleType(MimosaFunction.class, function)) {
            //defined function?
            MimosaFunction lambdaClosure = (MimosaFunction) function;
            MimosaType valueExpr = MimosaList.list(params);
            Environment extendedEnv = Environment.extend(lambdaClosure.savedEnv(),
                    lambdaClosure.params(),
                    valueExpr);
            MimosaType body = lambdaClosure.body();
            if(MimosaList.isNil(body)) {
                return MimosaList.nil();
            } else {
                while(!isNil(body) && !isNil(cdr(body))) {
                    eval(car(body), extendedEnv);
                    body = cdr(body);
                }
                return eval(car(body), extendedEnv);
            }
        } else {
            throw MimosaEvaluatorException.unknownFunction(function);
        }
    }
}
