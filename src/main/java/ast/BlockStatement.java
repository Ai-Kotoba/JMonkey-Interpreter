package ast;

import token.Token;

import java.util.List;

public record BlockStatement(Token token, List<Statement> statements) implements Statement {

    @SuppressWarnings("unused")
    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for (var s : statements) {
            buffer.append(s);
        }
        return String.valueOf(buffer);
    }
}
