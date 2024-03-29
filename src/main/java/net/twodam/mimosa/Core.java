package net.twodam.mimosa;

import net.twodam.mimosa.backend.ir.IRVM;
import net.twodam.mimosa.exceptions.MimosaException;
import net.twodam.mimosa.generators.IREmitter;
import net.twodam.mimosa.types.MimosaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import static net.twodam.mimosa.backend.Evaluator.eval;
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
        if(args.length < 1) {
            help();
        } else {
            String mode = args[0];
            if(mode.equalsIgnoreCase("repl")) {
                REPL();
            } else if(mode.equalsIgnoreCase("ir")) {
                if(args.length >= 2) {
                    String fileName = args[1];
                    try {
                        List<String> codes = Files.readAllLines(Paths.get(fileName));
                        StringBuilder builder = new StringBuilder();
                        codes.forEach(builder::append);

                        IREmitter emitter = new IREmitter();
                        MimosaType parsedExpr = parse(builder.toString());
                        emitter.eval(parsedExpr);
                        System.out.println(emitter);
                        int result = IRVM.run(emitter.toSource());
                        emitter.clear();
                        System.out.println(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    IR();
                }
            } else {
                System.out.println("不支持的模式！");
                help();
            }
        }
    }

    private static void help() {
        System.out.println("=== MimosaCompiler Help ===");
        System.out.println("mimosa repl 以REPL模式启动");
        System.out.println("mimosa ir filename 以IR模式启动");
        System.out.println("===========================");
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
                int result = IRVM.run(emitter.toSource());
                emitter.clear();

                System.out.println(result);
            } catch (MimosaException e) {
                System.out.println(IR_PREFIX + ERROR + e.getMessage());
            }
        } while(true);
    }
}
