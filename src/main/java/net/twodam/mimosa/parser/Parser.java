package net.twodam.mimosa.parser;

import net.twodam.mimosa.evaluator.expressions.QuoteExpr;
import net.twodam.mimosa.exceptions.MimosaParserException;
import net.twodam.mimosa.types.*;

import java.util.Stack;

public class Parser {
    enum QuoteMode {
        DISABLED, //正常模式
        ENABLED, //进入quote模式
        VAL, //quote的是MimosaVal
        PAIR //quote的是MimosaPair
    }

    static class State {
        State upperState;
        boolean pairDotPresent;
        QuoteMode quoteMode;
        Stack<MimosaType> tokenStack;

        private State(State upperState) {
            this.upperState = upperState;
            this.pairDotPresent = false;
            this.quoteMode = QuoteMode.DISABLED;
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

        void enableQuoteMode() {
            this.quoteMode = QuoteMode.ENABLED;
        }

        void disableQuoteMode() {
            this.quoteMode = QuoteMode.DISABLED;
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
                case '\'':
                    //'exp => (quote exp)
                    if(tokenBuilder.length() == 0) {
                        state.enableQuoteMode();
                    }

                    break;
                case '(':
                    if(tokenBuilder.length() == 0) {
                        if(QuoteMode.ENABLED == state.quoteMode) {
                            state.quoteMode = QuoteMode.PAIR;
                        }

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

                    if(QuoteMode.PAIR == state.quoteMode) {
                        state.tokenStack.push(QuoteExpr.makeQuote(state.tokenStack.pop()));
                        state.disableQuoteMode();
                    }

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

                        if(QuoteMode.VAL == state.quoteMode) {
                            state.tokenStack.push(QuoteExpr.makeQuote(state.tokenStack.pop()));
                            state.disableQuoteMode();
                        }
                    }

                    break;
                default:
                    tokenBuilder.append(c);
                    if(QuoteMode.ENABLED == state.quoteMode) {
                        state.quoteMode = QuoteMode.VAL;
                    }
                    break;
            }
        }

        if(state.isTopState()) {
            MimosaType ret;
            if(state.tokenStack.size() == 0) {
                if(tokenBuilder.length() > 0) {
                    state.tokenStack.push(parseSingleExpr(tokenBuilder.toString()));
                    tokenBuilder.setLength(0);

                    if(QuoteMode.VAL == state.quoteMode) {
                        state.tokenStack.push(QuoteExpr.makeQuote(state.tokenStack.pop()));
                        state.disableQuoteMode();
                    }
                }
            }

            if(state.tokenStack.size() == 1) {
                ret = state.tokenStack.pop();
            } else {
                throw new RuntimeException("The size of PairStack is more than 1, something wrong!");
            }

            if (QuoteMode.DISABLED == state.quoteMode) {
                return ret;
            } else {
                return QuoteExpr.makeQuote(ret);
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
