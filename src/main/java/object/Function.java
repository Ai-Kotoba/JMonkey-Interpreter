package object;

import ast.BlockStatement;
import ast.Identifier;

import java.lang.String;
import java.util.List;

public record Function(List<Identifier> parameters, BlockStatement body, Environment env) implements Object {
    @Override
    public ObjectType type() {
        return ObjectType.FUNCTION_OBJ;
    }

    @Override
    public String inspect() {
        List<String> params = parameters.stream().map(Identifier::toString).toList();
        return "fn(" + String.join(", ", params) + ") {\n" + body + "\n}";
    }
}
