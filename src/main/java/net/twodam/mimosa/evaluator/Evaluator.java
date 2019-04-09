package net.twodam.mimosa.evaluator;

import net.twodam.mimosa.evaluator.expressions.*;
import net.twodam.mimosa.types.*;
import net.twodam.mimosa.utils.TypeUtil;

/**
 * enviroment
 * lambda
 */
public class Evaluator {
    public static MimosaType eval(MimosaPair expr, Enviroment env) {
        if(ConstExpr.check(expr)) {
            return ConstExpr.constant(expr);
        }
        else if(SymbolExpr.check(expr)) {
            return Enviroment.search(env, SymbolExpr.symbol(expr));
        }
        else if(DiffExpr.check(expr)) {
            MimosaType val1 = eval(DiffExpr.exp1(expr), env);
            MimosaType val2 = eval(DiffExpr.exp2(expr), env);
            return MimosaNumber.substract((MimosaVal) val1, (MimosaVal) val2);
        }
        else if(ZeroPredExpr.check(expr)) {
            MimosaType val = eval(ZeroPredExpr.predicate(expr), env);
            TypeUtil.checkType(MimosaNumber.class, val);
            return MimosaNumber.isZero((MimosaVal) val) ?
                    MimosaBool.TRUE : MimosaBool.FALSE;
        }
        else if(IfExpr.check(expr)) {
            MimosaPair predicate = IfExpr.predicate(expr);
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
            MimosaPair body = LetExpr.body(expr);
            return eval(body, extendedEnv);
        }
        else {
            throw new RuntimeException("Unsupported syntax: " + expr);
        }
    }
}
