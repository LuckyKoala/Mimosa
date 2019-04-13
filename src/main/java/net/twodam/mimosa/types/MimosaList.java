package net.twodam.mimosa.types;

import net.twodam.mimosa.exceptions.MimosaPairExtractorException;

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

    public static MimosaList list(Stack<MimosaType> valStack) {
        List<MimosaType> reverseList = new ArrayList<>(valStack.size());
        while(!valStack.empty()) {
            reverseList.add(valStack.pop());
        }
        return list(reverseList);
    }

    public static MimosaList list(List<MimosaType> vals) {
        MimosaList list = nil();
        for(int i=vals.size()-1; i>=0; i--) {
            list = cons(vals.get(i), list);
        }
        return list;
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
