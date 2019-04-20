package net.twodam.mimosa.parser;

import net.twodam.mimosa.exceptions.MimosaParserException;
import net.twodam.mimosa.types.*;

import java.util.Stack;

public class Parser {
    static class State {
        State upperState;
        boolean pairDotPresent;
        Stack<MimosaType> tokenStack;

        private State(State upperState) {
            this.upperState = upperState;
            this.pairDotPresent = false;
            this.tokenStack = new Stack<>();
        }

        static State topState() {
            return new State(null);
        }

        boolean isTopState() {
            return upperState == null;
        }

        State nextState() {
            return new State(this);
        }

        MimosaPair merge() {
            if(pairDotPresent) {
                if(tokenStack.size() == 2) {
                    return MimosaPair.cons(tokenStack.elementAt(0), tokenStack.elementAt(1));
                } else {
                    throw MimosaParserException.notExactlyTwoValuesInPair(tokenStack.size());
                }
            } else {
                return MimosaList.list(tokenStack.toArray(new MimosaType[0]));
            }
        }

        State mergeToUpperState() {
            upperState.tokenStack.push(merge());

            return upperState;
        }
    }

    /**
     * 1 -> const-expr 1
     * (x y (a (z 2 ) (a 1) 1) ... 1) -> MimosaList (x y (z 2) ... 1)
     * ' ' complete a val
     * '(' increase list level
     * ')' reverse and parse a list
     * '.' indicates a pair which combines exactly two values
     * @return
     */
    public static MimosaType parse(String data) {
        StringBuilder tokenBuilder = new StringBuilder();
        State state = State.topState();

        for(int i=0; i<data.length(); i++) {
            char c = data.charAt(i);

            switch (c) {
                case '(':
                    if(tokenBuilder.length() == 0) {
                        state = state.nextState(); //new state for deeper depth
                    } else {
                        throw MimosaParserException.listCharInVal();
                    }
                    break;

                case ')':
                    if(state.isTopState()) throw MimosaParserException.unfinishedPair();

                    //Push possibly remain token
                    if(tokenBuilder.length() > 0) {
                        state.tokenStack.push(parseSingleExpr(tokenBuilder.toString()));
                        tokenBuilder.setLength(0);
                    }

                    //merge
                    state = state.mergeToUpperState();

                    break;
                case '.':
                    if (!state.pairDotPresent) {
                        state.pairDotPresent = true;
                    } else {
                        throw MimosaParserException.multiDotInPair();
                    }
                    break;
                case ' ':
                    if(tokenBuilder.length() > 0) {
                        state.tokenStack.push(parseSingleExpr(tokenBuilder.toString()));
                        tokenBuilder.setLength(0);
                    }

                    break;
                default:
                    tokenBuilder.append(c);
                    break;
            }
        }

        if(state.isTopState()) {
            int tokenSize = state.tokenStack.size();
            if(tokenSize == 0) {
                return parseSingleExpr(data);
            } else if(tokenSize == 1) {
                return state.tokenStack.pop();
            } else {
                throw new RuntimeException("The size of PairStack is more than 1, something wrong!");
            }
        } else {
            throw MimosaParserException.unfinishedPair();
        }
    }

    /**
     * Wrap Number / Symbol into single special body
     */
    private static MimosaType parseSingleExpr(String expStr) {
        try {
            return MimosaNumber.numToVal(Integer.valueOf(expStr));
        } catch (NumberFormatException nfe) {
            return MimosaSymbol.strToSymbol(expStr);
        }
    }
}
