import evaluator.Evaluator;
import lexer.Lexer;
import object.Boolean;
import object.Error;
import object.Integer;
import object.Object;
import object.*;
import parser.Parser;
import org.junit.jupiter.api.Test;


import java.lang.String;
import java.util.Map;

import static evaluator.Evaluator.FALSE;
import static evaluator.Evaluator.TRUE;
import static org.junit.jupiter.api.Assertions.*;

class EvaluatorTest {
    @Test
    void testEvalIntegerExpression() {
        record Temp(String input, int expected) {
        }

        Temp[] tests = new Temp[]{
                new Temp("5", 5),
                new Temp("10", 10),
                new Temp("-5", -5),
                new Temp("-10", -10),
                new Temp("5 + 5 + 5 + 5 - 10", 10),
                new Temp("2 * 2 * 2 * 2 * 2", 32),
                new Temp("-50 + 100 + -50", 0),
                new Temp("5 * 2 + 10", 20),
                new Temp("5 + 2 * 10", 25),
                new Temp("20 + 2 * -10", 0),
                new Temp("50 / 2 * 2 + 10", 60),
                new Temp("2 * (5 + 10)", 30),
                new Temp("3 * 3 * 3 + 10", 37),
                new Temp("3 * (3 * 3) + 10", 37),
                new Temp("(5 + 10 * 2 + 15 / 3) * 2 + -10", 50),
        };
        for (var test : tests) {
            var evaluated = testEval(test.input);
            testIntegerObject(evaluated, test.expected);
        }
    }

    @Test
    void testEvalBooleanExpression() {
        record Temp(String input, boolean expected) {
        }

        Temp[] tests = new Temp[]{
                new Temp("true", true),
                new Temp("false", false),
                new Temp("1 < 2", true),
                new Temp("1 > 2", false),
                new Temp("1 < 1", false),
                new Temp("1 > 1", false),
                new Temp("1 == 1", true),
                new Temp("1 != 1", false),
                new Temp("1 == 2", false),
                new Temp("1 != 2", true),
                new Temp("true == true", true),
                new Temp("false == false", true),
                new Temp("true == false", false),
                new Temp("true != false", true),
                new Temp("false != true", true),
                new Temp("(1 < 2) == true", true),
                new Temp("(1 < 2) == false", false),
                new Temp("(1 > 2) == true", false),
                new Temp("(1 > 2) == false", true),
        };
        for (var test : tests) {
            var evaluated = testEval(test.input);
            testBooleanObject(evaluated, test.expected);
        }
    }

    @Test
    void testBangOperator() {
        record Temp(String input, boolean expected) {
        }

        Temp[] tests = new Temp[]{
                new Temp("!true", false),
                new Temp("!false", true),
                new Temp("!5", false),
                new Temp("!!true", true),
                new Temp("!!false", false),
                new Temp("!!5", true),
        };
        for (var test : tests) {
            var evaluated = testEval(test.input);
            testBooleanObject(evaluated, test.expected);
        }
    }

    @Test
    void testIfElseExpressions() {
        record Temp(String input, java.lang.Object expected) {
        }
        Temp[] tests = new Temp[]{
                new Temp("if (true) { 10 }", 10),
                new Temp("if (false) { 10 }", null),
                new Temp("if (1) { 10 }", 10),
                new Temp("if (1 < 2) { 10 }", 10),
                new Temp("if (1 > 2) { 10 }", null),
                new Temp("if (1 > 2) { 10 } else { 20 }", 20),
                new Temp("if (1 < 2) { 10 } else { 20 }", 10),
        };
        for (var test : tests) {
            var evaluated = testEval(test.input);
            if (test.expected instanceof java.lang.Integer integer) {
                testIntegerObject(evaluated, integer);
            } else {
                testNullObject(evaluated);
            }
        }
    }

