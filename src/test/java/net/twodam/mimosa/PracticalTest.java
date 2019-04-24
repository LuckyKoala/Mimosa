package net.twodam.mimosa;

import net.twodam.mimosa.types.MimosaBool;
import org.junit.Test;

import static net.twodam.mimosa.evaluator.Evaluator.eval;
import static net.twodam.mimosa.parser.Parser.parse;
import static org.junit.Assert.assertEquals;

public class PracticalTest {
    @Test
    public void recursiveEven() {
        eval(parse("(define (_even? x)" +
                "  ((lambda (even? odd?)" +
                "     (even? even? odd? x))" +
                "            (lambda (ev? od? n)" +
                "     (if (= n 0)" +
                "            #t" +
                "            (od? ev? od? (- n 1))))" +
                "            (lambda (ev? od? n)" +
                "     (if (= n 0)" +
                "            #f" +
                "            (ev? ev? od? (- n 1))))))"));

        assertEquals(MimosaBool.FALSE, eval(parse("(_even? 1)")));
        assertEquals(MimosaBool.TRUE, eval(parse("(_even? 2)")));
        assertEquals(MimosaBool.FALSE, eval(parse("(_even? 13)")));
        //FIXME this will lead to StackOverflowError
        //assertEquals(MimosaBool.TRUE, eval(parse("(_even? 2334)")));
    }
}
