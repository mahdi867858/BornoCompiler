package main;

import ast.ASTPrinter;
import ast.ProgramNode;
import lexer.Lexer;
import parser.Parser;
import semantic.SemanticAnalyzer;
import token.Token;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        // ═══ TEST 1: Valid code ═══════════════════════════════════════════════
        System.out.println("════════════════════════════════════════");
        System.out.println("  TEST 1 — Valid code");
        System.out.println("════════════════════════════════════════");

        String code1 = """
                সংখ্যা বয়স = 20;
                লেখা নাম = "Mahdi";

                যদি (বয়স >= 18)
                {
                    সংখ্যা status = 1;
                }
                নাহলে
                {
                    সংখ্যা status = 0;
                }
                """;

        runTest(code1);

        // ═══ TEST 2: Type mismatch ════════════════════════════════════════════
        System.out.println("\n════════════════════════════════════════");
        System.out.println("  TEST 2 — Type mismatch (expect error)");
        System.out.println("════════════════════════════════════════");

        String code2 = """
                সংখ্যা বয়স = "Mahdi";
                """;

        runTestExpectError(code2);

        // ═══ TEST 3: Undeclared variable ══════════════════════════════════════
        System.out.println("\n════════════════════════════════════════");
        System.out.println("  TEST 3 — Undeclared variable (expect error)");
        System.out.println("════════════════════════════════════════");

        String code3 = """
                সংখ্যা x = y + 10;
                """;

        runTestExpectError(code3);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private static void runTest(String code) {
        try {
            Lexer lexer       = new Lexer(code);
            List<Token> tokens = lexer.tokenize();
            Parser parser     = new Parser(tokens);
            ProgramNode ast   = parser.parseProgram();

            System.out.println("\n  AST:");
            new ASTPrinter().print(ast);

            System.out.println();
            SemanticAnalyzer analyzer = new SemanticAnalyzer();
            analyzer.analyze(ast);

            System.out.println("\nStatements: " + ast.getStatements().size());

        } catch (RuntimeException e) {
            System.out.println("  [ERROR] " + e.getMessage());
        }
    }

    private static void runTestExpectError(String code) {
        try {
            Lexer lexer       = new Lexer(code);
            List<Token> tokens = lexer.tokenize();
            Parser parser     = new Parser(tokens);
            ProgramNode ast   = parser.parseProgram();
            SemanticAnalyzer analyzer = new SemanticAnalyzer();
            analyzer.analyze(ast);

            System.out.println("  ❌ Error ধরা পড়েনি! Test fail।");

        } catch (RuntimeException e) {
            System.out.println("  ✅ Expected error ধরা পড়েছে: " + e.getMessage());
        }
    }
}
