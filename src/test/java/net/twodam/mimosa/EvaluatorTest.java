package net.twodam.mimosa;

import net.twodam.mimosa.evaluator.Environment;
import net.twodam.mimosa.types.MimosaBool;
import net.twodam.mimosa.types.MimosaNumber;
import net.twodam.mimosa.types.MimosaType;
import org.junit.Test;

import static net.twodam.mimosa.evaluator.Evaluator.eval;
import static net.twodam.mimosa.parser.Parser.parse;
import static net.twodam.mimosa.types.MimosaNumber.numToVal;
import static net.twodam.mimosa.types.MimosaSymbol.strToSymbol;
import static org.junit.Assert.assertEquals;

/**
 * Created by luckykoala on 19-4-8.
 */
public class EvaluatorTest {
    @Test
    public void constExpr() {
        MimosaType expr = parse("1");
        assertEquals(numToVal(1), eval(expr));
    }

    @Test
    public void symbolExpr() {
        MimosaType expr = parse("x");
        assertEquals(numToVal(1), eval(expr,
                Environment.extend(Environment.empty(),
                        strToSymbol("x"),
                        MimosaNumber.numToVal(1))));
    }

    @Test
    public void primitiveFunction() {
        assertEquals(numToVal(1), eval(parse("(+ 2 (- 1))")));
        assertEquals(numToVal(1), eval(parse("(- 2 1)")));
        assertEquals(MimosaBool.FALSE, eval(parse("(zero? 1)")));
        assertEquals(MimosaBool.TRUE, eval(parse("(zero? 0)")));
    }

    @Test
    public void ifExpr() {
        MimosaType expr = parse("(if (zero? 0) 1 0)");
        assertEquals(numToVal(1), eval(expr));
    }

    @Test
    public void letExpr() {
        MimosaType expr = parse("(let ((y 0)) y)");
        assertEquals(numToVal(0), eval(expr));
    }

    @Test
    public void beginExpr() {
        MimosaType expr = parse("(begin (+ 1 1) (+ 1 1))");
        assertEquals(numToVal(2), eval(expr));
    }

    @Test
    public void lambdaExpr() {
        assertEquals(numToVal(1), eval(parse("((lambda (y) (- y 1)) 2)")));

        assertEquals(strToSymbol("override-val"), eval(parse("((lambda (y) (- y 1) (quote override-val)) 2)")));
    }

    @Test
    public void lambdaInLet() {
        assertEquals(numToVal(55),
                eval(parse("(let ((f (lambda (x) (- x 11)))) " +
                        "           (f (f 77)))")));

        assertEquals(numToVal(55),
                eval(parse("((lambda (f) (f (f 77))) " +
                        "         (lambda (x) (- x 11)))")));
    }

    @Test
    public void lexicalScope() {
        MimosaType parsedExpr = parse("(let ((x 200))" +
                "           (let ((f (lambda (z) (- z x))))" +
                "              (let ((x 100))" +
                "                 (let ((g (lambda (z) (- z x))))" +
                "                    (- (f 1) (g 1))))))");
        MimosaType evaluatedExpr = eval(parsedExpr);
        assertEquals(numToVal(-100), evaluatedExpr);
    }

    /**
     * (define x 0)
     *  * (let (x 2)
     *  *   x
     *  *   (set! x 1)
     *  *   x)
     *  * x
     */
    @Test
    public void setExpr() {
        assertEquals(numToVal(1), eval(parse("(begin (define _y 0) (set! _y 1) _y)")));
    }

    /**
     * define 会修改全局环境
     */
    @Test
    public void defineExpr() {
        eval(parse("(define _x 1)"));
        assertEquals(numToVal(1), eval(parse("_x")));
        eval(parse("(define _inc (lambda (x) (+ x 1)))"));
        assertEquals(numToVal(2), eval(parse("(_inc 1)")));
        eval(parse("(define (_dec x) (- x 1))"));
        assertEquals(numToVal(0), eval(parse("(_dec 1)")));
    }
}
