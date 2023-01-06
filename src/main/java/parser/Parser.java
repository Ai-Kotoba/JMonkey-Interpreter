package parser;

import lexer.Lexer;
import token.Token;
import token.TokenType;

import java.util.*;

import static token.TokenType.*;

public class Parser {

    // myFunction(X)
    private static final int LOWEST = 0;
    private static final int EQUALS = 1;        // ==
    @SuppressWarnings("SpellCheckingInspection")
    private static final int LESSGREATER = 2;   // > or <
    private static final int SUM = 3;           // +
    private static final int PRODUCT = 4;       // *
    private static final int PREFIX = 5;        // -X or !X
    private static final int CALL = 6;          // myFunction(X)
    private static final int INDEX = 7;         // array[index]
    private static final Map<TokenType, Integer> precedences = Map.ofEntries(
            Map.entry(EQ, EQUALS),
            Map.entry(NOT_EQ, EQUALS),
            Map.entry(LT, LESSGREATER),
            Map.entry(GT, LESSGREATER),
            Map.entry(PLUS, SUM),
            Map.entry(MINUS, SUM),
            Map.entry(SLASH, PRODUCT),
            Map.entry(ASTERISK, PRODUCT),
            Map.entry(LPAREN, CALL),
            Map.entry(LBRACKET, INDEX)
    );
    private final Lexer lexer;
    private final List<String> errors;
    private Token curToken;
    private Token peekToken;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.errors = new ArrayList<>();
        nextToken();
        nextToken();
    }

    private void nextToken() {
        curToken = peekToken;
        peekToken = lexer.nextToken();
    }
    private final Map<TokenType, PrefixParseFn> prefixParseFns = Map.ofEntries(
            Map.entry(IDENT, Parser::parseIdentifier),
            Map.entry(INT, Parser::parseIntegerLiteral),
            Map.entry(STRING, Parser::parseStringLiteral),
            Map.entry(BANG, Parser::parsePrefixExpression),
            Map.entry(MINUS, Parser::parsePrefixExpression),
            Map.entry(TRUE, Parser::parseBoolean),
            Map.entry(FALSE, Parser::parseBoolean),
            Map.entry(LPAREN, Parser::parseGroupedExpression),
            Map.entry(IF, Parser::parseIfExpression),
            Map.entry(FUNCTION, Parser::parseFunctionLiteral),
            Map.entry(LBRACKET, Parser::parseArrayLiteral),
            Map.entry(LBRACE, Parser::parseHashLiteral)
    );

    private boolean curTokenIs(TokenType t) {
        return curToken.type() == t;
    }

    private boolean peekTokenIs(TokenType t) {
        return peekToken.type() == t;
    }
    private static final Map<TokenType, InfixParseFn> infixParseFns = Map.ofEntries(
            Map.entry(PLUS, Parser::parseInfixExpression),
            Map.entry(MINUS, Parser::parseInfixExpression),
            Map.entry(SLASH, Parser::parseInfixExpression),
            Map.entry(ASTERISK, Parser::parseInfixExpression),
            Map.entry(EQ, Parser::parseInfixExpression),
            Map.entry(NOT_EQ, Parser::parseInfixExpression),
            Map.entry(LT, Parser::parseInfixExpression),
            Map.entry(GT, Parser::parseInfixExpression),
            Map.entry(LPAREN, Parser::parseCallExpression),
            Map.entry(LBRACKET, Parser::parseIndexExpression)
    );

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean expectPeek(TokenType t) {
        if (peekTokenIs(t)) {
            nextToken();
            return true;
        } else {
            peekError(t);
            return false;
        }
    }

    public List<String> errors() {
        return errors;
    }

    private void peekError(TokenType t) {
        String msg = "expected next token to be %s, got %s instead".formatted(t, peekToken.type());
        errors.add(msg);
    }

    private void noPrefixParseFnError(TokenType t) {
        String msg = "no prefix parse function for %s found".formatted(t);
        errors.add(msg);
    }

    public ast.Program parseProgram() {
        ArrayList<ast.Statement> statements = new ArrayList<>();
        while (!curTokenIs(EOF)) {
            ast.Statement stmt = parseStatement();
            if (stmt != null) {
                statements.add(stmt);
            }
            nextToken();
        }
        return new ast.Program(statements);
    }

    private ast.Statement parseStatement() {
        return switch (curToken.type()) {
            case LET -> parseLetStatement();
            case RETURN -> parseReturnStatement();
            default -> parseExpressionStatement();
        };
    }

    private ast.LetStatement parseLetStatement() {
        Token token = curToken;
        if (!expectPeek(IDENT)) {
            return null;
        }
        ast.Identifier name = new ast.Identifier(curToken, curToken.literal());
        if (!expectPeek(ASSIGN)) {
            return null;
        }
        nextToken();
        ast.Expression value = parseExpression(LOWEST);
        if (peekTokenIs(SEMICOLON)) {
            nextToken();
        }
        return new ast.LetStatement(token, name, value);
    }

    private ast.ReturnStatement parseReturnStatement() {
        Token token = curToken;
        nextToken();
        ast.Expression returnValue = parseExpression(LOWEST);
        if (peekTokenIs(SEMICOLON)) {
            nextToken();
        }
        return new ast.ReturnStatement(token, returnValue);
    }

    private ast.ExpressionStatement parseExpressionStatement() {
        Token token = curToken;
        ast.Expression expression = parseExpression(LOWEST);
        if (peekTokenIs(SEMICOLON)) {
            nextToken();
        }
        return new ast.ExpressionStatement(token, expression);
    }

    private ast.Expression parseExpression(int precedence) {
        PrefixParseFn prefix = prefixParseFns.get(curToken.type());
        if (prefix == null) {
            noPrefixParseFnError(curToken.type());
            return null;
        }
        ast.Expression leftExp = prefix.parse(this);
        while (!peekTokenIs(SEMICOLON) && precedence < peekPrecedence()) {
            InfixParseFn infix = infixParseFns.get(peekToken.type());
            if (infix == null) {
                return leftExp;
            }
            nextToken();
            leftExp = infix.parse(this, leftExp);
        }
        return leftExp;
    }

    private ast.Expression parseArrayLiteral() {
        Token token = curToken;
        List<ast.Expression> elements = parseExpressionList(RBRACKET);
        return new ast.ArrayLiteral(token, elements);
    }

    private ast.Expression parseIndexExpression(ast.Expression left) {
        Token token = curToken;
        nextToken();
        ast.Expression index = parseExpression(LOWEST);
        if (!expectPeek(RBRACKET)) {
            return null;
        }
        return new ast.IndexExpression(token, left, index);
    }

    private ast.Expression parseHashLiteral() {
        Token token = curToken;
        Map<ast.Expression, ast.Expression> pairs = new HashMap<>();
        while (!peekTokenIs(RBRACE)) {
            nextToken();
            ast.Expression key = parseExpression(LOWEST);
            if (!expectPeek(COLON)) {
                return null;
            }
            nextToken();
            ast.Expression value = parseExpression(LOWEST);
            pairs.put(key, value);
            if (!peekTokenIs(RBRACE) && !expectPeek(COMMA)) {
                return null;
            }
        }
        if (!expectPeek(RBRACE)) {
            return null;
        }
        return new ast.HashLiteral(token, pairs);
    }

    private int peekPrecedence() {
        Integer p = precedences.get(peekToken.type());
        return Objects.requireNonNullElse(p, LOWEST);
    }

    private int curPrecedence() {
        Integer p = precedences.get(curToken.type());
        return Objects.requireNonNullElse(p, LOWEST);
    }

    private ast.Expression parseIdentifier() {
        return new ast.Identifier(curToken, curToken.literal());
    }

    private ast.Expression parseIntegerLiteral() {
        int value;
        try {
            value = Integer.parseInt(curToken.literal());
        } catch (NumberFormatException e) {
            String msg = "could not parse %s as integer".formatted(curToken.literal());
            errors.add(msg);
            return null;
        }
        return new ast.IntegerLiteral(curToken, value);
    }

    private ast.Expression parseStringLiteral() {
        return new ast.StringLiteral(curToken, curToken.literal());
    }

    private ast.Expression parsePrefixExpression() {
        Token token = curToken;
        String operator = curToken.literal();
        nextToken();
        ast.Expression right = parseExpression(PREFIX);
        return new ast.PrefixExpression(token, operator, right);
    }

    private ast.Expression parseInfixExpression(ast.Expression left) {
        Token token = curToken;
        String operator = curToken.literal();
        int precedence = curPrecedence();
        nextToken();
        ast.Expression right = parseExpression(precedence);
        return new ast.InfixExpression(token, left, operator, right);
    }

    private ast.Expression parseBoolean() {
        return new ast.Boolean(curToken, curTokenIs(TRUE));
    }

    private ast.Expression parseGroupedExpression() {
        nextToken();
        ast.Expression expression = parseExpression(LOWEST);
        if (!expectPeek(RPAREN)) {
            return null;
        }
        return expression;
    }

    private ast.Expression parseIfExpression() {
        Token token = curToken;
        if (!expectPeek(LPAREN)) {
            return null;
        }
        nextToken();
        ast.Expression condition = parseExpression(LOWEST);
        if (!expectPeek(RPAREN)) {
            return null;
        }
        if (!expectPeek(LBRACE)) {
            return null;
        }
        ast.BlockStatement consequence = parseBlockStatement();
        ast.BlockStatement alternative = null;
        if (peekTokenIs(ELSE)) {
            nextToken();
            if (!expectPeek(LBRACE)) {
                return null;
            }
            alternative = parseBlockStatement();
        }
        return new ast.IfExpression(token, condition, consequence, alternative);
    }

    private ast.BlockStatement parseBlockStatement() {
        Token token = curToken;
        List<ast.Statement> statements = new ArrayList<>();
        nextToken();
        while (!curTokenIs(RBRACE) && !curTokenIs(EOF)) {
            ast.Statement stmt = parseStatement();
            if (stmt != null) {
                statements.add(stmt);
            }
            nextToken();
        }
        return new ast.BlockStatement(token, statements);
    }

    private ast.Expression parseFunctionLiteral() {
        Token token = curToken;
        if (!expectPeek(LPAREN)) {
            return null;
        }
        List<ast.Identifier> parameters = parseFunctionParameters();
        if (!expectPeek(LBRACE)) {
            return null;
        }
        ast.BlockStatement body = parseBlockStatement();
        return new ast.FunctionLiteral(token, parameters, body);
    }

    private List<ast.Identifier> parseFunctionParameters() {
        List<ast.Identifier> identifiers = new ArrayList<>();
        if (peekTokenIs(RPAREN)) {
            nextToken();
            return identifiers;
        }
        nextToken();
        ast.Identifier ident = new ast.Identifier(curToken, curToken.literal());
        identifiers.add(ident);
        while (peekTokenIs(COMMA)) {
            nextToken();
            nextToken();
            ident = new ast.Identifier(curToken, curToken.literal());
            identifiers.add(ident);
        }
        if (!expectPeek(RPAREN)) {
            return null;
        }
        return identifiers;
    }

    private ast.Expression parseCallExpression(ast.Expression function) {
        Token token = curToken;
        List<ast.Expression> arguments = parseExpressionList(RPAREN);
        return new ast.CallExpression(token, function, arguments);
    }

    private List<ast.Expression> parseExpressionList(TokenType end) {
        List<ast.Expression> list = new ArrayList<>();
        if (peekTokenIs(end)) {
            nextToken();
            return list;
        }
        nextToken();
        list.add(parseExpression(LOWEST));
        while (peekTokenIs(COMMA)) {
            nextToken();
            nextToken();
            list.add(parseExpression(LOWEST));
        }
        if (!expectPeek(end)) {
            return null;
        }
        return list;
    }

    //Two functional interfaces to replace values in maps.
    @FunctionalInterface
    interface PrefixParseFn {
        ast.Expression parse(Parser parser);
    }

    @FunctionalInterface
    interface InfixParseFn {
        ast.Expression parse(Parser parser, ast.Expression left);
    }
}
