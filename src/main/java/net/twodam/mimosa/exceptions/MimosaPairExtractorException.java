package net.twodam.mimosa.exceptions;

public class MimosaPairExtractorException extends MimosaException {
    private static final String EXTRACT_FROM_EMPTY_PAIR_MESSAGE = "Can't extract from empty pair, aka nil";

    private MimosaPairExtractorException(String message) {
        super(message);
    }

    public static MimosaPairExtractorException extractFromEmptyPair() {
        return new MimosaPairExtractorException(EXTRACT_FROM_EMPTY_PAIR_MESSAGE);
    }
}
