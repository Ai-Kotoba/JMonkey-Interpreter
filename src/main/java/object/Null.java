package object;

import java.lang.String;

public class Null implements Object {
    @Override
    public ObjectType type() {
        return ObjectType.NULL_OBJ;
    }

    @Override
    public String inspect() {
        return "null";
    }
}
