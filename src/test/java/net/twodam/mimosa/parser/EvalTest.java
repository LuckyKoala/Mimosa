package net.twodam.mimosa.parser;

import net.twodam.mimosa.evaluator.Enviroment;
import net.twodam.mimosa.evaluator.Evaluator;
import net.twodam.mimosa.types.MimosaNumber;
import net.twodam.mimosa.types.MimosaPair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by luckykoala on 19-4-8.
 */
public class EvalTest {
    private Parser parser;
    private Evaluator evaluator;

    @Before
    public void init() {
        this.parser = new Parser();
        this.evaluator = new Evaluator();
    }

    @Test
    public void test() {
        String data = "(- 2 1)";
        MimosaPair expr = parser.parse(data.toCharArray());
        assertEquals(MimosaNumber.numToVal(1), evaluator.eval(expr, Enviroment.empty()));
    }
}
