//interpreter/Lexer.java
package lexer;

import token.Token;
import token.TokenType;

import static token.TokenType.*;
import static java.lang.Character.*;

public class Lexer {
    private final String input;
    private int position;// current position in input (points to current char)
    private int readPosition;// current reading position in input (after current char)
    private char ch;// current char under examination

    public Lexer(String input) {
        this.input = input;
        readChar();
    }

    public Token nextToken() {
        Token tok;
        skipWhitespace();
        switch (ch) {
            case '+', '-', '/', '*', '<', '>', ';', ',', '{', '}', '(', ')', '[', ']', ':' ->
                    tok = newToken(Token.lookupSymbol(ch), ch);
            case '"' -> tok = new Token(STRING, readString());
            case '=' -> {
                if (peekChar() == '=') {
                    readChar();
                    tok = new Token(EQ, "==");
                } else {
                    tok = newToken(Token.lookupSymbol(ch), ch);
                }
            }
            case '!' -> {
                if (peekChar() == '=') {
                    readChar();
                    tok = new Token(NOT_EQ, "!=");
                } else {
                    tok = newToken(Token.lookupSymbol(ch), ch);
                }
            }
            case 0 -> tok = new Token(EOF, "");
            default -> {
                if (isIdent(ch)) {
                    String literal = readIdentifier();
                    tok = new Token(Token.lookupIdent(literal), literal);
                    return tok;
                } else if (isDigit(ch)) {
                    String literal = readNumber();
                    tok = new Token(INT, literal);
                    return tok;
                } else {
                    tok = newToken(ILLEGAL, ch);
                }
            }
        }
        //Necessary, at this time ch is not a blank character, skipWhitespace() will read the processed characters.
        readChar();
        return tok;
    }

    private void skipWhitespace() {
        while (isWhitespace(ch)) {
            readChar();
        }
    }

    private void readChar() {
        if (readPosition >= input.length()) {
            ch = 0;
        } else {
            ch = input.charAt(readPosition);
        }
        position = readPosition;
        ++readPosition;
    }

    private char peekChar() {
        if (readPosition >= input.length()) {
            return 0;
        } else {
            return input.charAt(readPosition);
        }
    }

    private String readIdentifier() {
        int position = this.position;
        while (isIdent(ch)) {
            readChar();
        }
        return input.substring(position, this.position);
    }

    private String readNumber() {
        int position = this.position;
        while (isDigit(ch)) {
            readChar();
        }
        return input.substring(position, this.position);
    }

    private String readString() {
        int position = this.position + 1;
        do {
            readChar();
        } while (ch != '"' && ch != 0);
        return input.substring(position, this.position);
    }

    private boolean isIdent(char ch) {
        return isLetter(ch) || ch == '_';
    }

    private Token newToken(TokenType tokenType, char ch) {
        return new Token(tokenType, Character.toString(ch));
    }
}
