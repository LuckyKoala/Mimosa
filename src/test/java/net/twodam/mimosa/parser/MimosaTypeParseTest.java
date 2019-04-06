package net.twodam.mimosa.parser;

import net.twodam.mimosa.types.MimosaType;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static net.twodam.mimosa.types.MimosaList.list;
import static net.twodam.mimosa.types.MimosaNumber.numToVal;
import static net.twodam.mimosa.types.MimosaSymbol.strToSymbol;
import static net.twodam.mimosa.types.MimosaVal.wrap;
import static net.twodam.mimosa.utils.MimosaListUtil.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by luckykoala on 19-4-5.
 */
public class MimosaTypeParseTest {
    private Parser parser;

    @Before
    public void init() {
        this.parser = new Parser();
    }

    //====== Number and Symbol ======
    @Test
    public void number() {
        assertEquals(numToVal(134), parser.parse("134".toCharArray()));
        assertEquals(numToVal(1341241413), parser.parse("+1341241413".toCharArray()));
        assertEquals(numToVal(-9123134), parser.parse("-09123134".toCharArray()));
        assertEquals(wrap("123a"), parser.parse("123a".toCharArray()));
    }

    //====== Pair and list ======
    @Test
    public void pair() {
        String pair = "(a . 3)";
        MimosaType expr = parser.parse(pair.toCharArray());

        assertEquals(wrap("a"), car(expr));
        assertEquals(numToVal(3), cdr(expr));
    }

    @Test
    public void listOperations() {
        MimosaType pair = parser.parse("(a . b)".toCharArray());
        assertEquals(strToSymbol("a"), listRef(pair, numToVal(0)));
        //assertEquals(strToSymbol("b"), listRef(pair, numToVal(1)));

        MimosaType list = parser.parse("(1 2 3 4 (5 6))".toCharArray());
        assertEquals(numToVal(5), length(list));
        assertEquals(numToVal(2), length(car(cddddr(list))));
        assertEquals(
                list(asList(numToVal(1), numToVal(2), numToVal(3), numToVal(4),
                        list(asList(numToVal(5), numToVal(6)))))
                , list);
    }

    @Test
    public void pairExtractor() {
        String lambdaStr = "(lambda (point) (+ point 1))";
        MimosaType expr = parser.parse(lambdaStr.toCharArray());

        assertEquals(strToSymbol("lambda"), car(expr));
        assertEquals(strToSymbol("point"), caadr(expr));
        assertEquals(strToSymbol("+"), car(caddr(expr)));
        assertEquals(strToSymbol("point"), cadr(caddr(expr)));
        assertEquals(numToVal(1), caddr(caddr(expr)));
    }
}
