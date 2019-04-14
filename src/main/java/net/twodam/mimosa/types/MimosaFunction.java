package net.twodam.mimosa.types;

import net.twodam.mimosa.evaluator.Environment;

/**
 * Created by luckykoala on 19-4-14.
 */
public class MimosaFunction extends MimosaType {
    MimosaPair lambdaExpr;
    Environment savedEnv;

    MimosaFunction(MimosaPair lambdaExpr, Environment savedEnv) {
        this.lambdaExpr = lambdaExpr;
        this.savedEnv = savedEnv;
    }

    public static MimosaFunction wrap(MimosaPair lambdaExpr, Environment savedEnv) {
        return new MimosaFunction(lambdaExpr, savedEnv);
    }

    public MimosaPair lambdaExpr() {
        return lambdaExpr;
    }

    public Environment savedEnv() {
        return savedEnv;
    }

    @Override
    public String toString() {
        return "[#Closure " + lambdaExpr + "]";
    }
}
