package ast;

import token.Token;

import java.util.List;

public record CallExpression(Token token, Expression function, List<Expression> arguments) implements Expression {


    @SuppressWarnings("unused")
    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        String[] args = new String[arguments.size()];
        for (int i = 0; i < arguments.size(); ++i) {
            args[i] = arguments.get(i).toString();
        }
        return function + "(" + String.join(", ", args) + ")";
    }
}
