package net.twodam.mimosa.evaluator.ir;

import net.twodam.mimosa.parser.Parser;
import net.twodam.mimosa.types.MimosaNumber;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.TypeUtil;

import java.util.*;
import java.util.stream.Collectors;

import static net.twodam.mimosa.utils.MimosaListUtil.*;

public class IRVM {
    static final String GLOBAL_PREFIX = "global_";
    static final int LABEL_FACTOR = 4;

    public static final MimosaSymbol RET_REGISTER = MimosaSymbol.strToSymbol("ret");
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
    }

    private IRVM(List<MimosaType> source) {
        this.stack = new Stack<>();
        this.registerMap = new HashMap<>();
        this.source = source;
        this.labelMap = new HashMap<>(source.size() / LABEL_FACTOR);
        this.pc = 0;
    }

    Register registerOf(MimosaSymbol symbol) {
        return registerMap.putIfAbsent(symbol, new Register());
    }

    public static int run(List<String> source) {
        IRVM irvm = new IRVM(source.stream().map(Parser::parse).collect(Collectors.toList()));

        irvm.analyze();
        irvm.eval();
        return irvm.registerOf(RET_REGISTER).val;
    }

    void analyze() {
        Iterator<MimosaType> iterator = source.iterator();
        while(iterator.hasNext()) {
            MimosaType expr = iterator.next();
            MimosaType tag = car(expr);

            if(INSTRUCTION.LABEL.check(tag) || INSTRUCTION.FUNCTION.check(tag)) {
                MimosaSymbol name = (MimosaSymbol) cadr(expr);
                labelMap.put(name, pc);
                iterator.remove();
            }

            pc++;
        }
        pc = 0;
    }

    void eval() {
        while(pc < source.size()) {
            MimosaType expr = source.get(pc);
            MimosaType tag = car(expr);

            if(INSTRUCTION.LABEL.check(tag) || INSTRUCTION.FUNCTION.check(tag)) {
                throw new RuntimeException("label/function instruction found in eval stage, code is " + expr);
            }

            if(INSTRUCTION.LEAVE.check(tag)) {
                pc = stack.pop();
                continue;
            }

            MimosaType first = cadr(expr);
            if(INSTRUCTION.JMP.check(tag)) {
                pc = labelMap.get(first);
                continue;
            }
            else if(INSTRUCTION.JE.check(tag)) {
                if(registerOf(FLAG_REGISTER).val == 0) {
                    pc = labelMap.get(first);
                    continue;
                }
            }
            else if(INSTRUCTION.JNE.check(tag)) {
                if(registerOf(FLAG_REGISTER).val != 0) {
                    pc = labelMap.get(first);
                    continue;
                }
            }
            else if(INSTRUCTION.JGE.check(tag)) {
                if(registerOf(FLAG_REGISTER).val >= 0) {
                    pc = labelMap.get(first);
                    continue;
                }
            }
            else if(INSTRUCTION.JLE.check(tag)) {
                if(registerOf(FLAG_REGISTER).val <= 0) {
                    pc = labelMap.get(first);
                    continue;
                }
            }
            else if(INSTRUCTION.CALL.check(tag)) {
                stack.push(pc+1);
                pc = labelMap.get(first);
                continue;
            }
            else if(INSTRUCTION.PUSH.check(tag)) {
                int value;
                if(TypeUtil.isCompatibleType(MimosaSymbol.class, first)) {
                    value = registerOf((MimosaSymbol) first).val;
                } else {
                    value = MimosaNumber.valToNum(first);
                }
                stack.push(value);
            }
            else if(INSTRUCTION.POP.check(tag)) {
                registerOf((MimosaSymbol) first).val = stack.pop();
            }
            else {
                MimosaType second = caddr(expr);
                if(INSTRUCTION.GLOBAL.check(tag)) {
                    int value;
                    if(TypeUtil.isCompatibleType(MimosaSymbol.class, first)) {
                        value = registerOf((MimosaSymbol) first).val;
                    } else {
                        value = MimosaNumber.valToNum(first);
                    }
                    //TODO implement semantic of global
                    registerOf((MimosaSymbol) first).val = value;
                }
                else if(INSTRUCTION.MOV.check(tag)) {
                    int value;
                    if(TypeUtil.isCompatibleType(MimosaSymbol.class, first)) {
                        value = registerOf((MimosaSymbol) first).val;
                    } else {
                        value = MimosaNumber.valToNum(first);
                    }
                    registerOf((MimosaSymbol) first).val = value;
                }
                else if(INSTRUCTION.COMPARE.check(tag)) {
                    registerOf(FLAG_REGISTER).val = Integer.compare(registerOf((MimosaSymbol) first).val,
                            MimosaNumber.valToNum(second));
                }
            }

            pc++;
        }
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

        public boolean check(MimosaType anotherTag) {
            return this.tag.equals(anotherTag);
        }
    }
}
