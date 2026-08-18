package parser;

import java.util.ArrayList;
import java.util.List;

import ast.*;
import token.Token;
import token.TokenType;

/**
 * Parser — Borno Compiler
 *
 * Syntax Error Recovery:
 *   - Error হলে crash করে না
 *   - Error message collect করে
 *   - synchronize() দিয়ে পরের ';' বা '}' পর্যন্ত skip করে
 *   - বাকি code parse continue করে
 *   - শেষে সব error একসাথে দেখায়
 *
 * Grammar:
 *   program        → statement* EOF
 *   statement      → declareStmt | reassignStmt | printStmt | ifStmt
 *   declareStmt    → ('সংখ্যা'|'লেখা') IDENTIFIER '=' expression ';'
 *   reassignStmt   → IDENTIFIER '=' expression ';'
 *   printStmt      → 'দেখাও' '(' expression ')' ';'
 *   ifStmt         → 'যদি' '(' expression ')' '{' statement* '}'
 *                    ( 'নাহলে' '{' statement* '}' )?
 *   expression     → comparison
 *   comparison     → addition ( ('=='|'!='|'<'|'>'|'<='|'>=') addition )*
 *   addition       → multiplication ( ('+'|'-') multiplication )*
 *   multiplication → primary ( ('*'|'/'|'%') primary )*
 *   primary        → NUMBER | STRING | IDENTIFIER | '(' expression ')'
 */
public class Parser {

    private final List<Token> tokens;
    private int position = 0;

