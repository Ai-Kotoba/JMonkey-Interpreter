package object;

import java.lang.Object;
import java.lang.Integer;

public record HashKey(ObjectType type, int value) {
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof HashKey hashKey))
            return false;
        return type == hashKey.type && value == hashKey.value;
    }

    //According to the "Effective Java" agreement, when covering equals(), hashCode() must also be covered.
    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + Integer.hashCode(value);
        return result;
    }
}
