package object;

import java.lang.String;

public record ReturnValue(Object value) implements Object {
    @Override
    public ObjectType type() {
        return ObjectType.RETURN_VALUE_OBJ;
    }

    @Override
    public String inspect() {
        return value.inspect();
    }
}
