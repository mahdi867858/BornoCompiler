package parser;

import java.util.List;

import ast.*;
import token.Token;
import token.TokenType;

/**
 * Parser — Borno Compiler
 *
 * Grammar (simplified):
 *   program       → statement* EOF
 *   statement     → assignStmt | printStmt | ifStmt
 *   assignStmt    → KEYWORD IDENTIFIER '=' expression ';'
 *   printStmt     → 'দেখাও' '(' expression ')' ';'
 *   ifStmt        → 'যদি' '(' expression ')' block ( 'নাহলে' block )?
 *   block         → '{' statement* '}'
 *   expression    → comparison
 *   comparison    → addition ( ('==' | '!=' | '<' | '>' | '<=' | '>=') addition )*
 *   addition      → multiplication ( ('+' | '-') multiplication )*
 *   multiplication→ primary ( ('*' | '/' | '%') primary )*
 *   primary       → NUMBER | STRING | IDENTIFIER | '(' expression ')'
 */
public class Parser {

    private final List<Token> tokens;
    private int position = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // ─── Token helpers ───────────────────────────────────────────────────────

    private Token currentToken() {
        return tokens.get(position);
    }

    private Token peek(int offset) {
        int idx = position + offset;
        if (idx >= tokens.size()) return tokens.get(tokens.size() - 1);
        return tokens.get(idx);
    }

    private void advance() {
        if (position < tokens.size() - 1) {
            position++;
        }
    }

    private Token consume(TokenType expected, String errorMsg) {
        if (currentToken().getType() != expected) {
            throw new RuntimeException(
                "[Parser Error] " + errorMsg +
                " — পেলাম: " + currentToken()
            );
        }
        Token tok = currentToken();
        advance();
        return tok;
    }

    private boolean check(TokenType type) {
        return currentToken().getType() == type;
    }

    private boolean checkValue(String value) {
        return currentToken().getValue().equals(value);
    }

    // ─── Entry Point ──────────────────────────────────────────────────────────

    public ProgramNode parseProgram() {
        ProgramNode program = new ProgramNode();

        while (!check(TokenType.EOF)) {
            ASTNode stmt = parseStatement();
            if (stmt != null) {
                program.addStatement(stmt);
            }
        }

        return program;
    }

    // ─── Statements ───────────────────────────────────────────────────────────

    private ASTNode parseStatement() {
        Token tok = currentToken();

        if (tok.getType() == TokenType.KEYWORD) {
            switch (tok.getValue()) {
                case "সংখ্যা":
                case "লেখা":
                    return parseAssignment();

                case "দেখাও":
                    return parsePrint();

                case "যদি":
                    return parseIf();

                default:
                    // অজানা keyword — skip
                    advance();
                    return null;
            }
        }

        // অজানা token — skip
        advance();
        return null;
    }

    /**
     * assignStmt → KEYWORD IDENTIFIER '=' expression ';'
     * Example:  সংখ্যা বয়স = 20;
     */
    private AssignmentNode parseAssignment() {
        String typeName = currentToken().getValue(); // সংখ্যা / লেখা
        System.out.println("Parsing Assignment... (type: " + typeName + ")");
        advance(); // type keyword খাও

        Token idToken = consume(TokenType.IDENTIFIER, "Variable name (IDENTIFIER) আশা করা হয়েছিল");
        String varName = idToken.getValue();

        consume(TokenType.ASSIGN, "'=' আশা করা হয়েছিল");

        ASTNode expr = parseExpression();

        consume(TokenType.SEMICOLON, "';' আশা করা হয়েছিল assignment-এর শেষে");

        System.out.println("  → AssignmentNode: " + typeName + " " + varName);
        return new AssignmentNode(varName, expr);
    }

    /**
     * printStmt → 'দেখাও' '(' expression ')' ';'
     * Example:  দেখাও("প্রাপ্তবয়স্ক");
     */
    private PrintNode parsePrint() {
        System.out.println("Parsing Print...");
        advance(); // 'দেখাও' খাও

        consume(TokenType.LEFT_PAREN, "'(' আশা করা হয়েছিল দেখাও-এর পরে");
        ASTNode expr = parseExpression();
        consume(TokenType.RIGHT_PAREN, "')' আশা করা হয়েছিল");
        consume(TokenType.SEMICOLON, "';' আশা করা হয়েছিল");

        System.out.println("  → PrintNode");
        return new PrintNode(expr);
    }