    @Test
    void testReturnStatements() {
        record Temp(String input, int expected) {
        }
        Temp[] tests = new Temp[]{
                new Temp("return 10;", 10),
                new Temp("return 10; 9;", 10),
                new Temp("return 2 * 5; 9;", 10),
                new Temp("9; return 2 * 5; 9;", 10),
                new Temp("if (10 > 1) { return 10; }", 10),
                new Temp("""
                        if (10 > 1) {
                          if (10 > 1) {
                            return 10;
                          }
                                                
                          return 1;
                        }
                        """, 10),
                new Temp("""
                        let f = fn(x) {
                          return x;
                          x + 10;
                        };
                        f(10);
                        """, 10),
                new Temp("""
                        let f = fn(x) {
                           let result = x + 10;
                           return result;
                           return 10;
                        };
                        f(10);
                        """, 20),
        };
        for (var test : tests) {
            var evaluated = testEval(test.input);
            testIntegerObject(evaluated, test.expected);
        }
    }

    @Test
    void testErrorHandling() {
        record Temp(String input, String expectedMessage) {
        }
        Temp[] tests = new Temp[]{
                new Temp("5 + true;",
                        "type mismatch: INTEGER + BOOLEAN"),
                new Temp("5 + true; 5;",
                        "type mismatch: INTEGER + BOOLEAN"),
                new Temp("-true",
                        "unknown operator: -BOOLEAN"),
                new Temp("true + false;",
                        "unknown operator: BOOLEAN + BOOLEAN"),
                new Temp("true + false + true + false;",
                        "unknown operator: BOOLEAN + BOOLEAN"),
                new Temp("5; true + false; 5",
                        "unknown operator: BOOLEAN + BOOLEAN"),
                new Temp("foobar",
                        "identifier not found: foobar"),
                new Temp("if (10 > 1) { true + false; }",
                        "unknown operator: BOOLEAN + BOOLEAN"),
                new Temp("""
                        "Hello" - "World"
                        """,
                        "unknown operator: STRING - STRING"),
                new Temp("""
                        if (10 > 1) {
                          if (10 > 1) {
                            return true + false;
                          }
                                                
                          return 1;
                        }
                        """, "unknown operator: BOOLEAN + BOOLEAN"),
        };
        for (var test : tests) {
            var evaluated = testEval(test.input);
            assertTrue(evaluated instanceof Error);
            var errObj = (Error) evaluated;
            assertEquals(test.expectedMessage, errObj.message());
        }
    }

    @Test
    void testLetStatements() {
        record Temp(String input, int expected) {
        }
        Temp[] tests = new Temp[]{
                new Temp("let a = 5; a;", 5),
                new Temp("let a = 5 * 5; a;", 25),
                new Temp("let a = 5; let b = a; b;", 5),
                new Temp("let a = 5; let b = a; let c = a + b + 5; c;", 15),
        };
        for (var test : tests) {
            testIntegerObject(testEval(test.input), test.expected);
        }
    }

    @Test
    void testFunctionObject() {
        String input = "fn(x) { x + 2; };";
        var evaluated = testEval(input);
        assertTrue(evaluated instanceof Function);
        var fn = (Function) evaluated;
        assertEquals(1, fn.parameters().size());
        assertEquals("x", fn.parameters().get(0).toString());
        assertEquals("(x + 2)", fn.body().toString());
    }

    @Test
    void testFunctionApplication() {
        record Temp(String input, int expected) {
        }
        Temp[] tests = new Temp[]{
                new Temp("let identity = fn(x) { x; }; identity(5);", 5),
                new Temp("let identity = fn(x) { return x; }; identity(5);", 5),
                new Temp("let double = fn(x) { x * 2; }; double(5);", 10),
                new Temp("let add = fn(x, y) { x + y; }; add(5, 5);", 10),
                new Temp("let add = fn(x, y) { x + y; }; add(5 + 5, add(5, 5));", 20),
                new Temp("fn(x) { x; }(5)", 5),
        };
        for (var test : tests) {
            testIntegerObject(testEval(test.input), test.expected);
        }
    }

