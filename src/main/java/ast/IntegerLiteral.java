package ast;

import token.Token;

public record IntegerLiteral(Token token, int value) implements Expression {

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return token.literal();
    }
}
