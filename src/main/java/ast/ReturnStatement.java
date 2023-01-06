package ast;

import token.Token;

public record ReturnStatement(Token token, Expression returnValue) implements Statement {

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(tokenLiteral()).append(" ");
        if (returnValue != null) {
            buffer.append(returnValue);
        }
        buffer.append(";");
        return String.valueOf(buffer);
    }
}
