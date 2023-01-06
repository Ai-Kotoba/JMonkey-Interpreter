package ast;

import token.Token;

public record StringLiteral(Token token, String value) implements Expression {

    @SuppressWarnings("unused")
    @Override
    public String tokenLiteral() {
        return this.token.literal();
    }

    @Override
    public String toString() {
        return this.token.literal();
    }
}
