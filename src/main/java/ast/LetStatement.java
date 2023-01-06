package ast;

import token.Token;

public record LetStatement(Token token, Identifier name, Expression value) implements Statement {
    @Override
    public String tokenLiteral() {
        return token.literal();
    }


    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(tokenLiteral())
                .append(" ")
                .append(name)
                .append(" = ");
        if (value != null) {
            buffer.append(value);
        }
        buffer.append(";");
        return String.valueOf(buffer);
    }
}
