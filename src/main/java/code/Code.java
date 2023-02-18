package code;

import exception.UndefinedOperandException;

import java.nio.ByteBuffer;

public class Code {
    public final static byte OpConstant = 0;

    public static byte[] make(byte op, int... operands)  {
        try {
            Definition def = Definition.lookup(op);
            int instructionLen = 1;
            for (int width : def.operandWidths) {
                instructionLen += width;
            }
            byte[] instruction = new byte[instructionLen];
            instruction[0] = op;
            int offset = 1;
            for (int i = 0; i < operands.length; ++i) {
                int width = def.operandWidths[i];
                int o = operands[i];
                switch (width) {
                    case 2 -> {
                        var buffer = ByteBuffer.wrap(instruction);
                        buffer.putShort(offset, (short)o);
                    }
                    default -> throw new UndefinedOperandException("unsupported operand width %d".formatted(width));
                }
                offset += width;
            }
            return instruction;
        } catch (UndefinedOperandException e) {
            return new byte[]{};
        }
    }
}
