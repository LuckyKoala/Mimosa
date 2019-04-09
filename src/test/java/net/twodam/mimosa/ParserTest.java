package net.twodam.mimosa;

import net.twodam.mimosa.evaluator.expressions.*;
import net.twodam.mimosa.types.MimosaPair;
import org.junit.Test;

import static java.util.Arrays.asList;
import static net.twodam.mimosa.parser.Parser.parse;
import static net.twodam.mimosa.types.MimosaList.list;
import static net.twodam.mimosa.types.MimosaNumber.numToVal;
import static net.twodam.mimosa.types.MimosaSymbol.strToSymbol;
import static net.twodam.mimosa.utils.MimosaListUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by luckykoala on 19-4-5.
 */
public class ParserTest {
    //====== Number and Symbol ======
    @Test
    public void number() {
        assertEquals(ConstExpr.wrap(numToVal(134)), parse("134"));
        assertEquals(ConstExpr.wrap(numToVal(+134)), parse("+134"));
        assertEquals(ConstExpr.wrap(numToVal(-134)), parse("-134"));
        assertEquals(SymbolExpr.wrap(strToSymbol("134a")), parse("134a"));
    }

    //====== Pair and list ======
    @Test
    public void pair() {
        String pair = "(a . 3)";
        MimosaPair expr = parse(pair);

        assertEquals(SymbolExpr.wrap(strToSymbol("a")), car(expr));
        assertEquals(ConstExpr.wrap(numToVal(3)), cdr(expr));
    }

    @Test
    public void listOperations() {
        MimosaPair pair = parse("(a . b)");
        assertEquals(SymbolExpr.wrap(strToSymbol("a")), listRef(pair, numToVal(0)));
        //assertEquals(SymbolExpr.TAG, listRef(pair, numToVal(1)));

        MimosaPair list = parse("(1 2 3 4 (5 6))");
        assertEquals(numToVal(5), length(list));
        assertEquals(numToVal(2), length(car(cddddr(list))));
        assertEquals(
                list(asList(ConstExpr.wrap(numToVal(1)),
                        ConstExpr.wrap(numToVal(2)),
                        ConstExpr.wrap(numToVal(3)),
                        ConstExpr.wrap(numToVal(4)),
                        list(asList(ConstExpr.wrap(numToVal(5)),
                                ConstExpr.wrap(numToVal(6))))))
                , list);
    }

    @Test
    public void pairExtractor() {
        String lambdaStr = "(lambda (point) (+ point 1))";
        MimosaPair expr = parse(lambdaStr);

        assertEquals(SymbolExpr.wrap(strToSymbol("lambda")), car(expr));
        assertEquals(SymbolExpr.wrap(strToSymbol("point")), caadr(expr));
        assertEquals(SymbolExpr.wrap(strToSymbol("+")), car(caddr(expr)));
        assertEquals(SymbolExpr.wrap(strToSymbol("point")), cadr(caddr(expr)));
        assertEquals(ConstExpr.wrap(numToVal(1)), caddr(caddr(expr)));
    }

    @Test
    public void expressions() {
        String testStr = "(let (y 1) (if (zero? (- y 1)) 1 0))";

        final MimosaPair letExpr = parse(testStr);
        assertTrue(LetExpr.check(letExpr));
        assertEquals(strToSymbol("y"), LetExpr.bindingKey(letExpr));
        assertEquals(ConstExpr.wrap(numToVal(1)), LetExpr.bindingValue(letExpr));

        final MimosaPair ifExpr = LetExpr.body(letExpr);
        assertTrue(IfExpr.check(ifExpr));
        assertEquals(ConstExpr.wrap(numToVal(1)), IfExpr.trueExpr(ifExpr));
        assertEquals(ConstExpr.wrap(numToVal(0)), IfExpr.falseExpr(ifExpr));

        final MimosaPair zeroExpr = IfExpr.predicate(ifExpr);
        assertTrue(ZeroPredExpr.check(zeroExpr));

        final MimosaPair diffExpr = ZeroPredExpr.predicate(zeroExpr);
        assertTrue(DiffExpr.check(diffExpr));
        assertEquals(SymbolExpr.wrap(strToSymbol("y")), DiffExpr.exp1(diffExpr));
        assertEquals(ConstExpr.wrap(numToVal(1)), DiffExpr.exp2(diffExpr));
    }
}
