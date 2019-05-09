package net.twodam.mimosa.parser;

import net.twodam.mimosa.backend.expressions.QuoteExpr;
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

        void trySwitchQuoteModeTo(QuoteMode newQuoteMode) {
            if(QuoteMode.ENABLED == quoteMode) {
                quoteMode = newQuoteMode;
            }
        }
    }

    public static MimosaType parse(String data) {
        StringBuilder tokenBuilder = new StringBuilder();
        State state = State.topState();
        boolean escapeModeEnabled = false;

        for(int i=0; i<data.length(); i++) {
            char c = data.charAt(i);

            if(escapeModeEnabled) {
                fillTokenBuilder(tokenBuilder, state, c);
                escapeModeEnabled = false;
                continue;
            }

            switch (c) {
                case '\\':
                    escapeModeEnabled = true;
                    break;

                case '(':
                    if(tokenBuilder.length() == 0) {
                        state.trySwitchQuoteModeTo(QuoteMode.PAIR);
                        state = state.nextState(); //new state for deeper depth
                    } else {
                        throw MimosaParserException.listCharInVal();
                    }
                    break;

                case ')':
                    if(state.isTopState()) throw MimosaParserException.unfinishedPair();

                    tryParseToken(tokenBuilder, state);
                    state = state.mergeToUpperState(); //finish this state, merge data to upper state

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
                    tryParseToken(tokenBuilder, state);
                    break;

                case '\'':
                    //'exp => (quote exp)
                    if(tokenBuilder.length() == 0 && QuoteMode.DISABLED == state.quoteMode) {
                        state.enableQuoteMode();
                        break;
                    }

                    //fall through to default case
                default:
                    fillTokenBuilder(tokenBuilder, state, c);
                    break;
            }
        }

        if(state.isTopState()) {
            MimosaType ret;
            if(state.tokenStack.size() == 0) {
                tryParseToken(tokenBuilder, state);
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

    private static void fillTokenBuilder(StringBuilder tokenBuilder, State state, char c) {
        tokenBuilder.append(c);
        state.trySwitchQuoteModeTo(QuoteMode.VAL);
    }

    private static void tryParseToken(StringBuilder tokenBuilder, State state) {
        //Push possibly remain token
        if (tokenBuilder.length() > 0) {
            state.tokenStack.push(parseSingleExpr(tokenBuilder.toString()));
            tokenBuilder.setLength(0);

            if (QuoteMode.VAL == state.quoteMode) {
                state.tokenStack.push(QuoteExpr.makeQuote(state.tokenStack.pop()));
                state.disableQuoteMode();
            }
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