    // Error recovery — collect errors, don't crash
    private final List<String> errors = new ArrayList<>();

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /** Parsing-এর পরে collected errors */
    public List<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
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
        if (position < tokens.size() - 1) position++;
    }

    private boolean check(TokenType type) {
        return currentToken().getType() == type;
    }

    private boolean checkValue(String value) {
        return currentToken().getValue().equals(value);
    }

    /**
     * consume — token মেলে তাহলে এগোও, না মিললে error collect করো।
     * Crash করে না — error recovery mode-এ যায়।
     */
    private Token consume(TokenType expected, String errorMsg) {
        if (currentToken().getType() == expected) {
            Token tok = currentToken();
            advance();
            return tok;
        }
        // Error record করো, null return করো
        errors.add("[Syntax Error] " + errorMsg +
                   " — পেলাম: '" + currentToken().getValue() + "'");
        return null; // null = error token
    }

    /**
     * synchronize — error recovery।
     * পরের ';' বা '}' বা নতুন statement keyword পর্যন্ত skip করে।
     */
    private void synchronize() {
        while (!check(TokenType.EOF)) {
            // ';' পেলে এটা consume করে থামো
            if (check(TokenType.SEMICOLON)) {
                advance();
                return;
            }
            // '}' পেলে থামো (block শেষ)
            if (check(TokenType.RIGHT_BRACE)) {
                return;
            }
            // নতুন statement-এর keyword পেলে থামো
            if (check(TokenType.KEYWORD)) {
                String kw = currentToken().getValue();
                if (kw.equals("সংখ্যা") || kw.equals("লেখা") ||
                    kw.equals("যদি")    || kw.equals("দেখাও")) {
                    return;
                }
            }
            advance();
        }
    }

    // ─── Entry Point ──────────────────────────────────────────────────────────

    public ProgramNode parseProgram() {
        ProgramNode program = new ProgramNode();

        while (!check(TokenType.EOF)) {
            try {
                ASTNode stmt = parseStatement();
                if (stmt != null) program.addStatement(stmt);
            } catch (RuntimeException e) {
                // Unexpected runtime error — record করো, sync করো
                errors.add("[Syntax Error] " + e.getMessage());
                synchronize();
            }
        }

        return program;
    }

    // ─── Statements ───────────────────────────────────────────────────────────

    private ASTNode parseStatement() {
        if (check(TokenType.KEYWORD)) {
            switch (currentToken().getValue()) {
                case "সংখ্যা":
                case "লেখা":
                    return parseDeclare();

                case "দেখাও":
                    return parsePrint();

                case "যদি":
                    return parseIf();

                default:
                    advance();
                    return null;
            }
        }

        // IDENTIFIER followed by '=' → re-assignment
        if (check(TokenType.IDENTIFIER) &&
            peek(1).getType() == TokenType.ASSIGN) {
            return parseReassign();
        }

        advance();
        return null;
    }

    // ─── Declaration: সংখ্যা i = 0; ──────────────────────────────────────────

    private AssignmentNode parseDeclare() {
        String keyword      = currentToken().getValue();
        String declaredType = keyword.equals("সংখ্যা") ? "NUMBER" : "STRING";
        advance();

        Token id = consume(TokenType.IDENTIFIER, "Variable name আশা করা হয়েছিল");
        if (id == null) { synchronize(); return null; }

        if (consume(TokenType.ASSIGN, "'=' আশা করা হয়েছিল") == null) {
            synchronize(); return null;
        }

        ASTNode expr = parseExpression();
        if (expr == null) { synchronize(); return null; }

        if (consume(TokenType.SEMICOLON, "';' আশা করা হয়েছিল declaration-এর শেষে") == null) {
            // ';' নেই — error already recorded, continue without crash
            // synchronize করার দরকার নেই কারণ পরের keyword-এ এমনিতেই যাবে
        }

        return new AssignmentNode(id.getValue(), expr, declaredType);
    }

    // ─── Re-assignment: i = i + 1; ───────────────────────────────────────────

    private AssignmentNode parseReassign() {
        Token id = consume(TokenType.IDENTIFIER, "IDENTIFIER আশা করা হয়েছিল");
        if (id == null) { synchronize(); return null; }

        if (consume(TokenType.ASSIGN, "'=' আশা করা হয়েছিল") == null) {
            synchronize(); return null;
        }

        ASTNode expr = parseExpression();
        if (expr == null) { synchronize(); return null; }

        if (consume(TokenType.SEMICOLON, "';' আশা করা হয়েছিল") == null) {
            // error recorded, continue
        }

        return new AssignmentNode(id.getValue(), expr, null);
    }

    // ─── Print: দেখাও(...); ───────────────────────────────────────────────────

    private PrintNode parsePrint() {
        advance(); // 'দেখাও'

        if (consume(TokenType.LEFT_PAREN, "'(' আশা করা হয়েছিল") == null) {
            synchronize(); return null;
        }

        ASTNode expr = parseExpression();
        if (expr == null) { synchronize(); return null; }

        if (consume(TokenType.RIGHT_PAREN, "')' আশা করা হয়েছিল") == null) {
            synchronize(); return null;
        }

        consume(TokenType.SEMICOLON, "';' আশা করা হয়েছিল");
        return new PrintNode(expr);
    }

    // ─── If: যদি (...) { } নাহলে { } ────────────────────────────────────────

    private IfNode parseIf() {
        advance(); // 'যদি'

        if (consume(TokenType.LEFT_PAREN, "'(' আশা করা হয়েছিল যদি-র পরে") == null) {
            synchronize(); return null;
        }

        ASTNode condition = parseExpression();
        if (condition == null) { synchronize(); return null; }

        if (consume(TokenType.RIGHT_PAREN, "')' আশা করা হয়েছিল") == null) {
            synchronize(); return null;
        }

        List<ASTNode> thenBranch = parseBlock();
        if (thenBranch == null) return null;

        List<ASTNode> elseBranch = new ArrayList<>();
        if (check(TokenType.KEYWORD) && checkValue("নাহলে")) {
            advance();
            List<ASTNode> eb = parseBlock();
            if (eb != null) elseBranch = eb;
        }

        return new IfNode(condition, thenBranch, elseBranch);
    }

    // ─── Block: { statement* } ────────────────────────────────────────────────

    private List<ASTNode> parseBlock() {
        if (consume(TokenType.LEFT_BRACE, "'{' আশা করা হয়েছিল block-এর শুরুতে") == null) {
            synchronize(); return null;
        }

        List<ASTNode> stmts = new ArrayList<>();
        while (!check(TokenType.RIGHT_BRACE) && !check(TokenType.EOF)) {
            try {
                ASTNode s = parseStatement();
                if (s != null) stmts.add(s);
            } catch (RuntimeException e) {
                errors.add("[Syntax Error] " + e.getMessage());
                synchronize();
            }
        }

        consume(TokenType.RIGHT_BRACE, "'}' আশা করা হয়েছিল block-এর শেষে");
        return stmts;
    }

    // ─── Expressions ─────────────────────────────────────────────────────────

    private ASTNode parseExpression() {
        return parseComparison();
    }

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
            default: return false;
        }
    }

    private ASTNode parseAddition() {
        ASTNode left = parseMultiplication();
        while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
            String op = currentToken().getValue();
            advance();
            left = new BinaryExpressionNode(left, op, parseMultiplication());
        }
        return left;
    }

    private ASTNode parseMultiplication() {
        ASTNode left = parsePrimary();
        while (check(TokenType.MULTIPLY) || check(TokenType.DIVIDE) || check(TokenType.MODULO)) {
            String op = currentToken().getValue();
            advance();
            left = new BinaryExpressionNode(left, op, parsePrimary());
        }
        return left;
    }

    private ASTNode parsePrimary() {
        Token tok = currentToken();

        if (tok.getType() == TokenType.NUMBER)     { advance(); return new LiteralNode(tok.getValue()); }
        if (tok.getType() == TokenType.STRING)     { advance(); return new LiteralNode(tok.getValue()); }
        if (tok.getType() == TokenType.IDENTIFIER) { advance(); return new VariableNode(tok.getValue()); }

        if (tok.getType() == TokenType.LEFT_PAREN) {
            advance();
            ASTNode expr = parseExpression();
            consume(TokenType.RIGHT_PAREN, "')' আশা করা হয়েছিল");
            return expr;
        }

        // এখানে crash করি না — error record করি
        errors.add("[Syntax Error] Unexpected token in expression: '" + tok.getValue() + "'");
        advance(); // skip করো
        return new LiteralNode("0"); // placeholder যাতে tree intact থাকে
    }
}
