package net.twodam.mimosa.types;

import net.twodam.mimosa.evaluator.Enviroment;

/**
 * Created by luckykoala on 19-4-14.
 */
public class MimosaClosure extends MimosaType {
    MimosaPair lambdaExpr;
    Enviroment savedEnv;

    MimosaClosure(MimosaPair lambdaExpr, Enviroment savedEnv) {
        this.lambdaExpr = lambdaExpr;
        this.savedEnv = savedEnv;
    }

    public static MimosaClosure wrap(MimosaPair lambdaExpr, Enviroment savedEnv) {
        return new MimosaClosure(lambdaExpr, savedEnv);
    }

    public MimosaPair lambdaExpr() {
        return lambdaExpr;
    }

    public Enviroment savedEnv() {
        return savedEnv;
    }

    @Override
    public String toString() {
        return "[#Closure " + lambdaExpr + " ]";
    }
}