    @Test
    void testEnclosingEnvironments() {
        String input = """
                let first = 10;
                let second = 10;
                let third = 10;
                                
                let ourFunction = fn(first) {
                  let second = 20;
                                
                  first + second + third;
                };
                                
                ourFunction(20) + first + second;
                """;
        testIntegerObject(testEval(input), 70);
    }

    @Test
    void testClosures() {
        var input = """
                let newAdder = fn(x) {
                  fn(y) { x + y };
                };
                                
                let addTwo = newAdder(2);
                addTwo(2);
                """;
        testIntegerObject(testEval(input), 4);
    }

    @Test
    void testStringLiteral() {
        var input = """
                "Hello World!"
                """;
        var evaluated = testEval(input);
        assertTrue(evaluated instanceof object.String);
        var str = (object.String) evaluated;
        assertEquals("Hello World!", str.value());
    }

    @Test
    void testStringConcatenation() {
        var input = """
                "Hello" + " " + "World!"
                """;
        var evaluated = testEval(input);
        assertTrue(evaluated instanceof object.String);
        var str = (object.String) evaluated;
        assertEquals("Hello World!", str.value());
    }

    @Test
    void testBuiltinFunctions() {
        record Temp(String input, java.lang.Object expected) {
        }
        Temp[] tests = {
                new Temp("""
                        len("")
                        """, 0),
                new Temp("""
                        len("four")
                        """, 4),
                new Temp("""
                        len("hello world")
                        """, 11),
                new Temp("""
                        len(1)
                        """, "argument to `len` not supported, got INTEGER"),
                new Temp("""
                        len("one", "two")
                        """, "wrong number of arguments. got=2, want=1"),
                new Temp("""
                        len([1, 2, 3])
                        """, 3),
                new Temp("""
                        len([])
                        """, 0),
                new Temp("""
                        puts("hello", "world!")
                        """,null),
                new Temp("""
                        first([1, 2, 3])
                        """, 1),
                new Temp("""
                        first([])
                        """, null),
                new Temp("""
                        last([1, 2, 3])
                        """, 3),
                new Temp("""
                        last([])
                        """, null),
                new Temp("""
                        last(1)
                        """, "argument to `last` must be ARRAY, got INTEGER"),
                new Temp("""
                        rest([1, 2, 3])
                        """, new int[]{2, 3}),
                new Temp("""
                        rest([])
                        """, null),
                new Temp("""
                        push([], 1)
                        """, new int[]{1}),
                new Temp("""
                        push(1, 1)
                        """, "argument to `push` must be ARRAY, got INTEGER")
        };
        for (var test : tests) {
            var evaluated = testEval(test.input);
            switch (test.expected) {
                case java.lang.Integer actual -> testIntegerObject(evaluated, actual);
                case null -> testNullObject(evaluated);
                case String actual -> {
                    assertTrue(evaluated instanceof Error);
                    var errObj = (Error) evaluated;
                    assertEquals(actual, errObj.message());
                }
                case int[] actual -> {
                    assertTrue(evaluated instanceof Array);
                    Array array = (Array) evaluated;
                    assertEquals(actual.length, array.elements().size());
                    for (int i = 0; i < actual.length; i++) {
                        testIntegerObject(array.elements().get(i), actual[i]);
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + test.expected);
            }
        }
    }

    @Test
    void testArrayLiterals() {
        var input = "[1, 2 * 2, 3 + 3]";
        var evaluated = testEval(input);
        assertTrue(evaluated instanceof Array);
        var array = (Array) evaluated;
        assertEquals(3, array.elements().size());
        testIntegerObject(array.elements().get(0), 1);
        testIntegerObject(array.elements().get(1), 4);
        testIntegerObject(array.elements().get(2), 6);
    }

    @Test
    void testArrayIndexExpression() {
        record Temp(String input, java.lang.Object expected) {
        }
        Temp[] tests = {
                new Temp("[1, 2, 3][0]",
                        1),
                new Temp("[1, 2, 3][1]",
                        2),
                new Temp("[1, 2, 3][2]",
                        3),
                new Temp("let i = 0; [1][i];",
                        1),
                new Temp("[1, 2, 3][1 + 1];",
                        3),
                new Temp("let myArray = [1, 2, 3]; myArray[2];",
                        3),
                new Temp("let myArray = [1, 2, 3]; myArray[0] + myArray[1] + myArray[2];",
                        6),
                new Temp("let myArray = [1, 2, 3]; let i = myArray[0]; myArray[i]",
                        2),
                new Temp("[1, 2, 3][3]",
                        null),
                new Temp("[1, 2, 3][-1]",
                        null),
        };
        for (var test : tests) {
            var evaluated = testEval(test.input);
            if (evaluated instanceof Integer integer) {
                testIntegerObject(evaluated, integer.value());
            } else {
                testNullObject(evaluated);
            }
        }
    }

    @Test
    void testHashLiterals() {
        var input = """
                let two = "two";
                	{
                		"one": 10 - 9,
                		two: 1 + 1,
                		"thr" + "ee": 6 / 2,
                		4: 4,
                		true: 5,
                		false: 6
                	}
                """;
        var evaluated = testEval(input);
        assertTrue(evaluated instanceof Hash);
        var result = (Hash) evaluated;
        Map<HashKey, java.lang.Integer> expected = Map.ofEntries(
                Map.entry(new object.String("one").hashKey(), 1),
                Map.entry(new object.String("two").hashKey(), 2),
                Map.entry(new object.String("three").hashKey(), 3),
                Map.entry(new Integer(4).hashKey(), 4),
                Map.entry(TRUE.hashKey(), 5),
                Map.entry(FALSE.hashKey(), 6)
        );
        assertEquals(expected.size(), result.pairs().size());
        for (var exp : expected.entrySet()) {
            HashKey expectedKey = exp.getKey();
            java.lang.Integer expectedValue = exp.getValue();
            var pair = result.pairs().get(expectedKey);
            assertNotNull(pair);
            testIntegerObject(pair.value(), expectedValue);
        }
    }

    @Test
    void testHashIndexExpressions() {
        record Temp(String input, java.lang.Object expected) {
        }
        Temp[] tests = {
                new Temp("""
                        {"foo": 5}["foo"]
                        """,
                        5),
                new Temp("""
                        {"foo": 5}["bar"]
                        """,
                        null),
                new Temp("""
                        let key = "foo"; {"foo": 5}[key]
                        """, 5),
                new Temp("""
                        {}["foo"]
                        """, null),
                new Temp("""
                        {5: 5}[5]
                        """, 5),
                new Temp("""
                        {true: 5}[true]
                        """, 5),
                new Temp("""
                        {false: 5}[false]
                        """, 5)
        };
        for (var tt : tests) {
            var evaluated = testEval(tt.input);
            if (evaluated instanceof Integer integer) {
                testIntegerObject( evaluated,integer.value());
            } else {
                testNullObject(evaluated);
            }
        }
    }
        private Object testEval (String input){
            var lexer = new Lexer(input);
            var parser = new Parser(lexer);
            var program = parser.parseProgram();
            var env = Environment.newEnvironment();
            return Evaluator.eval(program, env);
        }
        private void testIntegerObject (Object obj,int expected){
            assertTrue(obj instanceof Integer);
            var result = (Integer) obj;
            assertEquals(expected, result.value());
        }

        private void testBooleanObject (Object obj,boolean expected){
            assertTrue(obj instanceof Boolean);
            var result = (Boolean) obj;
            assertEquals(expected, result.value());
        }

        private void testNullObject (Object obj){
            assertTrue(obj instanceof Null);
        }
    }
