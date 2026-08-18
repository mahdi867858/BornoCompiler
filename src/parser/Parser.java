package parser;

import java.util.List;

import ast.*;
import token.Token;
import token.TokenType;

/**
 * Parser — Borno Compiler
 *
 * Grammar:
 *   program        → statement* EOF
 *   statement      → declareStmt | reassignStmt | printStmt | ifStmt | whileStmt
 *   declareStmt    → ('সংখ্যা'|'লেখা') IDENTIFIER '=' expression ';'
 *   reassignStmt   → IDENTIFIER '=' expression ';'
 *   printStmt      → 'দেখাও' '(' expression ')' ';'
 *   ifStmt         → 'যদি' '(' expression ')' '{' statement* '}'
 *                    ( 'নাহলে' '{' statement* '}' )?
 *   whileStmt      → 'যতক্ষণ' '(' expression ')' '{' statement* '}'
 *   expression     → comparison
 *   comparison     → addition ( ('=='|'!='|'<'|'>'|'<='|'>=') addition )*
 *   addition       → multiplication ( ('+'|'-') multiplication )*
 *   multiplication → primary ( ('*'|'/'|'%') primary )*
 *   primary        → NUMBER | STRING | IDENTIFIER | '(' expression ')'
 */
public class Parser {

    private final List<Token> tokens;
    private int position = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // ─── Token helpers ────────────────────────────────────────────────────────

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
            if (stmt != null) program.addStatement(stmt);
        }
        return program;
    }

    // ─── Statements ───────────────────────────────────────────────────────────

    private ASTNode parseStatement() {
        // KEYWORD: সংখ্যা / লেখা / দেখাও / যদি / যতক্ষণ
        if (check(TokenType.KEYWORD)) {
            switch (currentToken().getValue()) {
                case "সংখ্যা":
                case "লেখা":
                    return parseDeclare();   // নতুন declaration

                case "দেখাও":
                    return parsePrint();

                case "যদি":
                    return parseIf();

                case "যতক্ষণ":
                    return parseWhile();

                default:
                    advance();
                    return null;
            }
        }

        // IDENTIFIER followed by '=' → re-assignment (keyword ছাড়া)
        // e.g.  i = i + 1;
        if (check(TokenType.IDENTIFIER) &&
            peek(1).getType() == TokenType.ASSIGN) {
            return parseReassign();
        }

        // অজানা token — skip
        advance();
        return null;
    }

    // ─── Declaration: সংখ্যা i = 0; ──────────────────────────────────────────

    private AssignmentNode parseDeclare() {
        String keyword      = currentToken().getValue();
        String declaredType = keyword.equals("সংখ্যা") ? "NUMBER" : "STRING";
        advance(); // keyword খাও

        Token id = consume(TokenType.IDENTIFIER, "Variable name আশা করা হয়েছিল");
        consume(TokenType.ASSIGN, "'=' আশা করা হয়েছিল");
        ASTNode expr = parseExpression();
        consume(TokenType.SEMICOLON, "';' আশা করা হয়েছিল");

        return new AssignmentNode(id.getValue(), expr, declaredType);
    }

    // ─── Re-assignment: i = i + 1; ───────────────────────────────────────────

    private AssignmentNode parseReassign() {
        Token id = consume(TokenType.IDENTIFIER, "IDENTIFIER আশা করা হয়েছিল");
        consume(TokenType.ASSIGN, "'=' আশা করা হয়েছিল");
        ASTNode expr = parseExpression();
        consume(TokenType.SEMICOLON, "';' আশা করা হয়েছিল");

        // declaredType = null → re-assignment (SemanticAnalyzer বুঝবে)
        return new AssignmentNode(id.getValue(), expr, null);
    }

    // ─── Print: দেখাও(...); ───────────────────────────────────────────────────

    private PrintNode parsePrint() {
        advance(); // 'দেখাও' খাও
        consume(TokenType.LEFT_PAREN, "'(' আশা করা হয়েছিল");
        ASTNode expr = parseExpression();
        consume(TokenType.RIGHT_PAREN, "')' আশা করা হয়েছিল");
        consume(TokenType.SEMICOLON, "';' আশা করা হয়েছিল");
        return new PrintNode(expr);
    }

    // ─── If: যদি (...) { } নাহলে { } ────────────────────────────────────────

    private IfNode parseIf() {
        advance(); // 'যদি' খাও
        consume(TokenType.LEFT_PAREN, "'(' আশা করা হয়েছিল যদি-র পরে");
        ASTNode condition = parseExpression();
        consume(TokenType.RIGHT_PAREN, "')' আশা করা হয়েছিল");

        List<ASTNode> thenBranch = parseBlock();

        List<ASTNode> elseBranch = new java.util.ArrayList<>();
        if (check(TokenType.KEYWORD) && checkValue("নাহলে")) {
            advance(); // 'নাহলে' খাও
            elseBranch = parseBlock();
        }

        return new IfNode(condition, thenBranch, elseBranch);
    }

    // ─── While: যতক্ষণ (...) { } ─────────────────────────────────────────────

    private WhileNode parseWhile() {
        advance(); // 'যতক্ষণ' খাও
        consume(TokenType.LEFT_PAREN, "'(' আশা করা হয়েছিল যতক্ষণ-এর পরে");
        ASTNode condition = parseExpression();
        consume(TokenType.RIGHT_PAREN, "')' আশা করা হয়েছিল");

        List<ASTNode> body = parseBlock();
        return new WhileNode(condition, body);
    }

    // ─── Block: { statement* } ────────────────────────────────────────────────

    private List<ASTNode> parseBlock() {
        consume(TokenType.LEFT_BRACE, "'{' আশা করা হয়েছিল block-এর শুরুতে");
        List<ASTNode> stmts = new java.util.ArrayList<>();
        while (!check(TokenType.RIGHT_BRACE) && !check(TokenType.EOF)) {
            ASTNode s = parseStatement();
            if (s != null) stmts.add(s);
        }
        consume(TokenType.RIGHT_BRACE, "'}' আশা করা হয়েছিল block-এর শেষে");
        return stmts;
    }

    // ─── Expressions (Operator Precedence) ───────────────────────────────────

    private ASTNode parseExpression() {
        return parseComparison();
    }

    /** comparison → addition ( ('=='|'!='|'<'|'>'|'<='|'>=') addition )* */
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
            case EQUAL: case NOT_EQUAL:
            case LESS:  case GREATER:
            case LESS_EQUAL: case GREATER_EQUAL:
                return true;
            default:
                return false;
        }
    }

    /** addition → multiplication ( ('+'|'-') multiplication )* */
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

    /** multiplication → primary ( ('*'|'/'|'%') primary )* */
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

    /** primary → NUMBER | STRING | IDENTIFIER | '(' expression ')' */
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
            advance();
            ASTNode expr = parseExpression();
            consume(TokenType.RIGHT_PAREN, "')' আশা করা হয়েছিল");
            return expr;
        }

        throw new RuntimeException("[Parser Error] Unexpected token: " + tok);
    }
}
