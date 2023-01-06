package object;

import java.lang.String;

public record Integer(int value) implements Object, HashTable {

    @Override
    public ObjectType type() {
        return ObjectType.INTEGER_OBJ;
    }

    @Override
    public String inspect() {
        return "%d".formatted(value);
    }

    @Override
    public HashKey hashKey() {
        return new HashKey(type(), value);
    }
}
