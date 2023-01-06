package ast;

import token.Token;

public record Identifier(Token token, String value) implements Expression {

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return value;
    }
}
