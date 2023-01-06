package ast;

import token.Token;

public record Boolean(Token token, boolean value) implements Expression {
    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return token.literal();
    }
}
