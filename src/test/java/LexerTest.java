import lexer.Lexer;
import token.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static token.TokenType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


class LexerTest {
    Expected[] tests;
    private Lexer lexer;

    @BeforeEach()
    void init() {
        tests = new Expected[]{
                new Expected(LET, "let"),
                new Expected(IDENT, "five"),
                new Expected(ASSIGN, "="),
                new Expected(INT, "5"),
                new Expected(SEMICOLON, ";"),
                new Expected(LET, "let"),
                new Expected(IDENT, "ten"),
                new Expected(ASSIGN, "="),
                new Expected(INT, "10"),
                new Expected(SEMICOLON, ";"),
                new Expected(LET, "let"),
                new Expected(IDENT, "add"),
                new Expected(ASSIGN, "="),
                new Expected(FUNCTION, "fn"),
                new Expected(LPAREN, "("),
                new Expected(IDENT, "x"),
                new Expected(COMMA, ","),
                new Expected(IDENT, "y"),
                new Expected(RPAREN, ")"),
                new Expected(LBRACE, "{"),
                new Expected(IDENT, "x"),
                new Expected(PLUS, "+"),
                new Expected(IDENT, "y"),
                new Expected(SEMICOLON, ";"),
                new Expected(RBRACE, "}"),
                new Expected(SEMICOLON, ";"),
                new Expected(LET, "let"),
                new Expected(IDENT, "result"),
                new Expected(ASSIGN, "="),
                new Expected(IDENT, "add"),
                new Expected(LPAREN, "("),
                new Expected(IDENT, "five"),
                new Expected(COMMA, ","),
                new Expected(IDENT, "ten"),
                new Expected(RPAREN, ")"),
                new Expected(SEMICOLON, ";"),
                new Expected(BANG, "!"),
                new Expected(MINUS, "-"),
                new Expected(SLASH, "/"),
                new Expected(ASTERISK, "*"),
                new Expected(INT, "5"),
                new Expected(SEMICOLON, ";"),
                new Expected(INT, "5"),
                new Expected(LT, "<"),
                new Expected(INT, "10"),
                new Expected(GT, ">"),
                new Expected(INT, "5"),
                new Expected(SEMICOLON, ";"),
                new Expected(IF, "if"),
                new Expected(LPAREN, "("),
                new Expected(INT, "5"),
                new Expected(LT, "<"),
                new Expected(INT, "10"),
                new Expected(RPAREN, ")"),
                new Expected(LBRACE, "{"),
                new Expected(RETURN, "return"),
                new Expected(TRUE, "true"),
                new Expected(SEMICOLON, ";"),
                new Expected(RBRACE, "}"),
                new Expected(ELSE, "else"),
                new Expected(LBRACE, "{"),
                new Expected(RETURN, "return"),
                new Expected(FALSE, "false"),
                new Expected(SEMICOLON, ";"),
                new Expected(RBRACE, "}"),
                new Expected(INT, "10"),
                new Expected(EQ, "=="),
                new Expected(INT, "10"),
                new Expected(SEMICOLON, ";"),
                new Expected(INT, "10"),
                new Expected(NOT_EQ, "!="),
                new Expected(INT, "9"),
                new Expected(SEMICOLON, ";"),
                new Expected(FUNCTION, "fn"),
                new Expected(LPAREN, "("),
                new Expected(IDENT, "x"),
                new Expected(RPAREN, ")"),
                new Expected(LBRACE, "{"),
                new Expected(IDENT, "x"),
                new Expected(SEMICOLON, ";"),
                new Expected(RBRACE, "}"),
                new Expected(LPAREN, "("),
                new Expected(INT, "5"),
                new Expected(RPAREN, ")"),
                new Expected(STRING, "foobar"),
                new Expected(STRING,"foo bar"),
                new Expected(LBRACKET, "["),
                new Expected(INT, "1"),
                new Expected(COMMA, ","),
                new Expected(INT, "2"),
                new Expected(RBRACKET, "]"),
                new Expected(SEMICOLON, ";"),
                new Expected(LBRACE, "{"),
                new Expected(STRING, "foo"),
                new Expected(COLON, ":"),
                new Expected(STRING, "bar"),
                new Expected(RBRACE, "}"),
                new Expected(EOF, ""),
        };
        String input = """
                let five = 5;
                let ten = 10;
                                
                let add = fn(x, y) {
                  x + y;
                };
                                
                let result = add(five, ten);
                !-/*5;
                5 < 10 > 5;
                                
                if (5 < 10) {
                	return true;
                } else {
                	return false;
                }
                                
                10 == 10;
                10 != 9;
                fn(x) { x; }(5)
                "foobar"
                "foo bar"
                [1, 2];
                {"foo": "bar"}
                """;
        lexer = new Lexer(input);
    }

    @Test
    void nextToken() {
        for (Expected test : tests) {
            var tok = lexer.nextToken();
            assertEquals(test.expectedType(), tok.type());
            assertEquals(test.expectedLiteral(), tok.literal());
        }
    }

    record Expected(TokenType expectedType, String expectedLiteral) {
    }
}