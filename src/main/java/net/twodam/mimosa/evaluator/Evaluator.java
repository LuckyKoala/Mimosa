package net.twodam.mimosa.evaluator;

import net.twodam.mimosa.evaluator.expressions.*;
import net.twodam.mimosa.types.MimosaBool;
import net.twodam.mimosa.types.MimosaNumber;
import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaVal;
import net.twodam.mimosa.utils.TypeUtil;

/**
 * enviroment
 * lambda
 */
public class Evaluator {
    public MimosaVal eval(MimosaPair expr, Enviroment env) {
        if(ConstExpr.check(expr)) {
            return ConstExpr.constant(expr);
        }
        else if(VarExpr.check(expr)) {
            return eval(Enviroment.search(env, VarExpr.variable(expr)), env);
        }
        else if(LetExpr.check(expr)) {
            return eval(LetExpr.expression(expr),
                    Enviroment.extend(env,
                            LetExpr.bindingKey(expr),
                            LetExpr.bindingValue(expr)));
        }
        else if(ZeroPredExpr.check(expr)) {
            MimosaVal val = eval(ZeroPredExpr.predicate(expr), env);
            TypeUtil.checkType(MimosaNumber.class, val);
            MimosaNumber num = (MimosaNumber) val;
            return MimosaNumber.isZero(val) ?
                    MimosaBool.TRUE : MimosaBool.FALSE;
        }
        else if(DiffExpr.check(expr)) {
            MimosaVal val1 = eval(DiffExpr.exp1(expr), env);
            MimosaVal val2 = eval(DiffExpr.exp2(expr), env);
            return MimosaNumber.substract(val1, val2);
        }
        else if(IfExpr.check(expr)) {
            MimosaPair predicate = IfExpr.predicate(expr);
            if(MimosaBool.isTrue(eval(predicate, env))) {
                return eval(IfExpr.trueExpr(expr), env);
            } else {
                return eval(IfExpr.falseExpr(expr), env);
            }
        }
        else {
            throw new RuntimeException("Unsupported syntax: " + expr);
        }
    }
}
