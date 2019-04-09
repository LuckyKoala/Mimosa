package net.twodam.mimosa.parser;

public class PairExtractorException extends RuntimeException {
    private static final String EXTRACT_FROM_EMPTY_PAIR_MESSAGE = "Can't extract from empty pair, aka nil";

    private PairExtractorException(String message) {
        super(message);
    }

    public static PairExtractorException extractFromEmptyPair() {
        return new PairExtractorException(EXTRACT_FROM_EMPTY_PAIR_MESSAGE);
    }
}
