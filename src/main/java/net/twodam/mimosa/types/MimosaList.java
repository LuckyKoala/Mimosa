package net.twodam.mimosa.types;

import net.twodam.mimosa.exceptions.MimosaPairExtractorException;
import net.twodam.mimosa.utils.MimosaListUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MimosaList extends MimosaPair {
    MimosaList(MimosaType car, MimosaList cdr) {
        super(car, cdr);
    }

    public static MimosaList cons(MimosaType car, MimosaList cdr) {
        return new MimosaList(car, cdr);
    }

    public static MimosaList list(MimosaType... vals) {
        MimosaList list = nil();
        for(int i=vals.length-1; i>=0; i--) {
            list = cons(vals[i], list);
        }
        return list;
    }

    public static MimosaList list(List<MimosaType> vals) {
        MimosaList list = nil();
        for(int i=vals.size()-1; i>=0; i--) {
            list = cons(vals.get(i), list);
        }
        return list;
    }

    public static List<MimosaType> toList(MimosaType list) {
        Stack<MimosaType> stack = new Stack<>();
        while(!isNil(list)) {
            stack.push(MimosaListUtil.car(list));
            list = MimosaListUtil.cdr(list);
        }

        List<MimosaType> ret = new ArrayList<>(stack.size());
        while(!stack.isEmpty()) {
            ret.add(stack.pop());
        }
        return ret;
    }

    public static MimosaList nil() {
        return NilPair.INSTANCE;
    }

    public static boolean isNil(MimosaType mimosaType) {
        return mimosaType == NilPair.INSTANCE;
    }

    @Override
    public String toString() {
        if(isNil(cdr())) {
            return "(" + car() + ")";
        } else {
            return "(" + car() + " " + ((MimosaList) cdr()).toFlattenString() + ")";
        }
    }

    private String toFlattenString() {
        if(isNil(cdr())) {
            return car().toString();
        } else {
            return car() + " " + ((MimosaList) cdr()).toFlattenString();
        }
    }

    static class NilPair extends MimosaList {
        public static final NilPair INSTANCE = new NilPair();

        NilPair() {
            super(null, null);
        }

        @Override
        public MimosaType car() {
            throw MimosaPairExtractorException.extractFromEmptyPair();
        }

        @Override
        public MimosaType cdr() {
            throw MimosaPairExtractorException.extractFromEmptyPair();
        }

        @Override
        public String toString() {
            return "'()";
        }
    }
}
