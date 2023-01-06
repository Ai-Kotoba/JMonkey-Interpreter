package ast;

import token.Token;

public record InfixExpression(Token token, Expression left, String operator, Expression right) implements Expression {

    @SuppressWarnings("unused")
    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return "(" + left + " " + operator + " " + right + ")";
    }
}
