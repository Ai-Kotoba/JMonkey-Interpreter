package ast;

import token.Token;

public record PrefixExpression(Token token, String operator, Expression right) implements Expression {

    @SuppressWarnings("unused")
    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return "(" + operator + right + ")";
    }
}
