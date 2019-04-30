package net.twodam.mimosa;

import net.twodam.mimosa.exceptions.MimosaException;
import net.twodam.mimosa.generators.IREmitter;
import net.twodam.mimosa.types.MimosaType;

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
    private static final String DEBUG = "Debug> ";
    private static final String ERROR = "Error> ";
    private static final String IR_PREFIX = "(IR) ";

    public static void main(String[] args) {
        IR();
    }

    private static void REPL() {
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n\n");
        String input;

        do {
            System.out.println(PROMFT);
            input = scanner.next();
            input = input.replace('\n', ' ');
            if(input.equalsIgnoreCase("exit")) {
                System.out.println("Bye :)");
                break;
            }

            try {
                MimosaType parsedExpr = parse(input);
                System.out.println(eval(parsedExpr));
            } catch (MimosaException e) {
                System.out.println(ERROR + e.getMessage());
            }
        } while(true);
    }

    private static void IR() {
        IREmitter emitter = new IREmitter();
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n\n");
        String input;

        do {
            System.out.println(IR_PREFIX + PROMFT);
            input = scanner.next();
            input = input.replace('\n', ' ');
            if(input.equalsIgnoreCase("exit")) {
                System.out.println("Bye :)");
                break;
            }

            try {
                MimosaType parsedExpr = parse(input);
                emitter.eval(parsedExpr);

                System.out.println(emitter);
                emitter.clear();
            } catch (MimosaException e) {
                System.out.println(IR_PREFIX + ERROR + e.getMessage());
            }
        } while(true);
    }
}
