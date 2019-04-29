package net.twodam.mimosa;

import net.twodam.mimosa.types.MimosaType;
import org.junit.Test;

import static net.twodam.mimosa.evaluator.expressions.QuoteExpr.makeQuote;
import static net.twodam.mimosa.parser.Parser.parse;
import static net.twodam.mimosa.types.MimosaList.list;
import static net.twodam.mimosa.types.MimosaNumber.numToVal;
import static net.twodam.mimosa.types.MimosaPair.cons;
import static net.twodam.mimosa.types.MimosaSymbol.strToSymbol;
import static net.twodam.mimosa.utils.MimosaListUtil.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by luckykoala on 19-4-5.
 */
public class ParserTest {
    //====== Number and Symbol ======
    @Test
    public void primitive() {
        assertEquals(numToVal(134), parse("134"));
        assertEquals(numToVal(+134), parse("+134"));
        assertEquals(numToVal(-134), parse("-134"));
        assertEquals(strToSymbol("134a"), parse("134a"));
    }

    //====== Pair and list ======
    @Test
    public void pair() {
        MimosaType expect = cons(strToSymbol("a"), numToVal(3));
        MimosaType actual = parse("(a . 3)");

        assertEquals(expect, actual);
        assertEquals(strToSymbol("a"), car(actual));
        assertEquals(numToVal(3), cdr(actual));
    }

    @Test
    public void listTest() {
        MimosaType expect = list(list(numToVal(1)),
                numToVal(2),
                list(numToVal(3), list(numToVal(4))));
        MimosaType actual = parse("((1) 2 (3 (4)))");

        assertEquals(expect, actual);
    }

    @Test
    public void listOperations() {
        MimosaType list = parse("(1 2 3 4 (5 6) 7)");
        assertEquals(numToVal(6), length(list));
        assertEquals(numToVal(2), length(car(cddddr(list))));
        assertEquals(list(numToVal(5), numToVal(6))
                , listRef(list, numToVal(4)));
    }

    @Test
    public void quote() {
        assertEquals(makeQuote(numToVal(1)), parse("'1"));
        assertEquals(makeQuote(list(numToVal(1), numToVal(2))), parse("'(1 2)"));
        assertEquals(list(strToSymbol("f"),
                makeQuote(list(strToSymbol("a"), numToVal(2), numToVal(3)))),
                parse("(f '(a 2 3))"));
    }
}
