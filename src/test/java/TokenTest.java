import token.Token;
import org.junit.jupiter.api.*;

import static token.TokenType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenTest {
    private Token token;

    @BeforeEach
    void setUp() {
        token = new Token(ILLEGAL, "Test");
    }

    @Test
    void lookupIdent() {
        assertEquals(Token.lookupIdent("fn"), FUNCTION);
        assertEquals(Token.lookupIdent("var"), IDENT);
    }

    @Test
    void lookupSymbol() {
        assertEquals(Token.lookupSymbol('+'), PLUS);
        assertEquals(Token.lookupSymbol('`'), ILLEGAL);
    }

    @Test
    void testToString() {
        assertEquals(token.toString(), "{Type:ILLEGAL Literal:Test}");
    }
}