package ast;

import token.Token;

public record IfExpression(Token token, Expression condition, BlockStatement consequence,
                           BlockStatement alternative) implements Expression {

    @SuppressWarnings("unused")
    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("if")
                .append(condition)
                .append(" ")
                .append(consequence);
        if (alternative != null) {
            buffer.append("else ")
                    .append(alternative);
        }
        return String.valueOf(buffer);
    }
}
