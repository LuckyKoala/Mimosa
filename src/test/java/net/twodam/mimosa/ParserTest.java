package net.twodam.mimosa;

import net.twodam.mimosa.types.MimosaType;
import org.junit.Test;

import static java.util.Arrays.asList;
import static net.twodam.mimosa.parser.Parser.parse;
import static net.twodam.mimosa.types.MimosaList.list;
import static net.twodam.mimosa.types.MimosaNumber.numToVal;
import static net.twodam.mimosa.types.MimosaSymbol.strToSymbol;
import static net.twodam.mimosa.utils.MimosaListUtil.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by luckykoala on 19-4-5.
 */
public class ParserTest {
    //====== Number and Symbol ======
    @Test
    public void number() {
        assertEquals(numToVal(134), parse("134"));
        assertEquals(numToVal(+134), parse("+134"));
        assertEquals(numToVal(-134), parse("-134"));
        assertEquals(strToSymbol("134a"), parse("134a"));
    }

    //====== Pair and list ======
    @Test
    public void pair() {
        MimosaType expr = parse("(a . 3)");

        assertEquals(strToSymbol("a"), car(expr));
        assertEquals(numToVal(3), cdr(expr));
    }

    @Test
    public void listOperations() {
        MimosaType expr = parse("(a . b)");
        assertEquals(strToSymbol("a"), listRef(expr, numToVal(0)));
        //assertEquals(SymbolExpr.TAG, listRef(pair, numToVal(1)));

        MimosaType list = parse("(1 2 3 4 (5 6))");
        assertEquals(numToVal(5), length(list));
        assertEquals(numToVal(2), length(car(cddddr(list))));
        assertEquals(
                list(asList(numToVal(1),
                        numToVal(2),
                        numToVal(3),
                        numToVal(4),
                        list(asList(numToVal(5), numToVal(6)))))
                , list);
    }

    @Test
    public void pairExtractor() {
        String lambdaStr = "(lambda (point) (+ point 1))";
        MimosaType expr = parse(lambdaStr);

        assertEquals(strToSymbol("lambda"), car(expr));
        assertEquals(strToSymbol("point"), caadr(expr));
        assertEquals(strToSymbol("+"), car(caddr(expr)));
        assertEquals(strToSymbol("point"), cadr(caddr(expr)));
        assertEquals(numToVal(1), caddr(caddr(expr)));
    }
}
