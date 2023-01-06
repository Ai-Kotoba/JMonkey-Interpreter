//interpreter/TokenType.java
package token;

public enum TokenType {
    ILLEGAL,
    EOF,
    // Identifiers + literals
    IDENT,
    INT,
    STRING,
    // Operators
    ASSIGN, //"+"
    EQ, // "=="
    NOT_EQ, // "!="
    PLUS,  // "*"
    MINUS,  // "-"
    BANG, // "!"
    ASTERISK, // "*"
    SLASH, // "/"
    LT, // "<"
    GT, // ">"
    // Delimiters
    COMMA, // ","
    SEMICOLON, // ";"
    COLON, // ":"
    LPAREN, // "("
    RPAREN,// ")"
    LBRACE, // "{"
    RBRACE, // "}
    LBRACKET, // "["
    RBRACKET, // "]"
    // Keywords
    FUNCTION,
    LET,
    TRUE,
    FALSE,
    IF,
    ELSE,
    RETURN
}
