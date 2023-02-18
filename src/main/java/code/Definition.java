package code;

import java.util.Map;
import java.util.Objects;
import exception.UndefinedOperandException;
public class Definition {
    public Definition(String name, int[] operandWidths) {
        this.name = name;
        this.operandWidths = operandWidths;
    }

    String name;
    int[] operandWidths;
    static Map<Byte, Definition> definitions = Map.of(
            (byte) 0, new Definition("OpConstant", new int[]{2})
    );

    public static Definition lookup(byte opcode) throws UndefinedOperandException {
        Definition def = definitions.get(opcode);
        if (Objects.isNull(def)) {
            throw new UndefinedOperandException("opcode %d defined".formatted(opcode));
        }
        return def;
    }
}
