package ast;

import token.Token;

public record ExpressionStatement(Token token, Expression expression) implements Statement {

    @SuppressWarnings("unused")
    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return expression.toString();
    }
}
