package net.twodam.mimosa;

import net.twodam.mimosa.evaluator.Enviroment;

import java.util.Scanner;

import static net.twodam.mimosa.evaluator.Evaluator.eval;
import static net.twodam.mimosa.parser.Parser.parse;

/**
 * REPL
 *
 * Created by luckykoala on 19-4-5.
 */
public class Core {
    private static final String PROMFT = "REPL> ";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;

        do {
            System.out.println(PROMFT);
            input = scanner.nextLine();
            if(input.equalsIgnoreCase("exit")) {
                System.out.println("Bye :)");
                break;
            }

            System.out.println(eval(parse(input), Enviroment.empty()));
        } while(true);
    }
}
