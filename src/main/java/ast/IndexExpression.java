package ast;

import token.Token;

public record IndexExpression(Token token, Expression left, Expression index) implements Expression {

    @SuppressWarnings("unused")
    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return "("
                + left
                + "["
                + index
                + "])";
    }
}
