package net.twodam.mimosa.parser;

import net.twodam.mimosa.types.*;

import java.util.ArrayList;
import java.util.Stack;

public class Parser {
    public static final String SLIST_CHAR_IN_SVAL = "Can\'t handled \'(\' and \')\' while parsing MimosaVal, try to turn it into escape char.";
    public static final String UNFINISHED_ESCAPE_LITERAL = "Found unfinished escape literal.";
    public static final String UNFINISHED_SLIST = "Found unfinished MimosaList.";

    /**
     * x -> MimosaVal x
     * (x y (a (z 2 ) (a 1) ) ... 1) -> MimosaList (x y (z 2) ... 1)
     * ' ' complete a val
     * '(' increase list level
     * ')' reverse and parse a list
     * '.' indicates a pair which combines exactly two values
     * @param data
     * @return
     */
    public MimosaType parse(char[] data) {
        int listLevel = 0;
        int lastScan = -1;
        int lastParseStart = data.length;
        int lastParseEnd = data.length;
        StringBuilder valBuilder = new StringBuilder();
        Stack<MimosaPair> pairStack = new Stack<>();

        for(char ch : data) {
            lastScan++;

            switch (ch) {
                case '(':
                    if(valBuilder.length() == 0) {
                        listLevel++;
                    } else {
                        throw new ParsingException(SLIST_CHAR_IN_SVAL);
                    }
                    break;
                case ')':
                    //reverse and parse a list
                    if(listLevel > 0) {
                        Stack<MimosaType> stack = new Stack<>();

                        boolean pairDotPresented = false; //Pair (a . b)
                        //MimosaList mimosaList = MimosaList.nil();
                        label:
                        for (int i = lastScan - 1; i >= 0; i--) {
                            if (i == lastParseEnd) {
                                //Handle parsed expr
                                while (pairStack.size() > 0) {
                                    stack.push(pairStack.pop());
                                }
                                i = lastParseStart - 1;
                            }

                            char current = data[i];
                            switch (current) {
                                case ' ':
                                    //try to complete a sval
                                    if (valBuilder.length() > 0) {
                                        MimosaVal mimosaVal = parseMimosaVal(valBuilder.reverse().toString());
                                        stack.push(mimosaVal);
                                        valBuilder.setLength(0);
                                    } else {
                                        //ignore
                                    }
                                    break;
                                case '.':
                                    if(stack.size() == 1) {
                                        pairDotPresented = true;
                                    } else {
                                        throw new ParsingException("Pair combines exactly two values in the form (a . b)," +
                                                " but " + stack.size() + " values occurred after the dot!");
                                    }
                                    break;
                                case '(':
                                    if (valBuilder.length() > 0) {
                                        MimosaVal mimosaVal = parseMimosaVal(valBuilder.reverse().toString());
                                        stack.push(mimosaVal);
                                        valBuilder.setLength(0);
                                    }

                                    //try to complete parsing a mimosa list
                                    if(pairDotPresented) {
                                        if(stack.size() == 2) {
                                            pairStack.push(MimosaPair.cons(stack.pop(), stack.pop()));
                                        } else {
                                            throw new ParsingException("Pair combines exactly two values in the form (a . b)," +
                                                    " but " + stack.size() + " values occurred in total!");
                                        }
                                    } else {
                                        pairStack.push(MimosaList.list(stack));
                                    }
                                    listLevel--;
                                    lastParseEnd = lastScan;
                                    if (i < lastParseStart)
                                        lastParseStart = i; //Only increase size of region of parsed exprs

                                    break label;
                                default:
                                    valBuilder.append(current);
                                    break;
                            }
                        }
                    } else {
                        throw new ParsingException(SLIST_CHAR_IN_SVAL);
                    }
                    break;
                default:
                    break;
            }
        }

        if(listLevel==0) {
            if(pairStack.size() == 0) {
                return parseMimosaVal(new String(data));
            } else if(pairStack.size() == 1) {
                return pairStack.pop();
            } else {
                throw new RuntimeException("The size of PairStack is more than 1, something wrong!");
            }
        } else {
            throw new ParsingException(UNFINISHED_SLIST);
        }
    }

    /**
     * Number / Symbol
     * @param expStr
     * @return
     */
    public MimosaVal parseMimosaVal(String expStr) {
        MimosaVal mimosaVal;
        try {
            mimosaVal = MimosaNumber.numToVal(Integer.valueOf(expStr));
        } catch (NumberFormatException nfe) {
            mimosaVal = MimosaSymbol.strToSymbol(expStr);
        }
        return mimosaVal;
    }
}
