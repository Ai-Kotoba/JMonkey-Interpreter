package ast;

import token.Token;

import java.util.List;

public record FunctionLiteral(Token token, List<Identifier> parameters, BlockStatement body) implements Expression {

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        String[] params = new String[parameters.size()];
        for (int i = 0; i < parameters.size(); ++i) {
            params[i] = parameters.get(i).toString();
        }
        return tokenLiteral() + "(" + String.join(", ", params) + ")" + body;
    }
}
