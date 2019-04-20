package net.twodam.mimosa.evaluator;

import net.twodam.mimosa.evaluator.expressions.ApplicationExpr;
import net.twodam.mimosa.evaluator.expressions.IfExpr;
import net.twodam.mimosa.evaluator.expressions.LambdaExpr;
import net.twodam.mimosa.evaluator.expressions.LetExpr;
import net.twodam.mimosa.exceptions.MimosaEvaluatorException;
import net.twodam.mimosa.types.*;
import net.twodam.mimosa.utils.TypeUtil;

import static net.twodam.mimosa.utils.MimosaListUtil.map;

/**
 * enviroment
 * lambda
 */
public class Evaluator {
    public static MimosaType eval(MimosaType val) {
        return eval(val, MimosaRuntime.baseEnvironment);
    }

    public static MimosaType eval(MimosaType val, Environment env) {
        if (TypeUtil.isNumber(val)) {
            return val;
        } else if (TypeUtil.isSymbol(val)) {
            return Environment.search(env, (MimosaSymbol) val);
        } else if (TypeUtil.isCompatibleType(MimosaPrimitiveFunction.class, val)
                || TypeUtil.isCompatibleType(MimosaFunction.class, val)) {
            return val;
        }

        TypeUtil.checkType(MimosaPair.class, val);
        MimosaPair expr = (MimosaPair) val;

        if(IfExpr.check(expr)) {
            MimosaType predicate = IfExpr.predicate(expr);
            if(MimosaBool.isTrue(eval(predicate, env))) {
                return eval(IfExpr.trueExpr(expr), env);
            } else {
                return eval(IfExpr.falseExpr(expr), env);
            }
        }
        else if(LetExpr.check(expr)) {
            Environment extendedEnv = Environment.extend(env,
                    LetExpr.bindingKey(expr),
                    eval(LetExpr.bindingValue(expr), env));
            MimosaType body = LetExpr.body(expr);
            return eval(body, extendedEnv);
        } else if(LambdaExpr.check(expr)) {
            return MimosaFunction.wrap(expr, env);
        } else if(ApplicationExpr.check(expr)) {
            return apply(expr, env);
        }

        throw MimosaEvaluatorException.unsupportedSyntax(expr);
    }

    public static MimosaType apply(MimosaPair expr, Environment env) {
        MimosaType functionExpr = eval(ApplicationExpr.function(expr), env);

        if(TypeUtil.isCompatibleType(MimosaPrimitiveFunction.class, functionExpr)) {
            //primitive function?
            return MimosaRuntime.applyPrimitive(((MimosaPrimitiveFunction) functionExpr).primitiveSymbol(),
                    ApplicationExpr.params(expr), env);
        } else if(TypeUtil.isCompatibleType(MimosaFunction.class, functionExpr)) {
            //defined function?
            MimosaFunction lambdaClosure = (MimosaFunction) functionExpr;
            MimosaType valueExpr = ApplicationExpr.params(expr);
            Environment extendedEnv = Environment.extend(lambdaClosure.savedEnv(),
                    LambdaExpr.params(lambdaClosure.lambdaExpr()),
                    map(valExpr -> eval(valExpr, env), valueExpr));
            return eval(LambdaExpr.body(lambdaClosure.lambdaExpr()), extendedEnv);
        } else {
            throw MimosaEvaluatorException.unknownFunction(functionExpr);
        }
    }
}
