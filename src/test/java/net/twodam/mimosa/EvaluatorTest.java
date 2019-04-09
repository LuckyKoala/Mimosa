package net.twodam.mimosa;

import net.twodam.mimosa.evaluator.Enviroment;
import net.twodam.mimosa.types.MimosaBool;
import net.twodam.mimosa.types.MimosaNumber;
import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
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
        String data = "1";
        MimosaPair expr = parse(data);
        assertEquals(numToVal(1), eval(expr, Enviroment.empty()));
    }

    @Test
    public void symbolExpr() {
        String data = "x";
        MimosaPair expr = parse(data);
        assertEquals(numToVal(1), eval(expr,
                Enviroment.extend(Enviroment.empty(),
                        MimosaSymbol.strToSymbol("x"),
                        MimosaNumber.numToVal(1))));
    }

    @Test
    public void diffExpr() {
        String data = "(- 2 1)";
        MimosaPair expr = parse(data);
        assertEquals(numToVal(1), eval(expr, Enviroment.empty()));
    }

    @Test
    public void zeroPredExpr() {
        String data = "(zero? 1)";
        MimosaPair expr = parse(data);
        assertEquals(MimosaBool.FALSE, eval(expr, Enviroment.empty()));
    }

    @Test
    public void ifExpr() {
        String data = "(if (zero? 0) 1 0)";
        MimosaPair expr = parse(data);
        assertEquals(numToVal(1), eval(expr, Enviroment.empty()));
    }

    @Test
    public void letExpr() {
        String data = "(let (y 0) (if (zero? y) 1 0))";
        MimosaPair expr = parse(data);
        assertEquals(numToVal(1), eval(expr, Enviroment.empty()));
    }

//    @Test(expected = )
//    public void otherExpr() {
//        String data = "";
//        MimosaPair expr = parser.parse(data.toCharArray());
//        assertEquals(numToVal(1), evaluator.eval(expr, Enviroment.empty()));
//    }
}
