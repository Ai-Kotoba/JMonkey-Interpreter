import ast.Identifier;
import ast.LetStatement;
import ast.Program;
import token.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static token.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;

class ProgramTest {
    Program program;

    @BeforeEach
    void init() {
        LetStatement letSm = new LetStatement(
                new Token(LET, "let"),
                new Identifier(new Token(IDENT, "myVar"), "myVar"),
                new Identifier(new Token(IDENT, "anotherVar"), "anotherVar")
        );
        program = new Program(List.of(letSm));
    }

    @Test
    void testToString() {
        assertEquals("let myVar = anotherVar;",program.toString());
    }
}