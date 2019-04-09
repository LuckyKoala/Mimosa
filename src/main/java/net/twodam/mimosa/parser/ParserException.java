package net.twodam.mimosa.parser;

public class ParserException extends RuntimeException {
    private static final String LIST_CHAR_IN_VAL = "Can\'t handled \'(\' and \')\' while parsing value, try to turn it into escape char.";
    //private static final String UNFINISHED_ESCAPE_LITERAL = "Found unfinished escape literal.";
    private static final String UNFINISHED_PAIR = "Found unfinished pair.";
    private static final String NOT_EXACTLY_TWO_VALUES_IN_PAIR = "Pair combines exactly two values in the form (a . b)," +
            " but %d values occurred after the dot!";
    private static final String MULTI_DOT_IN_PAIR = "More than one dot occurred in pair";

    private ParserException(String message) {
        super(message);
    }

    public static ParserException unfinishedPair() {
        return new ParserException(UNFINISHED_PAIR);
    }

    public static ParserException listCharInVal() {
        return new ParserException(LIST_CHAR_IN_VAL);
    }

    public static ParserException notExactlyTwoValuesInPair(int size) {
        return new ParserException(String.format(NOT_EXACTLY_TWO_VALUES_IN_PAIR, size));
    }

    public static ParserException multiDotInPair() {
        return new ParserException(MULTI_DOT_IN_PAIR);
    }
}
