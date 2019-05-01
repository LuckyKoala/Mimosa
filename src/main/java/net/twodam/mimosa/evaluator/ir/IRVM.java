package net.twodam.mimosa.evaluator.ir;

import net.twodam.mimosa.parser.Parser;
import net.twodam.mimosa.types.MimosaNumber;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.TypeUtil;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static net.twodam.mimosa.utils.MimosaListUtil.*;

public class IRVM {
    static final String GLOBAL_PREFIX = "global_";
    static final int LABEL_FACTOR = 4;

    public static final MimosaSymbol RET_REGISTER = MimosaSymbol.strToSymbol("ret");

    //should only be used by generated code
    public static final MimosaSymbol INTERMEDIATE_REGISTER = MimosaSymbol.strToSymbol("__intermedia");
    public static final MimosaSymbol TEMP__REGISTER = MimosaSymbol.strToSymbol("__temp");
    public static final MimosaSymbol FUNC_REGISTER = MimosaSymbol.strToSymbol("__func");
    public static final MimosaSymbol FLAG_REGISTER = MimosaSymbol.strToSymbol("__flag");
    public static final MimosaSymbol RET_ADDRESS_REGISTER = MimosaSymbol.strToSymbol("__ret_address");

    Stack<Integer> stack;
    Map<MimosaSymbol, Register> registerMap;
    Map<MimosaSymbol, Integer> labelMap;
    List<MimosaType> source;
    int pc;

    static class Register {
        int val;

        @Override
        public String toString() {
            return Integer.toString(val);
        }
    }

    private IRVM(List<MimosaType> source) {
        this.stack = new Stack<>();
        this.registerMap = new HashMap<>();
        this.source = source;
        this.labelMap = new HashMap<>(source.size() / LABEL_FACTOR);
        this.pc = 0;
    }

    Register registerOf(MimosaSymbol symbol) {
        return registerOf(symbol, false);
    }

    Register registerOf(MimosaSymbol symbol, boolean createIfNotFound) {
        if(createIfNotFound) {
            return registerMap.computeIfAbsent(symbol, key -> new Register());
        } else {
            Register result = registerMap.get(symbol);
            requireNonNull(result, "Can't find register " + symbol);
            return result;
        }
    }

    public static int run(List<String> source) {
        IRVM irvm = new IRVM(source.stream().map(Parser::parse).collect(Collectors.toList()));

        irvm.analyze();
        irvm.eval();
        return irvm.getReturnValue();
    }

    int getReturnValue() {
        if(!stack.empty()) {
            registerOf(RET_REGISTER, true).val = stack.pop();
        }
        return registerOf(RET_REGISTER).val;
    }

    void analyze() {
        Iterator<MimosaType> iterator = source.iterator();
        while(iterator.hasNext()) {
            MimosaType expr = iterator.next();
            MimosaSymbol tag = (MimosaSymbol) car(expr);

            if(INSTRUCTION.LABEL.check(tag) || INSTRUCTION.FUNCTION.check(tag)) {
                MimosaSymbol name = (MimosaSymbol) cadr(expr);
                labelMap.put(name, pc+1);
            }

            pc++;
        }
        pc = 0;
    }

    int labelToPC(MimosaType label) {
        TypeUtil.checkType(MimosaSymbol.class, label);
        Object result = labelMap.get(label);
        requireNonNull(result, "Can't find label " + label);
        return (int) result;
    }

