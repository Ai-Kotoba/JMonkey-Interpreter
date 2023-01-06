package object;

import java.lang.String;

public record Boolean(boolean value) implements Object, HashTable {

    @Override
    public ObjectType type() {
        return ObjectType.BOOLEAN_OBJ;
    }


    @Override
    public String inspect() {
        return java.lang.Boolean.toString(value);
    }

    @Override
    public HashKey hashKey() {
        int value;
        if (this.value) {
            value = 1;
        } else {
            value = 0;
        }
        return new HashKey(type(), value);
    }
}
