//interpreter/Token.java
package token;

import java.util.Map;

import static token.TokenType.*;

public record Token(TokenType type, String literal) {
    private final static Map<String, TokenType> keywords = Map.ofEntries(
            Map.entry("fn", FUNCTION),
            Map.entry("let", LET),
            Map.entry("true", TRUE),
            Map.entry("false", FALSE),
            Map.entry("if", IF),
            Map.entry("else", ELSE),
            Map.entry("return", RETURN)
    );
    private final static Map<Character, TokenType> symbol = Map.ofEntries(
            Map.entry('+', PLUS),
            Map.entry('=', ASSIGN),
            Map.entry('-', MINUS),
            Map.entry('!', BANG),
            Map.entry('*', ASTERISK),
            Map.entry('/', SLASH),
            Map.entry('<', LT),
            Map.entry('>', GT),
            Map.entry(',', COMMA),
            Map.entry(';', SEMICOLON),
            Map.entry('(', LPAREN),
            Map.entry(')', RPAREN),
            Map.entry('{', LBRACE),
            Map.entry('}', RBRACE),
            Map.entry('[', LBRACKET),
            Map.entry(']', RBRACKET),
            Map.entry(':', COLON)
    );

    public static TokenType lookupIdent(String ident) {
        if (keywords.containsKey(ident)) {
            return keywords.get(ident);
        }
        return IDENT;
    }

    public static TokenType lookupSymbol(char ident) {
        if (symbol.containsKey(ident)) {
            return symbol.get(ident);
        }
        return ILLEGAL;
    }

    @Override
    public String toString() {
        return String.format("{Type:%s Literal:%s}", type, literal);
    }
}