    void eval() {
        outerLoop:
        while(pc < source.size()) {
            MimosaType expr = source.get(pc);
            MimosaSymbol tag = (MimosaSymbol) car(expr);

            if(INSTRUCTION.LABEL.check(tag)) {
                pc++;
                continue;
            } else if(INSTRUCTION.FUNCTION.check(tag)) {
                //skip function definition if fall through
                int newPC = pc++;
                MimosaType _expr;
                MimosaSymbol _tag;
                do {
                    _expr = source.get(newPC);
                    _tag = (MimosaSymbol) car(_expr);
                    if(INSTRUCTION.LEAVE.check(_tag)) {
                        pc = newPC+1;
                        continue outerLoop;
                    }
                    newPC++;
                } while(newPC < source.size());
                throw new RuntimeException("Found not completed function definition for " + cadr(_expr));
            }

            if(INSTRUCTION.LEAVE.check(tag)) {
                pc = registerOf(RET_ADDRESS_REGISTER).val;
                continue;
            }

            MimosaType first = cadr(expr);
            if(INSTRUCTION.JMP.check(tag)) {
                pc = labelToPC(first);
                continue;
            }
            else if(INSTRUCTION.JE.check(tag)) {
                if(registerOf(FLAG_REGISTER).val == 0) {
                    pc = labelToPC(first);
                    continue;
                }
            }
            else if(INSTRUCTION.JNE.check(tag)) {
                if(registerOf(FLAG_REGISTER).val != 0) {
                    pc = labelToPC(first);
                    continue;
                }
            }
            else if(INSTRUCTION.JGE.check(tag)) {
                if(registerOf(FLAG_REGISTER).val >= 0) {
                    pc = labelToPC(first);
                    continue;
                }
            }
            else if(INSTRUCTION.JLE.check(tag)) {
                if(registerOf(FLAG_REGISTER).val <= 0) {
                    pc = labelToPC(first);
                    continue;
                }
            }
            else if(INSTRUCTION.CALL.check(tag)) {
                stack.push(pc+1);
                pc = getValue(first);
                continue;
            }
            else if(INSTRUCTION.PUSH.check(tag)) {
                int value = getValue(first);
                stack.push(value);
            }
            else if(INSTRUCTION.POP.check(tag)) {
                registerOf((MimosaSymbol) first, true).val = stack.pop();
            }
            else {
                MimosaType second = caddr(expr);
                if(INSTRUCTION.GLOBAL.check(tag)) {
                    registerOf((MimosaSymbol) second, true).val = getValue(first);
                }
                else if(INSTRUCTION.MOV.check(tag)) {
                    registerOf((MimosaSymbol) second, true).val = getValue(first);
                }
                else if(INSTRUCTION.COMPARE.check(tag)) {
                    registerOf(FLAG_REGISTER, true).val = Integer.compare(getValue(first), getValue(second));
                } else {
                    //builtin function
                    Builtin.BuiltinFunction builtinFunction = Builtin.map.get(tag);
                    requireNonNull(builtinFunction, "Can't find builtin function " + tag);
                    MimosaSymbol value = (MimosaSymbol) first;
                    MimosaSymbol target = (MimosaSymbol) second;
                    registerOf(target).val = builtinFunction.apply(registerOf(value).val, registerOf(target).val);
                }
            }

            pc++;
        }
    }

    private int getValue(MimosaType expr) {
        int value;
        if(TypeUtil.isCompatibleType(MimosaSymbol.class, expr)) {
            MimosaSymbol s = (MimosaSymbol) expr;
            if(labelMap.containsKey(s)) {
                value = labelToPC(s);
            } else {
                value = registerOf(s).val;
            }
        } else {
            value = MimosaNumber.valToNum(expr);
        }
        return value;
    }

    enum INSTRUCTION {
        GLOBAL("global"),
        MOV("mov"),
        PUSH("push"),
        POP("pop"),
        CALL("call"),
        COMPARE("compare"),
        JNE("jne"),
        JE("je"),
        JGE("jge"),
        JLE("jle"),
        JMP("jmp"),
        LABEL("label"),
        FUNCTION("function"),
        LEAVE("leave");

        private MimosaSymbol tag;

        INSTRUCTION(String tag) {
            this.tag = MimosaSymbol.strToSymbol(tag);
        }

        public boolean check(MimosaSymbol anotherTag) {
            return this.tag.equals(anotherTag);
        }
    }
}
