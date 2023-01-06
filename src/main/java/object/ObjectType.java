package object;

import java.lang.String;

public enum ObjectType {
    NULL_OBJ("NULL"),
    ERROR_OBJ("ERROR"),
    INTEGER_OBJ("INTEGER"),
    BOOLEAN_OBJ("BOOLEAN"),
    RETURN_VALUE_OBJ("RETURN_VALUE"),
    FUNCTION_OBJ("FUNCTION"),
    BUILTIN_OBJ("BUILTIN"),
    STRING_OBJ("STRING"),
    ARRAY_OBJ("ARRAY"),
    HASH_OBJ("HASH");
    private final String literal;

    ObjectType(String literal) {
        this.literal = literal;
    }

    public String literal() {
        return literal;
    }
}
