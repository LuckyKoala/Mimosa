package net.twodam.mimosa.generators;

import net.twodam.mimosa.evaluator.expressions.*;
import net.twodam.mimosa.evaluator.ir.IRVM;
import net.twodam.mimosa.exceptions.MimosaEvaluatorException;
import net.twodam.mimosa.types.MimosaNumber;
import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.TypeUtil;

import static net.twodam.mimosa.evaluator.ir.IRVM.*;
import static net.twodam.mimosa.types.MimosaNumber.numToVal;
import static net.twodam.mimosa.types.MimosaSymbol.strToSymbol;
import static net.twodam.mimosa.utils.MimosaListUtil.*;

/**
 * 基于栈的虚拟机
 *
 * (label name) 代码块标签，用于跳转
 * (function name) 函数标签
 *
 * (leave) 离开函数，返回上一层
 *
 * (jne name) !=
 * (je name) ==
 * (jge name) >=
 * (jle name) <=
 * (jmp name) 无条件跳转
 * (push symbol/immediate) 将对应符号的值/立即数入栈
 * (pop symbol) 将出栈的值与符号关联
 * (call func) 调用func (参数提前入栈，结果也在栈上)
 *
 * (global symbol/immediate target) (全局)将对应符号的值/立即数与target符号关联
 * (mov symbol/immediate target) 将对应符号的值/立即数与target符号关联
 * (compare symbol/immediate symbol/immediate) 值比较，置flag
 */
public class IREmitter {

    /**
     * (inc (+ 1 1) 1) => "eval (+ 1 1)"
     *               => # (pop ret)
     *               => # (push ret)
     *               => (push 1)
     *               => # "eval inc"
     *               => (pop func)
     *               => (call func)
     */
    void applicationExpr(MimosaPair expr) {
        foreach(this::eval, ApplicationExpr.params(expr));
        eval(ApplicationExpr.function(expr));
        pop(FUNC_REGISTER);
        call(FUNC_REGISTER);
    }

    /**
     * (lambdaExpr (x)
     *   (+ x 1))
     *
     *   =>
     *
     * lambdaExpr  => (function lambda$?)
     *         => (pop __ret_address)
     * (x)     => (pop x)
     * (+ x 1) => "eval (+ x 1)"
     *         => (push ret)
     *            (leave)
     */
    void lambdaExpr(MimosaPair expr) {
        function(genLambdaName());
        pop(RET_ADDRESS_REGISTER);
        foreach(p -> pop((MimosaSymbol) p), LambdaExpr.params(expr));
        foreach(p -> {
            eval(p);
            pop(RET_REGISTER);
        }, LambdaExpr.body(expr));
        push(RET_REGISTER);
        leave();
    }

    /**
     * (if (zero? (- 1 1)) 1 0)
     *
     * (zero? (- 1 1)) =>   "eval (zero? (- 1 1))"
     *                 =>   (pop ret)
     *                 =>   (compare ret 0)
     * (if ...         =>   (jne false$?)
     *   1             => (label true$?)
     *                 =>   "eval 1"
     *                 =>   # (pop ret)
     *                 =>   (jmp final$?)
     *   0             => (label false$?)
     *                 =>   "eval 0"
     *                 =>   # (pop ret)
     *                 => (label final$?)
     *                 =>   (push ret)
     *
     */
    void ifExpr(MimosaPair expr) {
        String[] labelNames = genBranchNames();
        String trueLabel = labelNames[0];
        String falseLabel = labelNames[1];
        String finalLabel = labelNames[2];

        eval(IfExpr.predicate(expr));
        pop(RET_REGISTER);
        compare(RET_REGISTER, numToVal(0));
        jne(falseLabel);
        //fall through to true branch
        label(trueLabel);
        eval(IfExpr.trueExpr(expr));
        //pop(RET);
        //skip false branch
        jmp(finalLabel);
        //jne instruction to this false branch
        label(falseLabel);
        eval(IfExpr.falseExpr(expr));
        //pop(RET);
        //final branch
        label(finalLabel);
        //push(RET);
    }

