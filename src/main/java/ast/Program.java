package ast;

import java.util.List;

public record Program(List<Statement> statements) implements Node {
    @SuppressWarnings("unused")
    @Override
    public String tokenLiteral() {
        return statements.size() > 0 ? statements.get(0).tokenLiteral() : "";
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
