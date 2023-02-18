import org.junit.jupiter.api.Test;

import static code.Code.OpConstant;
import static code.Code.make;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeTest {
    @Test
    void testMake() {
        record Temp(byte op, int[] operandWidths, byte[] expected) {
        }
        Temp[] tests = new Temp[]{
                new Temp(OpConstant, new int[]{65534}, new byte[]{OpConstant, (byte) 255, (byte) 254}),
        };
        for (var test : tests) {
            byte[] instruction = make(test.op, test.operandWidths);
            assertEquals(test.expected.length, instruction.length);
            for (int i = 0; i < test.expected.length; ++i) {
                assertEquals(test.expected[i], instruction[i]);
            }
        }
    }

}
