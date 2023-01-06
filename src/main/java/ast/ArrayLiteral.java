package ast;

import token.Token;

import java.util.List;

public record ArrayLiteral(Token token, List<Expression> elements) implements Expression {

    @SuppressWarnings("unused")
    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        String[] args = elements.stream()
                .map(Object::toString)
                .toArray(String[]::new);
        return "[" + String.join(", ", args) + "]";
    }
}
