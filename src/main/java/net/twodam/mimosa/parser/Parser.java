package net.twodam.mimosa.parser;

import net.twodam.mimosa.exceptions.MimosaParserException;
import net.twodam.mimosa.types.*;

import java.util.Stack;

public class Parser {
    static class State {
        State upperState;
        int startBound; //index of '('
        int parsedStart;
        int parsedEnd;
        boolean pairDotPresent;
        Stack<MimosaType> tokenStack;
        Stack<MimosaPair> parsedExprStack;

        private State(State upperState, int startBound) {
            this.upperState = upperState;
            this.startBound = startBound;
            this.parsedStart = Integer.MAX_VALUE;
            this.parsedEnd = startBound;
            this.pairDotPresent = false;
            this.tokenStack = new Stack<>();
            this.parsedExprStack = new Stack<>();
        }

        static State topStateOf(int startBound) {
            return new State(null, startBound);
        }

        boolean isTopState() {
            return upperState == null;
        }

        State nextState(int startBound) {
            return new State(this, startBound);
        }

        State mergeToUpperState(int parsedEnd) {
            if(pairDotPresent) {
                if(tokenStack.size() == 2) {
                    upperState.parsedExprStack.push(MimosaPair.cons(tokenStack.pop(), tokenStack.pop()));
                } else {
                    throw MimosaParserException.notExactlyTwoValuesInPair(tokenStack.size());
                }
            } else {
                upperState.parsedExprStack.push(MimosaList.list(tokenStack));
            }

            if(startBound < upperState.parsedStart) upperState.parsedStart = startBound;
            upperState.parsedEnd = parsedEnd;
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
        State state = State.topStateOf(0);

        for(int i=0; i<data.length(); i++) {
            char c = data.charAt(i);

            switch (c) {
                case '(':
                    if(tokenBuilder.length() == 0) {
                        state = state.nextState(i); //new state for deeper depth
                    } else {
                        throw MimosaParserException.listCharInVal();
                    }
                    break;

                case ')':
                    if(state.isTopState()) throw MimosaParserException.unfinishedPair();
                    //Parse remain chars
                    for(int j = i-1; j>state.startBound; j--) {
                        //Skip parsed chars
                        if(j == state.parsedEnd) {
                            //Save parsed expr
                            while(!state.parsedExprStack.empty()) {
                                state.tokenStack.push(state.parsedExprStack.pop());
                            }
                            j = state.parsedStart - 1;
                            if(j <= state.startBound) {
                                //All chars in this level has been parsed
                                break;
                            }
                        }

                        char ch = data.charAt(j);

                        switch (ch) {
                            case '(':
                            case ')':
                                //This should never happened
                                throw new RuntimeException("Parser error");
                            case '.':
                                if (!state.pairDotPresent) {
                                    state.pairDotPresent = true;
                                } else {
                                    throw MimosaParserException.multiDotInPair();
                                }
                                break;
                            case ' ':
                                if(tokenBuilder.length() > 0) {
                                    state.tokenStack.push(parseSingleExpr(tokenBuilder.reverse().toString()));
                                    tokenBuilder.setLength(0);
                                }

                                break;
                            default:
                                tokenBuilder.append(ch);
                                break;
                        }
                    }
                    //Push possibly remain token
                    if(tokenBuilder.length() > 0) {
                        state.tokenStack.push(parseSingleExpr(tokenBuilder.reverse().toString()));
                        tokenBuilder.setLength(0);
                    }
                    state = state.mergeToUpperState(i);

                    break;
            }
        }

        if(state.isTopState()) {
            int parsedExprStackSize = state.parsedExprStack.size();
            if(parsedExprStackSize == 0) {
                return parseSingleExpr(data);
            } else if(parsedExprStackSize == 1) {
                return state.parsedExprStack.pop();
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
