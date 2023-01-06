package ast;

import token.Token;

import java.util.Map;

public record HashLiteral(Token token, Map<Expression, Expression> pairs) implements Expression {

    @SuppressWarnings("unused")
    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        String[] args = pairs.entrySet()
                .stream()
                .map(x -> x.getKey().toString() + ":" + x.getValue().toString())
                .toArray(String[]::new);
        return "{" + String.join(", ", args) + "}";
    }
}
