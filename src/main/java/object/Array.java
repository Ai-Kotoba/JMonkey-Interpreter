package object;

import java.util.List;
import java.lang.String;

public record Array(List<Object> elements) implements Object {

    @Override
    public ObjectType type() {
        return ObjectType.ARRAY_OBJ;
    }

    @Override
    public String inspect() {
        String[] args = elements.stream()
                .map(Object::inspect)
                .toArray(String[]::new);
        return "[" + String.join(", ", args) + "]";
    }
}
