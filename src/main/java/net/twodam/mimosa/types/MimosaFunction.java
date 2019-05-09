package net.twodam.mimosa.types;

import net.twodam.mimosa.backend.Environment;

/**
 * Created by luckykoala on 19-4-14.
 */
public class MimosaFunction extends MimosaType {
    MimosaType params;
    MimosaType body;
    Environment savedEnv;
    String expr; //only for toString

    public MimosaFunction(MimosaType params, MimosaType body, Environment savedEnv, String expr) {
        this.params = params;
        this.body = body;
        this.savedEnv = savedEnv;
        this.expr = expr;
    }

    public static MimosaFunction wrap(MimosaType params, MimosaType body, Environment savedEnv, String expr) {
        return new MimosaFunction(params, body, savedEnv, expr);
    }

    public MimosaType params() {
        return params;
    }

    public MimosaType body() {
        return body;
    }

    public Environment savedEnv() {
        return savedEnv;
    }

    @Override
    public String toString() {
        return "[#Closure " + expr + "]";
    }
}
