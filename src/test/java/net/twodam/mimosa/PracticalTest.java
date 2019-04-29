package net.twodam.mimosa;

import net.twodam.mimosa.types.MimosaBool;
import net.twodam.mimosa.types.MimosaNumber;
import net.twodam.mimosa.types.MimosaType;
import org.junit.Test;

import static net.twodam.mimosa.evaluator.Evaluator.eval;
import static net.twodam.mimosa.parser.Parser.parse;
import static org.junit.Assert.assertEquals;

public class PracticalTest {
    private MimosaType run(String data) {
        return eval(parse(data));
    }

    @Test
    public void recursiveEven() {
        run("(define (_even? x)" +
                "  ((lambda (even? odd?)" +
                "     (even? even? odd? x))" +
                "            (lambda (ev? od? n)" +
                "     (if (= n 0)" +
                "            #t" +
                "            (od? ev? od? (- n 1))))" +
                "            (lambda (ev? od? n)" +
                "     (if (= n 0)" +
                "            #f" +
                "            (ev? ev? od? (- n 1))))))");

        assertEquals(MimosaBool.FALSE, run("(_even? 1)"));
        assertEquals(MimosaBool.TRUE, run("(_even? 2)"));
        assertEquals(MimosaBool.FALSE, run("(_even? 13)"));
        //FIXME this will lead to StackOverflowError
        //assertEquals(MimosaBool.TRUE, eval(parse("(_even? 2334)")));
    }

    @Test
    public void factorial() {
        run("(define (_factorialIter x i sum)" +
                "   (if (> i x)" +
                "       sum" +
                "       (_factorialIter x (+ i 1) (* sum i))))");
        run("(define (_factorial x)" +
                "   (_factorialIter x 1 1))");
        assertEquals(MimosaNumber.numToVal(1), run("(_factorial 0)"));
        assertEquals(MimosaNumber.numToVal(1), run("(_factorial 1)"));
        assertEquals(MimosaNumber.numToVal(120), run("(_factorial 5)"));
    }
}
