package object;

import java.lang.String;

public record Error(String message) implements Object {
    @Override
    public ObjectType type() {
        return ObjectType.ERROR_OBJ;
    }

    @Override
    public String inspect() {
        return "ERROR: " + message;
    }
}
