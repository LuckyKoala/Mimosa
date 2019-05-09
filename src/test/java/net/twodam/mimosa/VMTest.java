package net.twodam.mimosa;

import net.twodam.mimosa.backend.ir.IRVM;
import net.twodam.mimosa.generators.IREmitter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static net.twodam.mimosa.parser.Parser.parse;
import static org.junit.Assert.assertEquals;

/**
 * Created by luckykoala on 19-5-1.
 */
public class VMTest {
    IREmitter emitter;

    @Before
    public void init() {
        emitter = new IREmitter();
    }

    private int run(String code) {
        emitter.eval(parse(code));
        return IRVM.run(emitter.toSource());
    }

    @Test
    public void constExpr() {
        assertEquals(1, run("1"));
        emitter.clear();
    }

    @Test
    public void variableExpr() {
        assertEquals(1, run("(begin (define x 1) x)"));
        emitter.clear();
    }

    @Test
    public void ifExpr() {
        assertEquals(1, run("(if (= 0 0) 1 0)"));
        emitter.clear();
    }

    @Test
    public void letExpr() {
        assertEquals(0, run("(let ((y 0)) y)"));
        emitter.clear();
    }

    @Test
    public void beginExpr() {
        assertEquals(2, run("(begin (+ 1 2) (+ 1 1))"));
        emitter.clear();
    }

    @Test
    public void lambdaExpr() {
        assertEquals(1, run("((lambda (y) (- y 1)) 2)"));

        //assertEquals(strToSymbol("override-val"), run("((lambda (y) (- y 1) (quote override-val)) 2)"));
        emitter.clear();
    }

    @Test
    public void lambdaInLet() {
        assertEquals(55,
                run("(let ((f (lambda (x) (- x 11)))) " +
                        "           (f (f 77)))"));
        emitter.clear();
    }

    @Test
    public void lambdaAsParam() {
        assertEquals(55,
                run("((lambda (f) (f (f 77))) " +
                        "         (lambda (x) (- x 11)))"));
        emitter.clear();
    }

    @Test
    @Ignore("TODO map infinite symbols to finite registers")
    public void lexicalScope() {
        int result = run("(let ((x 200))" +
                "           (let ((f (lambda (z) (- z x))))" +
                "              (let ((x 100))" +
                "                 (let ((g (lambda (z) (- z x))))" +
                "                    (- (f 1) (g 1))))))");
        assertEquals(-100, result);
        emitter.clear();
    }

    @Test
    public void setExpr() {
        assertEquals(1, run("(begin (define _y 0) (set! _y 1) _y)"));
        emitter.clear();
    }

    /**
     * define 会修改全局环境
     */
    @Test
    public void defineExpr() {
        run("(define _x 1)");
        assertEquals(1, run("_x"));
        run("(define _inc (lambda (x) (+ x 1)))");
        assertEquals(2, run("(_inc 1)"));
        run("(define (_dec x) (- x 1))");
        assertEquals(0, run("(_dec 1)"));
        emitter.clear();
    }

    @Test
    @Ignore("TODO nested define")
    public void doExpr() {
        assertEquals(4, run("(do ((x 0 x)) " +
                "                             ((> x 3) x)" +
                "                             (set! x (+ x 1)))"));
    }

    @Test
    public void programTest() {
        int result = run("(begin" +
                "  (define (iter x i sum)" +
                "    (if (> i x)" +
                "        sum" +
                "        (iter x (+ i 1) (* sum i))))" +
                "  (iter 5 1 1))");
        assertEquals(120, result);
        emitter.clear();
    }
}
