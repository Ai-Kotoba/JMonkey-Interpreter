package object;

public record String(java.lang.String value) implements Object, HashTable {
    @Override
    public ObjectType type() {
        return ObjectType.STRING_OBJ;
    }

    @Override
    public java.lang.String inspect() {
        return value;
    }

    @Override
    public HashKey hashKey() {
        return new HashKey(type(), value.hashCode());
    }
}
