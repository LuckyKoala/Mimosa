package net.twodam.mimosa;

import net.twodam.mimosa.types.MimosaBool;
import org.junit.Test;

import static net.twodam.mimosa.evaluator.Evaluator.eval;
import static net.twodam.mimosa.parser.Parser.parse;
import static net.twodam.mimosa.types.MimosaList.list;
import static net.twodam.mimosa.types.MimosaNumber.numToVal;
import static net.twodam.mimosa.types.MimosaPair.cons;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by luckykoala on 19-4-20.
 */
public class RuntimeTest {
    @Test
    public void quoteTest() {
        assertTrue(MimosaBool.isTrue(eval(parse("(quote #t)"))));
        assertTrue(!MimosaBool.isTrue(eval(parse("(quote #f)"))));
    }

    @Test
    public void pairTest() {
        assertEquals(cons(numToVal(1), numToVal(2)), eval(parse("(cons 1 2)")));
        assertEquals(numToVal(1), eval(parse("(car (cons 1 2))")));
        assertEquals(numToVal(2), eval(parse("(cdr (cons 1 2))")));
    }

    @Test
    public void listOperationTest() {
        assertEquals(list(numToVal(1), numToVal(2), numToVal(3)), eval(parse("(list 1 2 3)")));
        assertEquals(numToVal(1), eval(parse("(car (list 1 2 3))")));
        assertEquals(list(numToVal(2), numToVal(3)), eval(parse("(cdr (list 1 2 3))")));
        assertEquals(numToVal(3), eval(parse("(length (list 1 2 3))")));
        assertEquals(numToVal(2), eval(parse("(list-ref (list 1 2 3) 1)")));
        assertEquals(list(numToVal(1), numToVal(2), numToVal(3)), eval(parse("(append (list 1 2) (list 3))")));
    }

    @Test
    public void listIterationTest() {
        assertEquals(list(numToVal(2), numToVal(3), numToVal(4)), eval(parse("(map (lambda (v) (+ v 1)) (list 1 2 3))")));
        assertEquals(numToVal(6), eval(parse("(foldl (lambda (i v) (+ i v)) 0 (list 1 2 3))")));
    }

    @Test
    public void booleanOperationTest() {
        assertEquals(MimosaBool.FALSE, eval(parse("(not #t)")));
        assertEquals(MimosaBool.TRUE, eval(parse("(not #f)")));

        assertEquals(MimosaBool.TRUE, eval(parse("(and #t #t)")));
        assertEquals(MimosaBool.FALSE, eval(parse("(and #t #f)")));
        assertEquals(MimosaBool.FALSE, eval(parse("(and #f #t)")));
        assertEquals(MimosaBool.FALSE, eval(parse("(and #f #f)")));

        assertEquals(MimosaBool.TRUE, eval(parse("(or #t #t)")));
        assertEquals(MimosaBool.TRUE, eval(parse("(or #t #f)")));
        assertEquals(MimosaBool.TRUE, eval(parse("(or #f #t)")));
        assertEquals(MimosaBool.FALSE, eval(parse("(or #f #f)")));

        assertEquals(MimosaBool.FALSE, eval(parse("(xor #t #t)")));
        assertEquals(MimosaBool.TRUE, eval(parse("(xor #t #f)")));
        assertEquals(MimosaBool.TRUE, eval(parse("(xor #f #t)")));
        assertEquals(MimosaBool.FALSE, eval(parse("(xor #f #f)")));
    }

    @Test
    public void typePredicateTest() {
        assertEquals(MimosaBool.TRUE, eval(parse("(null? (quote ()))")));
        assertEquals(MimosaBool.TRUE, eval(parse("(symbol? (quote x))")));
        assertEquals(MimosaBool.TRUE, eval(parse("(number? (quote 3))")));
        assertEquals(MimosaBool.TRUE, eval(parse("(pair? (quote ()))")));
        assertEquals(MimosaBool.TRUE, eval(parse("(pair? (quote (x . y)))")));
        assertEquals(MimosaBool.TRUE, eval(parse("(pair? (quote (x y z)))")));

        assertEquals(MimosaBool.TRUE, eval(parse("(eq? (quote 1) (quote 1))")));
        assertEquals(MimosaBool.TRUE, eval(parse("(eq? (quote (a c)) (quote (a c)))")));
        assertEquals(MimosaBool.FALSE, eval(parse("(eq? (quote (a c)) (quote (a b)))")));
    }

    @Test
    public void systemTest() {
        //
    }
}