    /**
     * (define x (+ 2 1))
     *
     * (+ 2 1) => eval ...
     *         => (pop ret)
     * x       => (global ret x)
     *
     * (define x (lambda (x) (+ x 1))
     *
     * (lambda ...) => eval...
     * x            => (global "lastLambdaName" x)
     */
    void defineExpr(MimosaPair expr) {
        MimosaSymbol symbol = DefineExpr.symbol(expr);
        MimosaType valueExpr = DefineExpr.value(expr);

        eval(valueExpr);
        if(LambdaExpr.check((MimosaPair) valueExpr)) {
            global(strToSymbol(lastLambdaName), symbol);
        } else {
            pop(RET_REGISTER);
            global(RET_REGISTER, symbol);
        }
    }

    /**
     * (let ((x 1))
     *    (+ x 1))
     *
     *    =>
     *
     * (x 1) => "eval 1"
     *       => (pop x)
     *  ...
     * (+ x 1) => "eval ..."
     */
     void letExpr(MimosaPair expr) {
        MimosaType entries = LetExpr.entries(expr);

        foreach(entry -> {
            MimosaType key = car(entry);
            MimosaType value = cdr(entry);

            eval(value);
            pop((MimosaSymbol) key);
        }, entries);

        eval(LetExpr.body(expr));
    }

    /**
     * (set! a 1)
     */
    void setExpr(MimosaPair expr) {
        eval(SetExpr.value(expr));
        pop(SetExpr.symbol(expr));
    }

    void beginExpr(MimosaPair expr) {
        foreach(this::eval, BeginExpr.body(expr));
    }

    public void eval(MimosaType val) {
        //No more analyzing for these "simple" types.
        if (TypeUtil.isNumber(val)) {
            mov(val, RET_REGISTER);
            push(RET_REGISTER);
            return;
        } else if (TypeUtil.isSymbol(val)) {
            push((MimosaSymbol) val);
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
        } else {
            throw MimosaEvaluatorException.unsupportedSyntax(expr);
        }
    }

    // ================================= Instruction set =================================

    StringBuilder builder = new StringBuilder();

    public void clear() {
        builder.setLength(0);
    }

    @Override
    public String toString() {
        return builder.toString();
    }


    int lambdaCounter = 0;
    int branchCounter = 0;
    String lastLambdaName = "UNINITIALIZED";

    String genLambdaName() {
        lastLambdaName = String.format("lambda$%d", lambdaCounter++);
        return lastLambdaName;
    }

    String[] genBranchNames() {
        return new String[] {
                String.format("true$%d", branchCounter),
                String.format("false$%d", branchCounter),
                String.format("final$%d", branchCounter++)
        };
    }

    void emit(String str) {
        builder.append('\t');
        emitNoIndent(str);
    }

    void emitNoIndent(String str) {
        builder.append(str);
        builder.append('\n');
    }

    void symbolOrImmediate(MimosaType value) {
        if(!TypeUtil.isCompatibleType(MimosaSymbol.class, value)) {
            TypeUtil.checkType(MimosaNumber.class, value);
        }
    }

    void instruction(String format, Object... args) {
        emit(String.format(format, args));
    }

    void global(MimosaType value, MimosaSymbol symbol) {
        symbolOrImmediate(value);
        instruction("(global %s %s)", value, symbol);
    }

    void mov(MimosaType value, MimosaSymbol target) {
        symbolOrImmediate(value);
        instruction("(mov %s %s)", value, target);
    }

    void push(MimosaSymbol target) {
        instruction("(push %s)", target);
    }

    void pop(MimosaSymbol target) {
        instruction("(pop %s)", target);
    }

    void call(MimosaSymbol target) {
        instruction("(call %s)", target);
    }

    void compare(MimosaType value1, MimosaType value2) {
        symbolOrImmediate(value1);
        symbolOrImmediate(value2);
        instruction("(compare %s %s)", value1, value2);
    }

    void jne(String name) {
        instruction("(jne %s)", name);
    }

    void je(String name) {
        instruction("(je %s)", name);
    }

    void jge(String name) {
        instruction("(jge %s)", name);
    }

    void jle(String name) {
        instruction("(jle %s)", name);
    }

    void jmp(String name) {
        instruction("(jmp %s)", name);
    }

    void label(String name) {
        emitNoIndent(String.format("(label %s)", name));
    }

    void function(String name) {
        emitNoIndent(String.format("(function %s)", name));
    }

    void leave() {
        instruction("(leave)");
    }
}
