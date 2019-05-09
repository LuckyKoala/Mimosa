package net.twodam.mimosa.backend;

import net.twodam.mimosa.exceptions.MimosaEvaluatorException;
import net.twodam.mimosa.types.MimosaFunction;
import net.twodam.mimosa.types.MimosaList;
import net.twodam.mimosa.types.MimosaPrimitiveFunction;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.TypeUtil;

import java.util.List;

import static net.twodam.mimosa.types.MimosaList.isNil;
import static net.twodam.mimosa.utils.MimosaListUtil.car;
import static net.twodam.mimosa.utils.MimosaListUtil.cdr;

/**
 */
public class Evaluator {

    public static MimosaType eval(MimosaType val) {
        return eval(val, MimosaRuntime.baseEnvironment);
    }

    public static MimosaType eval(MimosaType val, Environment env) {
        return Analyzer.analyze(val).apply(env);
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
