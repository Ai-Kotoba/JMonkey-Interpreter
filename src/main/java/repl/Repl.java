package repl;

import ast.Program;
import evaluator.Evaluator;
import lexer.Lexer;
import object.Environment;
import object.Object;
import parser.Parser;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class Repl {
    private static final String PROMPT = ">> ";
    private static final String MONKEY_FACE = """
                        __,__
               .--.  .-"     "-.  .--.
              / .. \\/  .-. .-.  \\/ .. \\
             | |  '|  /   Y   \\  |'  | |
             | \\   \\  \\ 0 | 0 /  /   / |
              \\ '- ,\\.-""\"""\""-./, -' /
               ''-' /_   ^ ^   _\\ '-''
                   |  \\._   _./  |
                   \\   \\ '~' /   /
                    '._ '-=-' _.'
                       '-----'
            """;

    public static void start(InputStream in, PrintStream out) {
        Scanner scanner = new Scanner(in);
        Environment env = Environment.newEnvironment();
        while (true) {
            out.print(PROMPT);
            if (!scanner.hasNextLine()) {
                return;
            }
            String line = scanner.nextLine();
            Lexer lexer = new Lexer(line);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();
            if (parser.errors().size() != 0) {
                printParserErrors(System.out, parser.errors());
                continue;
            }
            Object evaluated = Evaluator.eval(program, env);
            if (evaluated != null) {
                out.println(evaluated.inspect());
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static void printParserErrors(PrintStream out, List<String> errors) {
        out.println(MONKEY_FACE);
        out.println("Woops! We ran into some monkey business here!");
        out.println(" parser errors:");
        for (var msg : errors) {
            out.println("\t" + msg);
        }
    }

    public static void main(String[] args) {
        Repl.start(System.in, System.out);
    }
}
