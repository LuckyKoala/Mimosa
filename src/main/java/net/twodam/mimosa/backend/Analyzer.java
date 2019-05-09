package net.twodam.mimosa.backend;

import net.twodam.mimosa.backend.expressions.*;
import net.twodam.mimosa.exceptions.MimosaEvaluatorException;
import net.twodam.mimosa.types.*;
import net.twodam.mimosa.utils.TypeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.twodam.mimosa.utils.MimosaListUtil.foreach;

/**
 * Created by luckykoala on 19-5-8.
 */
public class Analyzer {
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
            return analyze(BeginExpr.toLambdaApplication(expr));
        }

        //Actual analyze stage
        if(QuoteExpr.check(expr)) {
            MimosaType value = QuoteExpr.value(expr);
            return env -> value;
        }
        else if(SetExpr.check(expr)) {
            MimosaSymbol symbol = SetExpr.symbol(expr);
            Analyzed value = analyze(SetExpr.value(expr));

            return env -> {
                Environment.set(env, symbol, value.apply(env));
                return MimosaList.nil();
            };
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

            return env -> Evaluator.apply(function.apply(env), params.stream().map(a -> a.apply(env)).collect(Collectors.toList()));
        }

        throw MimosaEvaluatorException.unsupportedSyntax(expr);
    }
}
