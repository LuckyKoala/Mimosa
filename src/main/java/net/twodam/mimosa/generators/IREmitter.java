package net.twodam.mimosa.generators;

import net.twodam.mimosa.backend.expressions.*;
import net.twodam.mimosa.backend.ir.Builtin;
import net.twodam.mimosa.exceptions.MimosaEvaluatorException;
import net.twodam.mimosa.types.*;
import net.twodam.mimosa.utils.TypeUtil;

import java.util.*;
import java.util.function.Consumer;

import static net.twodam.mimosa.backend.ir.IRVM.*;
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
 *
 * (#primitive function# symbol/immediate target)
 *
 */
public class IREmitter {
    Map<MimosaSymbol, Consumer<String>> compareFuncs = new HashMap<MimosaSymbol, Consumer<String>>() {{
        put(strToSymbol("="), falseLabel -> jne(falseLabel));
        put(strToSymbol(">"), falseLabel -> jle(falseLabel));
        put(strToSymbol(">="), falseLabel -> jl(falseLabel));
        put(strToSymbol("<"), falseLabel -> jge(falseLabel));
        put(strToSymbol("<="), falseLabel -> jg(falseLabel));
    }};


    /**
     * (inc (- 1 2) 1)
     *
     *
     * (- 1 2)      => "eval 1"
     *              => (pop temp) //initial
     *              => "eval 2"
     *              => (pop intermedia)
     *              => (- intermedia temp)
     *              => (push temp)
     * (inc ... 1)  => "eval 1"
     *              => "eval inc"
     *              => (pop func)
     *              => (call func)
     */
    void applicationExpr(MimosaPair expr) {
        MimosaType params = ApplicationExpr.params(expr);
        MimosaType functionExpr = ApplicationExpr.function(expr);

        if(TypeUtil.isCompatibleType(MimosaSymbol.class, functionExpr)) {
            MimosaSymbol funcName = (MimosaSymbol) functionExpr;

            if(compareFuncs.containsKey(funcName)) {
                eval(car(params));
                pop(INTERMEDIATE_REGISTER);
                eval(cadr(params));
                pop(TEMP__REGISTER);
                compare(INTERMEDIATE_REGISTER, TEMP__REGISTER);
                compareFuncs.get(funcName).accept(lastFalseLabelName);
                return;
            }

            if(Builtin.map.containsKey(funcName)) {
                //for builtin function
                eval(car(params));
                pop(TEMP__REGISTER);
                foreach(p -> {
                    eval(p);
                    pop(INTERMEDIATE_REGISTER);
                    instruction("(%s %s %s)", funcName, INTERMEDIATE_REGISTER, TEMP__REGISTER);
                }, cdr(params));
                push(TEMP__REGISTER);
                return;
            }
        }

        foreach(this::eval, params);
        eval(functionExpr);
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

        //params stack a->b>c
        //so the order of pop is c->b>a
        Stack<MimosaSymbol> stack = new Stack<>();
        foreach(p -> stack.push((MimosaSymbol) p), LambdaExpr.params(expr));
        while(!stack.empty()) {
            pop(stack.pop());
        }

        push(RET_ADDRESS_REGISTER);
        foreach(p -> {
            eval(p);
            pop(RET_REGISTER);
        }, LambdaExpr.body(expr));
        pop(RET_ADDRESS_REGISTER);
        push(RET_REGISTER);
        leave();
        push(lastLambdaName);
    }

    /**
     * (if (zero? (- 1 1)) 1 0)
     *
     * (zero? (- 1 1)) =>   "eval (zero? (- 1 1))"
     *                 =>   (pop ret)
     *                 =>   (compare ret 1)
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
        if(TypeUtil.isCompatibleType(MimosaPair.class, valueExpr)) {
            if(LambdaExpr.check((MimosaPair) valueExpr)) {
                global(lastLambdaName, symbol);
                return;
            }
        }

        pop(RET_REGISTER);
        global(RET_REGISTER, symbol);
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
            MimosaSymbol key = (MimosaSymbol) car(entry);
            MimosaType valueExpr = cadr(entry);

            eval(valueExpr);
            if(TypeUtil.isCompatibleType(MimosaPair.class, valueExpr)) {
                if(LambdaExpr.check((MimosaPair) valueExpr)) {
                    mov(lastLambdaName, key);
                    return;
                }
            }
            pop(key);
        }, entries);

        foreach(this::eval, LetExpr.body(expr));
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
            push((MimosaNumber)val);
            return;
        } else if (TypeUtil.isSymbol(val)) {
            push((MimosaSymbol) val);
            return;
        }

        //Analyze these "complex" types.
        TypeUtil.checkType(MimosaPair.class, val);
        MimosaPair expr = (MimosaPair) val;

        //Transform stage
        if(DoExpr.check(expr)) {
            eval(DoExpr.toLambdaApplication(expr));
            return;
        }

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

    public List<String> toSource() {
        return Arrays.asList(builder.toString().replace('\t', ' ').split("\n"));
    }

    @Override
    public String toString() {
        return builder.toString();
    }


    int lambdaCounter = 0;
    int branchCounter = 0;
    MimosaSymbol lastLambdaName = strToSymbol("undefined");
    String lastFalseLabelName = "undefined";

    String genLambdaName() {
        String name = String.format("lambda$%d", lambdaCounter++);
        lastLambdaName = strToSymbol(name);
        return name;
    }

    String[] genBranchNames() {
        String[] ret = new String[] {
                String.format("true$%d", branchCounter),
                String.format("false$%d", branchCounter),
                String.format("final$%d", branchCounter++)
        };
        lastFalseLabelName = ret[1];
        return ret;
    }

    void emit(String str) {
        builder.append('\t');
        emitNoIndent(str);
    }

    void emitNoIndent(String str) {
        builder.append(str);
        builder.append('\n');
    }

    void instruction(String format, Object... args) {
        emit(String.format(format, args));
    }

    void global(MimosaVal value, MimosaSymbol symbol) {
        instruction("(global %s %s)", value, symbol);
    }

    void mov(MimosaVal value, MimosaSymbol target) {
        instruction("(mov %s %s)", value, target);
    }

    void push(MimosaVal target) {
        instruction("(push %s)", target);
    }

    void pop(MimosaSymbol target) {
        instruction("(pop %s)", target);
    }

    void call(MimosaSymbol target) {
        instruction("(call %s)", target);
    }

    void compare(MimosaVal value1, MimosaVal value2) {
        instruction("(compare %s %s)", value1, value2);
    }

    void jne(String name) {
        instruction("(jne %s)", name);
    }

    void je(String name) {
        instruction("(je %s)", name);
    }

    void jl(String name) {
        instruction("(jl %s)", name);
    }

    void jg(String name) {
        instruction("(jg %s)", name);
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
