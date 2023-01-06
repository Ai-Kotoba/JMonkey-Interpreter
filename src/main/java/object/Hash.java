package object;

import java.util.Map;
import java.lang.String;

public record Hash(Map<HashKey, HashPair> pairs) implements Object {

    @Override
    public ObjectType type() {
        return ObjectType.HASH_OBJ;
    }

    @Override
    public String inspect() {
        String[] pairs = this.pairs.values()
                .stream()
                .map(pair -> "%s: %s".formatted(pair.key(), pair.value()))
                .toArray(String[]::new);
        return "{" + String.join(", ", pairs) + "}";
    }
}
