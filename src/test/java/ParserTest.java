import ast.Boolean;
import ast.*;
import lexer.Lexer;
import org.junit.jupiter.api.Test;
import parser.Parser;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    private Program getProgram(String input) {
        var lexer = new Lexer(input);
        var parser = new Parser(lexer);
        var program = parser.parseProgram();
        checkParserErrors(parser);
        assertEquals(1, program.statements().size());
        return program;
    }

    @Test
    void testLetStatements() {
        record Temp(String input, String expectedIdentifier, Object expectedValue) {
        }
        Temp[] tests = {
                new Temp("let x = 5;", "x", 5),
                new Temp("let y = true;", "y", true),
                new Temp("let foobar = y;", "foobar", "y"),
        };
        for (Temp test : tests) {
            Program program = getProgram(test.input);
            var statement = program.statements().get(0);
            testLetStatement(statement, test.expectedIdentifier);
            var letStatement = (LetStatement) statement;
            testLiteralExpression(letStatement.value(), test.expectedValue);
        }
    }


    @Test
    void testReturnStatements() {
        record Temp(String input, Object expectedValue) {
        }
        Temp[] tests = {
                new Temp("return 5;", 5),
                new Temp("return true;", true),
                new Temp("return foobar;", "foobar"),
        };
        for (Temp test : tests) {
            Program program = getProgram(test.input);
            var statement = program.statements().get(0);
            assertTrue(statement instanceof ReturnStatement);
            var returnStatement = (ReturnStatement) statement;
            assertEquals("return", returnStatement.tokenLiteral());
            testLiteralExpression(returnStatement.returnValue(), test.expectedValue);
        }
    }

    @Test
    void testIdentifierExpression() {
        var input = "foobar";
        Program program = getProgram(input);
        assertTrue(program.statements().get(0) instanceof ExpressionStatement);
        var statement = (ExpressionStatement) program.statements().get(0);
        assertTrue(statement.expression() instanceof Identifier);
        var identifier = (Identifier) statement.expression();
        assertEquals("foobar", identifier.value());
        assertEquals("foobar", identifier.tokenLiteral());
    }

    @Test
    void testIntegerLiteralExpression() {
        var input = "5";
        Program program = getProgram(input);
        assertTrue(program.statements().get(0) instanceof ExpressionStatement);
        var statement = (ExpressionStatement) program.statements().get(0);
        assertTrue(statement.expression() instanceof IntegerLiteral);
        var integerLiteral = (IntegerLiteral) statement.expression();
        assertEquals(5, integerLiteral.value());
        assertEquals("5", integerLiteral.tokenLiteral());
    }

    @Test
    void testParsingPrefixExpressions() {
        record PrefixTest(String input, String operator, Object value) {
        }
        PrefixTest[] tests = {
                new PrefixTest("!5;", "!", 5),
                new PrefixTest("-15;", "-", 15),
                new PrefixTest("!foobar;", "!", "foobar"),
                new PrefixTest("-foobar;", "-", "foobar"),
                new PrefixTest("!true;", "!", true),
                new PrefixTest("!false;", "!", false),
        };
        for (PrefixTest test : tests) {
            Program program = getProgram(test.input);
            assertTrue(program.statements().get(0) instanceof ExpressionStatement
                    && ((ExpressionStatement) program.statements().get(0)).expression() instanceof PrefixExpression);
            var expression = (PrefixExpression) ((ExpressionStatement) program.statements().get(0)).expression();
            assertEquals(test.operator, expression.operator());
            testLiteralExpression(expression.right(), test.value);
        }
    }

    @Test
    void testParseInfixExpressions() {
        record InfixTest(String input, Object leftValue, String operator, Object rightValue) {
        }
        InfixTest[] tests = {
                new InfixTest("5 + 5;", 5, "+", 5),
                new InfixTest("5 - 5;", 5, "-", 5),
                new InfixTest("5 * 5;", 5, "*", 5),
                new InfixTest("5 / 5;", 5, "/", 5),
                new InfixTest("5 > 5;", 5, ">", 5),
                new InfixTest("5 < 5;", 5, "<", 5),
                new InfixTest("5 == 5;", 5, "==", 5),
                new InfixTest("5 != 5;", 5, "!=", 5),
                //"barfoo" misspell
                new InfixTest("foobar + barfoo;", "foobar", "+", "barfoo"),
                new InfixTest("foobar - barfoo;", "foobar", "-", "barfoo"),
                new InfixTest("foobar * barfoo;", "foobar", "*", "barfoo"),
                new InfixTest("foobar / barfoo;", "foobar", "/", "barfoo"),
                new InfixTest("foobar > barfoo;", "foobar", ">", "barfoo"),
                new InfixTest("foobar < barfoo;", "foobar", "<", "barfoo"),
                new InfixTest("foobar == barfoo;", "foobar", "==", "barfoo"),
                new InfixTest("foobar != barfoo;", "foobar", "!=", "barfoo"),
                new InfixTest("true == true", true, "==", true),
                new InfixTest("true != false", true, "!=", false),
                new InfixTest("false == false", false, "==", false),
        };
        for (var test : tests) {
            Program program = getProgram(test.input);
            assertTrue(program.statements().get(0) instanceof ExpressionStatement);
            var statement = (ExpressionStatement) program.statements().get(0);
            testInfixExpression(statement.expression(), test.leftValue, test.operator, test.rightValue);
        }
    }

    @Test
    void testOperatorPrecedenceParsing() {
        record Temp(String input, String expected) {
        }
        Temp[] tests = {
                new Temp("-a * b",
                        "((-a) * b)"),
                new Temp("!-a",
                        "(!(-a))"),
                new Temp("a + b + c",
                        "((a + b) + c)"),
                new Temp("a + b - c",
                        "((a + b) - c)"),
                new Temp("a * b * c",
                        "((a * b) * c)"),
                new Temp("a * b / c",
                        "((a * b) / c)"),
                new Temp("a + b / c",
                        "(a + (b / c))"),
                new Temp("a + b * c + d / e - f",
                        "(((a + (b * c)) + (d / e)) - f)"),
                new Temp("3 + 4; -5 * 5",
                        "(3 + 4)((-5) * 5)"),
                new Temp("5 > 4 == 3 < 4",
                        "((5 > 4) == (3 < 4))"),
                new Temp("5 < 4 != 3 > 4",
                        "((5 < 4) != (3 > 4))"),
                new Temp("3 + 4 * 5 == 3 * 1 + 4 * 5",
                        "((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))"),
                new Temp("true",
                        "true"),
                new Temp("false",
                        "false"),
                new Temp("3 > 5 == false",
                        "((3 > 5) == false)"),
                new Temp("3 < 5 == true",
                        "((3 < 5) == true)"),
                new Temp("1 + (2 + 3) + 4",
                        "((1 + (2 + 3)) + 4)"),
                new Temp("(5 + 5) * 2",
                        "((5 + 5) * 2)"),
                new Temp("2 / (5 + 5)",
                        "(2 / (5 + 5))"),
                new Temp("(5 + 5) * 2 * (5 + 5)",
                        "(((5 + 5) * 2) * (5 + 5))"),
                new Temp("-(5 + 5)",
                        "(-(5 + 5))"),
                new Temp("!(true == true)",
                        "(!(true == true))"),
                new Temp("a + add(b * c) + d",
                        "((a + add((b * c))) + d)"),
                new Temp("add(a, b, 1, 2 * 3, 4 + 5, add(6, 7 * 8))",
                        "add(a, b, 1, (2 * 3), (4 + 5), add(6, (7 * 8)))"),
                new Temp("add(a + b + c * d / f + g)",
                        "add((((a + b) + ((c * d) / f)) + g))"),
                new Temp("a * [1, 2, 3, 4][b * c] * d",
                        "((a * ([1, 2, 3, 4][(b * c)])) * d)"),
                new Temp("add(a * b[2], b[1], 2 * [1, 2][1])",
                        "add((a * (b[2])), (b[1]), (2 * ([1, 2][1])))"),
        };
        for (var test : tests) {
            //Test "3 + 4; -5 * 5" contains two statements, cannot use getProgram(),
            //because getProgram() contains "only one statement" assertion.
            var lexer = new Lexer(test.input);
            var parser = new Parser(lexer);
            var program = parser.parseProgram();
            checkParserErrors(parser);
            var actual = program.toString();
            assertEquals(test.expected, actual);
        }
    }

    @Test
    void testBooleanExpression() {
        record Temp(String input, boolean expectedBoolean) {
        }
        Temp[] tests = {
                new Temp("true", true),
                new Temp("false", false),
        };
        for (var test : tests) {
            Program program = getProgram(test.input);
            assertTrue(program.statements().get(0) instanceof ExpressionStatement
                    && ((ExpressionStatement) program.statements().get(0)).expression() instanceof Boolean);
            var bool = (Boolean) ((ExpressionStatement) program.statements().get(0)).expression();
            assertEquals(test.expectedBoolean, bool.value());
        }
    }

    @Test
    void testIfExpression() {
        var input = "if (x < y) { x }";
        var program = getProgram(input);
        assertTrue(program.statements().get(0) instanceof ExpressionStatement
                && ((ExpressionStatement) program.statements().get(0)).expression() instanceof IfExpression);
        var expression = (IfExpression) ((ExpressionStatement) program.statements().get(0)).expression();
        testInfixExpression(expression.condition(), "x", "<", "y");
        assertEquals(1, expression.consequence().statements().size());
        assertTrue(expression.consequence().statements().get(0) instanceof ExpressionStatement);
        var consequence = (ExpressionStatement) expression.consequence().statements().get(0);
        testIdentifier(consequence.expression(), "x");
        assertNull(expression.alternative());
    }

    @Test
    void testIfElseExpression() {
        var input = "if (x < y) { x } else { y }";
        var program = getProgram(input);
        assertTrue(program.statements().get(0) instanceof ExpressionStatement
                && ((ExpressionStatement) program.statements().get(0)).expression() instanceof IfExpression);
        var expression = (IfExpression) ((ExpressionStatement) program.statements().get(0)).expression();
        testInfixExpression(expression.condition(), "x", "<", "y");
        assertEquals(1, expression.consequence().statements().size());
        assertTrue(expression.consequence().statements().get(0) instanceof ExpressionStatement);
        var consequence = (ExpressionStatement) expression.consequence().statements().get(0);
        testIdentifier(consequence.expression(), "x");
        assertEquals(1, expression.consequence().statements().size());
        assertTrue(expression.alternative().statements().get(0) instanceof ExpressionStatement);
        var alternative = (ExpressionStatement) expression.alternative().statements().get(0);
        testIdentifier(alternative.expression(), "y");
    }

    @Test
    void testFunctionLiteralParsing() {
        var input = "fn(x, y) { x + y; }";
        var program = getProgram(input);
        assertTrue(program.statements().get(0) instanceof ExpressionStatement
                && ((ExpressionStatement) program.statements().get(0)).expression() instanceof FunctionLiteral);
        var function = (FunctionLiteral) ((ExpressionStatement) program.statements().get(0)).expression();
        assertEquals(2, function.parameters().size());
        testLiteralExpression(function.parameters().get(0), "x");
        testLiteralExpression(function.parameters().get(1), "y");
        assertEquals(1, function.body().statements().size());
        assertTrue(function.body().statements().get(0) instanceof ExpressionStatement);
        var body = (ExpressionStatement) function.body().statements().get(0);
        testInfixExpression(body.expression(), "x", "+", "y");
    }

    @Test
    void testFunctionParameterParsing() {
        record Temp(String input, List<String> expectedParams) {
        }
        Temp[] tests = {
                new Temp("fn() {};", List.of()),
                new Temp("fn(x) {};", List.of("x")),
                new Temp("fn(x, y, z) {};", List.of("x", "y", "z")),
        };
        for (var test : tests) {
            Program program = getProgram(test.input);
            assertTrue(program.statements().get(0) instanceof ExpressionStatement
                    && ((ExpressionStatement) program.statements().get(0)).expression() instanceof FunctionLiteral);
            var function = (FunctionLiteral) ((ExpressionStatement) program.statements().get(0)).expression();
            assertEquals(test.expectedParams.size(), function.parameters().size());
            for (var i = 0; i < test.expectedParams.size(); i++) {
                var ident = test.expectedParams.get(i);
                testLiteralExpression(function.parameters().get(i), ident);
            }
        }
    }

    @Test
    void testCallExpressionParsing() {
        var input = "add(1, 2 * 3, 4 + 5);";
        var program = getProgram(input);
        assertTrue(program.statements().get(0) instanceof ExpressionStatement
                && ((ExpressionStatement) program.statements().get(0)).expression() instanceof CallExpression);
        var expression = (CallExpression) ((ExpressionStatement) program.statements().get(0)).expression();
        testIdentifier(expression.function(), "add");
        assertEquals(3, expression.arguments().size());
        testLiteralExpression(expression.arguments().get(0), 1);
        testInfixExpression(expression.arguments().get(1), 2, "*", 3);
        testInfixExpression(expression.arguments().get(2), 4, "+", 5);
    }

    @Test
    void testCallExpressionParameterParsing() {
        record Temp(String input, String expectedIdent, List<String> expectedArgs) {
        }
        Temp[] tests = {
                new Temp("add();", "add", List.of()),
                new Temp("ask(1);", "ask", List.of("1")),
                new Temp("add(1, 2 * 3, 4 + 5);", "add", List.of("1", "(2 * 3)", "(4 + 5)")),
        };
        for (var test : tests) {
            Program program = getProgram(test.input);
            assertTrue(program.statements().get(0) instanceof ExpressionStatement
                    && ((ExpressionStatement) program.statements().get(0)).expression() instanceof CallExpression);
            var expression = (CallExpression) ((ExpressionStatement) program.statements().get(0)).expression();
            testIdentifier(expression.function(), test.expectedIdent);
            assertEquals(test.expectedArgs.size(), expression.arguments().size());
            for (var i = 0; i < test.expectedArgs.size(); i++) {
                var arg = test.expectedArgs.get(i);
                assertEquals(arg, expression.arguments().get(i).toString());
            }
        }
    }

    @Test
    void testStringLiteralExpression() {
        var input = """
                "hello world";
                """;
        var program = getProgram(input);
        var stmt = program.statements().get(0);
        assertTrue(stmt instanceof ExpressionStatement);
        var expression = (ExpressionStatement) stmt;
        assertTrue(expression.expression() instanceof StringLiteral);
        var literal = (StringLiteral) expression.expression();
        assertEquals("hello world", literal.value());
    }

    @Test
    void testParsingEmptyArrayLiterals() {
        var input = "[]";
        var program = getProgram(input);
        assertTrue(program.statements().get(0) instanceof ExpressionStatement);
        var stmt = (ExpressionStatement) program.statements().get(0);
        assertTrue(stmt.expression() instanceof ArrayLiteral);
        var array = (ArrayLiteral) stmt.expression();
        assertEquals(0, array.elements().size());
    }

    @Test
    void TestParsingArrayLiterals() {
        var input = "[1, 2 * 2, 3 + 3]";
        var program = getProgram(input);
        assertTrue(program.statements().get(0) instanceof ExpressionStatement);
        var stmt = (ExpressionStatement) program.statements().get(0);
        assertTrue(stmt.expression() instanceof ArrayLiteral);
        var array = (ArrayLiteral) stmt.expression();
        assertEquals(3, array.elements().size());
        testIntegerLiteral(array.elements().get(0), 1);
        testInfixExpression(array.elements().get(1), 2, "*", 2);
        testInfixExpression(array.elements().get(2), 3, "+", 3);
    }

    @Test
    void testParsingIndexExpressions() {
        var input = "myArray[1 + 1]";
        var program = getProgram(input);
        assertTrue(program.statements().get(0) instanceof ExpressionStatement);
        var stmt = (ExpressionStatement) program.statements().get(0);
        assertTrue(stmt.expression() instanceof IndexExpression);
        var indexExp = (IndexExpression) stmt.expression();
        testIdentifier(indexExp.left(), "myArray");
        testInfixExpression(indexExp.index(), 1, "+", 1);
    }

    @Test
    void testParsingEmptyHashLiteral() {
        var input = "{}";
        var program = getProgram(input);
        assertTrue(program.statements().get(0) instanceof ExpressionStatement);
        var stmt = (ExpressionStatement) program.statements().get(0);
        assertTrue(stmt.expression() instanceof HashLiteral);
        var hash = (HashLiteral) stmt.expression();
        assertEquals(0, hash.pairs().size());
    }

    @Test
    void testParsingHashLiteralsStringKeys() {
        var input = """
                {"one": 1, "two": 2, "three": 3}
                """;
        var program = getProgram(input);
        assertTrue(program.statements().get(0) instanceof ExpressionStatement);
        var stmt = (ExpressionStatement) program.statements().get(0);
        assertTrue(stmt.expression() instanceof HashLiteral);
        var hash = (HashLiteral) stmt.expression();
        Map<String, Integer> expected = Map.of("one", 1, "two", 2, "three", 3);
        assertEquals(expected.size(), hash.pairs().size());
        for (var entry : hash.pairs().entrySet()) {
            assertTrue(entry.getKey() instanceof StringLiteral);
            StringLiteral literal = (StringLiteral) entry.getKey();
            var expectedValue = expected.get(literal.toString());
            testIntegerLiteral(entry.getValue(), expectedValue);
        }
    }

    @Test
    void testParsingHashLiteralsBooleanKeys() {
        var input = """
                {true: 1, false: 2}
                """;
        var program = getProgram(input);
        assertTrue(program.statements().get(0) instanceof ExpressionStatement);
        var stmt = (ExpressionStatement) program.statements().get(0);
        assertTrue(stmt.expression() instanceof HashLiteral);
        var hash = (HashLiteral) stmt.expression();
        Map<String, Integer> expected = Map.of("true", 1, "false", 2);
        assertEquals(expected.size(), hash.pairs().size());
        for (var entry : hash.pairs().entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            assertTrue(key instanceof Boolean);
            Boolean literal = (Boolean) entry.getKey();
            var expectedValue = expected.get(literal.toString());
            testIntegerLiteral(value, expectedValue);
        }
    }

    @Test
    void testParsingHashLiteralsIntegerKeys() {
        var input = """
                {1: 1, 2: 2, 3: 3}
                """;
        var program = getProgram(input);
        assertTrue(program.statements().get(0) instanceof ExpressionStatement);
        var stmt = (ExpressionStatement) program.statements().get(0);
        assertTrue(stmt.expression() instanceof HashLiteral);
        var hash = (HashLiteral) stmt.expression();
        Map<String, Integer> expected = Map.of("1", 1, "2", 2, "3", 3);
        assertEquals(expected.size(), hash.pairs().size());
        for (var entry : hash.pairs().entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            assertTrue(key instanceof IntegerLiteral);
            IntegerLiteral literal = (IntegerLiteral) entry.getKey();
            var expectedValue = expected.get(literal.toString());
            testIntegerLiteral(value, expectedValue);
        }
    }

    @Test
    void testParsingHashLiteralsWithExpressions() {
        var input = """
                {"one": 0 + 1, "two": 10 - 8, "three": 15 / 5}
                """;
        var program = getProgram(input);
        assertTrue(program.statements().get(0) instanceof ExpressionStatement);
        var stmt = (ExpressionStatement) program.statements().get(0);
        assertTrue(stmt.expression() instanceof HashLiteral);
        var hash = (HashLiteral) stmt.expression();
        Map<String, Consumer<Expression>> tests = Map.of(
                "one",
                e -> testInfixExpression(e, 0, "+", 1),
                "two",
                e -> testInfixExpression(e, 10, "-", 8),
                "three",
                e -> testInfixExpression(e, 15, "/", 5)
        );
        for (var entry : hash.pairs().entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            assertTrue(key instanceof StringLiteral);
            StringLiteral literal = (StringLiteral) entry.getKey();
            var testFunc = tests.get(literal.toString());
            assertNotNull(testFunc);
            testFunc.accept(value);
        }
    }

    void testLetStatement(Statement statement, String name) {
        assertTrue(statement instanceof LetStatement);
        LetStatement letStatement = (LetStatement) statement;
        assertEquals(letStatement.name().value(), name);
        assertEquals(letStatement.name().tokenLiteral(), name);
    }

    void testInfixExpression(Expression expression, Object left, String operator, Object right) {
        assertTrue(expression instanceof InfixExpression);
        InfixExpression infixExpression = (InfixExpression) expression;
        testLiteralExpression(infixExpression.left(), left);
        assertEquals(infixExpression.operator(), operator);
        testLiteralExpression(infixExpression.right(), right);
    }

    void testLiteralExpression(Expression expression, Object expected) {
        //switch in java 17 preview
        switch (expected) {
            case Integer i -> testIntegerLiteral(expression, i);
            case java.lang.Boolean b -> testBooleanLiteral(expression, b);
            case String s -> testIdentifier(expression, s);
            default -> fail("type of exp not handled. got=%s".formatted(expression));
        }
    }

    private void testIntegerLiteral(Expression expression, Integer value) {
        assertTrue(expression instanceof IntegerLiteral);
        IntegerLiteral integerLiteral = (IntegerLiteral) expression;
        assertEquals(value, integerLiteral.value());
        assertEquals(Integer.toString(value), integerLiteral.tokenLiteral());
    }

    private void testBooleanLiteral(Expression expression, java.lang.Boolean value) {
        //class-name conflict
        assertTrue(expression instanceof Boolean);
        Boolean booleanLiteral = (Boolean) expression;
        assertEquals(value, booleanLiteral.value());
        assertEquals(java.lang.Boolean.toString(value), booleanLiteral.tokenLiteral());
    }

    private void testIdentifier(Expression expression, String value) {
        assertTrue(expression instanceof Identifier);
        Identifier identifier = (Identifier) expression;
        assertEquals(value, identifier.value());
        assertEquals(value, identifier.tokenLiteral());
    }

    private void checkParserErrors(Parser parser) {
        var errors = parser.errors();
        assertEquals(0, errors.size(), () -> {
            var message = new StringBuilder("parser has %d errors".formatted(errors.size()));
            for (var msg : errors) {
                message.append("\n").append(msg);
            }
            return message.toString();
        });
    }
}
