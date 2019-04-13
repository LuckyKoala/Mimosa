package net.twodam.mimosa;

import net.twodam.mimosa.evaluator.Enviroment;
import net.twodam.mimosa.types.MimosaBool;
import net.twodam.mimosa.types.MimosaNumber;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import org.junit.Test;

import static net.twodam.mimosa.evaluator.Evaluator.eval;
import static net.twodam.mimosa.parser.Parser.parse;
import static net.twodam.mimosa.types.MimosaNumber.numToVal;
import static org.junit.Assert.assertEquals;

/**
 * Created by luckykoala on 19-4-8.
 */
public class EvaluatorTest {
    @Test
    public void constExpr() {
        MimosaType expr = parse("1");
        assertEquals(numToVal(1), eval(expr, Enviroment.empty()));
    }

    @Test
    public void symbolExpr() {
        MimosaType expr = parse("x");
        assertEquals(numToVal(1), eval(expr,
                Enviroment.extend(Enviroment.empty(),
                        MimosaSymbol.strToSymbol("x"),
                        MimosaNumber.numToVal(1))));
    }

    @Test
    public void diffExpr() {
        MimosaType expr = parse("(- 2 1)");
        assertEquals(numToVal(1), eval(expr, Enviroment.empty()));
    }

    @Test
    public void zeroPredExpr() {
        MimosaType expr = parse("(zero? 1)");
        assertEquals(MimosaBool.FALSE, eval(expr, Enviroment.empty()));
    }

    @Test
    public void ifExpr() {
        MimosaType expr = parse("(if (zero? 0) 1 0)");
        assertEquals(numToVal(1), eval(expr, Enviroment.empty()));
    }

    @Test
    public void letExpr() {
        MimosaType expr = parse("(let (y 0) y)");
        assertEquals(numToVal(0), eval(expr, Enviroment.empty()));
    }

    @Test
    public void lambdaExpr() {
        MimosaType expr = parse("((lambda y (- y 1)) 2)");
        assertEquals(numToVal(1), eval(expr, Enviroment.empty()));
    }

//    @Test(expected = )
//    public void otherExpr() {
//        String data = "";
//        MimosaPair expr = parser.parse(data.toCharArray());
//        assertEquals(numToVal(1), evaluator.eval(expr, Enviroment.empty()));
//    }
}
