package net.twodam.mimosa.generators;

import net.twodam.mimosa.evaluator.Analyzed;
import net.twodam.mimosa.evaluator.Environment;
import net.twodam.mimosa.evaluator.MimosaRuntime;
import net.twodam.mimosa.evaluator.expressions.*;
import net.twodam.mimosa.exceptions.MimosaEvaluatorException;
import net.twodam.mimosa.types.*;
import net.twodam.mimosa.utils.TypeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.twodam.mimosa.evaluator.Evaluator.analyze;
import static net.twodam.mimosa.types.MimosaList.isNil;
import static net.twodam.mimosa.types.MimosaList.nil;
import static net.twodam.mimosa.utils.MimosaListUtil.*;

/**
 * 基于栈的虚拟机
 *
 * (global symbol symbol/immediate) 全局字面量 string number
 * (mov symbol/immediate target) 将对应符号的值/立即数与target符号关联
 * (push symbol/immediate) 将对应符号的值/立即数入栈
 * (pop symbol) 将出栈的值与符号关联
 * (call func) 调用func (参数提前入栈，结果也在栈上)
 * (compare symbol/immediate symbol/immediate) 值比较，置flag
 * (jne) !=
 * (je) ==
 * (jge) >=
 * (jle) <=
 * (jmp) 无条件跳转
 * (label name) 代码块标签，用于跳转
 * (function name) 函数标签
 * (ret) 离开函数，返回上一层
 */
public class IREmitter {
    static StringBuilder builder = new StringBuilder();

    static void emit(String str) {
        builder.append('\t');
        emitNoIndent(str);
    }

    static void emitNoIndent(String str) {
        builder.append(str);
        builder.append('\n');
    }

    /**
     * (+ 2 1) => (push 2)
     *            (push 1)
     *            (call +)
     */
    static void applicationExpr(MimosaPair expr) {
        //
    }

    /**
     * (lambdaExpr (x)
     *   (f x)
     *   (+ x 1))
     *
     *   =>
     *
     * lambdaExpr  => (function)
     * (x)     => (pop x)
     * (f x)   => (push x)
     *            (call f)
     *            (pop nil)
     * (+ x 1) => (push x)
     *            (push 1)
     *            (call +)
     *            (pop ret)
     *         => (push ret)
     *            (leave)
     */
    static void lambdaExpr(MimosaPair expr) {
        emit("(function)");
        //get all parameters
        foreach(p -> emit(String.format("(pop %s)", p)), LambdaExpr.params(expr));
        //analyze expressions in body
        foreach(IREmitter::eval, LambdaExpr.body(expr));
        emit("(push ret)");
        emit("(leave)");
    }

    /**
     * (if (zero? (- 1 1)) 1 0)
     *
     * (- 1 1)   => ...
     * zero? ... => (pop ret)
     *              (compare 0 ret)
     *              (jne falseExpr)
     *
     */
    static void ifExpr(MimosaPair expr) {
        eval(IfExpr.predicate(expr)); //TODO predicate should emit (compare ...) and (jne) etc.
        //fall through
        emitNoIndent("(label trueExpr)");
        eval(IfExpr.trueExpr(expr));
        //skip falseExpr
        emit("(jmp otherExpr)");
        emitNoIndent("(label falseExpr)");
        eval(IfExpr.falseExpr(expr));
        emitNoIndent("(label otherExpr)");
    }

    /**
     * (define x (+ 2 1))
     *
     * (+ 2 1) => (push 2)
     *            (push 1)
     *            (call +)
     * x       => (pop ret)
     *            (global x ret)
     */
    static void defineExpr(MimosaPair expr) {
        eval(DefineExpr.value(expr));
        emit("(pop ret)");
        emit(String.format("(global %s ret)", DefineExpr.symbol(expr)));
    }

    /**
     * (letExpr ((x 1))
     *    (+ x 1))
     *
     *    =>
     *
     * (x 1) => (mov 1 x)
     *  ...  => ...
     */
    static void letExpr(MimosaPair expr) {
        MimosaType entries = LetExpr.entries(expr);

        foreach(entry -> {
            MimosaType key = car(entry);
            MimosaType value = cdr(entry);
            eval(value);
            emit("(pop ret)");
            emit(String.format("(mov ret %s)", key));
        }, entries);

        eval(LetExpr.body(expr));
    }

    static void setExpr(MimosaPair expr) {
        eval(SetExpr.value(expr));
        emit("(pop ret)");
        emit(String.format("(mov ret %s)", SetExpr.symbol(expr)));
    }

    static void beginExpr(MimosaPair expr) {
        foreach(IREmitter::eval, BeginExpr.body(expr));
    }

    public static void eval(MimosaType val) {
        //No more analyzing for these "simple" types.
        if (TypeUtil.isNumber(val)) {
            emit(String.format("(push %s)", val));
            return;
        } else if (TypeUtil.isSymbol(val)) {
            return;
        }

        //Analyze these "complex" types.
        TypeUtil.checkType(MimosaPair.class, val);
        MimosaPair expr = (MimosaPair) val;

        //Transform stage
        if(LetExpr.check(expr)) {
            letExpr(expr);
        } else if(BeginExpr.check(expr)) {
            beginExpr(expr);
        } else if(QuoteExpr.check(expr)) {
            emit(String.format("(push %s)", QuoteExpr.value(expr)));
        }
        else if(SetExpr.check(expr)) {
            setExpr(expr);
        }
        else if(DefineExpr.check(expr)) {
            defineExpr(expr);
        }
        else if(IfExpr.check(expr)) {
            ifExpr(expr);
        }
        else if(LambdaExpr.check(expr)) {
            lambdaExpr(expr);
        }
        else if(ApplicationExpr.check(expr)) {
            applicationExpr(expr);
        }

        throw MimosaEvaluatorException.unsupportedSyntax(expr);
    }
}
