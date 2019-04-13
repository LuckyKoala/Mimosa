package net.twodam.mimosa.exceptions;

public class MimosaParserException extends MimosaException {
    private static final String LIST_CHAR_IN_VAL = "Can\'t handled \'(\' and \')\' while parsing value, try to turn it into escape char.";
    //private static final String UNFINISHED_ESCAPE_LITERAL = "Found unfinished escape literal.";
    private static final String UNFINISHED_PAIR = "Found unfinished pair.";
    private static final String NOT_EXACTLY_TWO_VALUES_IN_PAIR = "Pair combines exactly two values in the form (a . b)," +
            " but %d values occurred after the dot!";
    private static final String MULTI_DOT_IN_PAIR = "More than one dot occurred in pair";

    private MimosaParserException(String message) {
        super(message);
    }

    public static MimosaParserException unfinishedPair() {
        return new MimosaParserException(UNFINISHED_PAIR);
    }

    public static MimosaParserException listCharInVal() {
        return new MimosaParserException(LIST_CHAR_IN_VAL);
    }

    public static MimosaParserException notExactlyTwoValuesInPair(int size) {
        return new MimosaParserException(String.format(NOT_EXACTLY_TWO_VALUES_IN_PAIR, size));
    }

    public static MimosaParserException multiDotInPair() {
        return new MimosaParserException(MULTI_DOT_IN_PAIR);
    }
}
