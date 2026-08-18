package main;

import ast.ProgramNode;
import lexer.Lexer;
import parser.Parser;
import semantic.SemanticAnalyzer;
import token.Token;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        // ══ Task 3 — IF condition must be BOOLEAN ════════════════════════════
        System.out.println("════════════════════════════════════════");
        System.out.println("  Task 3 — IF Condition Type Error");
        System.out.println("  যদি (বয়স) — বয়স is NUMBER, not BOOLEAN");
        System.out.println("════════════════════════════════════════");

        runTest("""
                সংখ্যা বয়স = 20;

                যদি (বয়স)
                {
                    সংখ্যা status = 1;
                }
                নাহলে
                {
                    সংখ্যা status = 0;
                }
                """);
    }

    private static void runTest(String code) {
        try {
            List<Token> tokens = new Lexer(code).tokenize();
            ProgramNode ast    = new Parser(tokens).parseProgram();
            new SemanticAnalyzer().analyze(ast);

            System.out.println("  ⚠️  কোনো error ধরা পড়েনি!");

        } catch (RuntimeException e) {
            System.out.println("  ❌ Compiler Error:");
            System.out.println("     " + e.getMessage());
        }
    }
}
