package net.twodam.mimosa.evaluator;

import net.twodam.mimosa.evaluator.expressions.*;
import net.twodam.mimosa.types.*;
import net.twodam.mimosa.utils.TypeUtil;

/**
 * enviroment
 * lambda
 */
public class Evaluator {
    public static MimosaType eval(MimosaType val, Enviroment env) {
        if (TypeUtil.isNumber(val)) {
            return val;
        } else if (TypeUtil.isSymbol(val)) {
            return Enviroment.search(env, (MimosaSymbol) val);
        }

        TypeUtil.checkType(MimosaPair.class, val);
        MimosaPair expr = (MimosaPair) val;

        if(DiffExpr.check(expr)) {
            MimosaType val1 = eval(DiffExpr.exp1(expr), env);
            MimosaType val2 = eval(DiffExpr.exp2(expr), env);
            return MimosaNumber.substract((MimosaVal) val1, (MimosaVal) val2);
        }
        else if(ZeroPredExpr.check(expr)) {
            MimosaType ret = eval(ZeroPredExpr.predicate(expr), env);
            TypeUtil.checkType(MimosaNumber.class, ret);
            return MimosaNumber.isZero((MimosaVal) ret) ?
                    MimosaBool.TRUE : MimosaBool.FALSE;
        }
        else if(IfExpr.check(expr)) {
            MimosaType predicate = IfExpr.predicate(expr);
            if(MimosaBool.isTrue(eval(predicate, env))) {
                return eval(IfExpr.trueExpr(expr), env);
            } else {
                return eval(IfExpr.falseExpr(expr), env);
            }
        }
        else if(LetExpr.check(expr)) {
            Enviroment extendedEnv = Enviroment.extend(env,
                    LetExpr.bindingKey(expr),
                    eval(LetExpr.bindingValue(expr), env));
            MimosaType body = LetExpr.body(expr);
            return eval(body, extendedEnv);
        }
        else {
            throw new RuntimeException("Unsupported syntax: " + expr);
        }
    }
}