    /**
     * ifStmt → 'যদি' '(' expression ')' block ( 'নাহলে' block )?
     * Example:  যদি (বয়স >= 18) { দেখাও("Adult"); } নাহলে { দেখাও("Minor"); }
     */
    private IfNode parseIf() {
        System.out.println("Parsing If...");
        advance(); // 'যদি' খাও

        consume(TokenType.LEFT_PAREN, "'(' আশা করা হয়েছিল যদি-র পরে");
        ASTNode condition = parseExpression();
        consume(TokenType.RIGHT_PAREN, "')' আশা করা হয়েছিল");

        BlockNode thenBlock = parseBlock();
        System.out.println("  → IfNode condition parsed");

        BlockNode elseBlock = null;
        if (check(TokenType.KEYWORD) && checkValue("নাহলে")) {
            System.out.println("Parsing Else...");
            advance(); // 'নাহলে' খাও
            elseBlock = parseBlock();
            System.out.println("  → ElseNode parsed");
        }

        return new IfNode(condition, thenBlock, elseBlock);
    }

    /**
     * block → '{' statement* '}'
     */
    private BlockNode parseBlock() {
        consume(TokenType.LEFT_BRACE, "'{' আশা করা হয়েছিল block-এর শুরুতে");

        BlockNode block = new BlockNode();
        while (!check(TokenType.RIGHT_BRACE) && !check(TokenType.EOF)) {
            ASTNode stmt = parseStatement();
            if (stmt != null) {
                block.addStatement(stmt);
            }
        }

        consume(TokenType.RIGHT_BRACE, "'}' আশা করা হয়েছিল block-এর শেষে");
        return block;
    }

    // ─── Expressions (Operator Precedence) ───────────────────────────────────

    /**
     * expression → comparison
     */
    private ASTNode parseExpression() {
        return parseComparison();
    }

    /**
     * comparison → addition ( ('==' | '!=' | '<' | '>' | '<=' | '>=') addition )*
     */
    private ASTNode parseComparison() {
        ASTNode left = parseAddition();

        while (isComparisonOp()) {
            String op = currentToken().getValue();
            advance();
            ASTNode right = parseAddition();
            left = new BinaryExpressionNode(left, op, right);
        }

        return left;
    }

    private boolean isComparisonOp() {
        switch (currentToken().getType()) {
            case EQUAL:
            case NOT_EQUAL:
            case LESS:
            case GREATER:
            case LESS_EQUAL:
            case GREATER_EQUAL:
                return true;
            default:
                return false;
        }
    }

    /**
     * addition → multiplication ( ('+' | '-') multiplication )*
     */
    private ASTNode parseAddition() {
        ASTNode left = parseMultiplication();

        while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
            String op = currentToken().getValue();
            advance();
            ASTNode right = parseMultiplication();
            left = new BinaryExpressionNode(left, op, right);
        }

        return left;
    }

    /**
     * multiplication → primary ( ('*' | '/' | '%') primary )*
     */
    private ASTNode parseMultiplication() {
        ASTNode left = parsePrimary();

        while (check(TokenType.MULTIPLY) || check(TokenType.DIVIDE) || check(TokenType.MODULO)) {
            String op = currentToken().getValue();
            advance();
            ASTNode right = parsePrimary();
            left = new BinaryExpressionNode(left, op, right);
        }

        return left;
    }

    /**
     * primary → NUMBER | STRING | IDENTIFIER | '(' expression ')'
     */
    private ASTNode parsePrimary() {
        Token tok = currentToken();

        if (tok.getType() == TokenType.NUMBER) {
            advance();
            return new LiteralNode(tok.getValue());
        }

        if (tok.getType() == TokenType.STRING) {
            advance();
            return new LiteralNode(tok.getValue());
        }

        if (tok.getType() == TokenType.IDENTIFIER) {
            advance();
            return new VariableNode(tok.getValue());
        }

        if (tok.getType() == TokenType.LEFT_PAREN) {
            advance(); // '(' খাও
            ASTNode expr = parseExpression();
            consume(TokenType.RIGHT_PAREN, "')' আশা করা হয়েছিল");
            return expr;
        }

        throw new RuntimeException(
            "[Parser Error] Unexpected token in expression: " + tok
        );
    }
}
