package net.twodam.mimosa;

import net.twodam.mimosa.evaluator.Environment;
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
    private MimosaType run(String code) {
        return eval(parse(code));
    }

    private MimosaType run(String code, Environment env) {
        return eval(parse(code), env);
    }

    @Test
    public void constExpr() {
        assertEquals(numToVal(1), run("1"));
    }

    @Test
    public void variableExpr() {
        assertEquals(numToVal(1), run("x",
                Environment.extend(Environment.empty(),
                        strToSymbol("x"),
                        MimosaNumber.numToVal(1))));
    }

    @Test
    public void ifExpr() {
        assertEquals(numToVal(1), run("(if (zero? 0) 1 0)"));
    }

    @Test
    public void letExpr() {
        assertEquals(numToVal(0), run("(let ((y 0)) y)"));
    }

    @Test
    public void beginExpr() {
        assertEquals(numToVal(2), run("(begin (+ 1 1) (+ 1 1))"));
    }

    @Test
    public void lambdaExpr() {
        assertEquals(numToVal(1), run("((lambda (y) (- y 1)) 2)"));

        assertEquals(strToSymbol("override-val"), run("((lambda (y) (- y 1) (quote override-val)) 2)"));
    }

    @Test
    public void lambdaInLet() {
        assertEquals(numToVal(55),
                run("(let ((f (lambda (x) (- x 11)))) " +
                        "           (f (f 77)))"));

        assertEquals(numToVal(55),
                run("((lambda (f) (f (f 77))) " +
                        "         (lambda (x) (- x 11)))"));
    }

    @Test
    public void lexicalScope() {
        MimosaType result = run("(let ((x 200))" +
                "           (let ((f (lambda (z) (- z x))))" +
                "              (let ((x 100))" +
                "                 (let ((g (lambda (z) (- z x))))" +
                "                    (- (f 1) (g 1))))))");
        assertEquals(numToVal(-100), result);
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
        assertEquals(numToVal(1), run("(begin (define _y 0) (set! _y 1) _y)"));
    }

    /**
     * define 会修改全局环境
     */
    @Test
    public void defineExpr() {
        run("(define _x 1)");
        assertEquals(numToVal(1), run("_x"));
        run("(define _inc (lambda (x) (+ x 1)))");
        assertEquals(numToVal(2), run("(_inc 1)"));
        run("(define (_dec x) (- x 1))");
        assertEquals(numToVal(0), run("(_dec 1)"));
    }
}
