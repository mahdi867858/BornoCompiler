package main;

import ast.ProgramNode;
import lexer.Lexer;
import parser.Parser;
import semantic.SemanticAnalyzer;
import token.Token;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        // ══ TEST 1: সংখ্যা-তে string assign ══════════════════════════════════
        System.out.println("════════════════════════════════════════");
        System.out.println("  TEST 1 — Type Error");
        System.out.println("  সংখ্যা বয়স = \"Mahdi\";");
        System.out.println("════════════════════════════════════════");

        runTest("""
                সংখ্যা বয়স = "Mahdi";
                """);

        // ══ TEST 2: undeclared variable use ═══════════════════════════════════
        System.out.println("\n════════════════════════════════════════");
        System.out.println("  TEST 2 — Undeclared Variable");
        System.out.println("  সংখ্যা বয়স = 20;");
        System.out.println("  সংখ্যা result = বয়স + নাম;");
        System.out.println("════════════════════════════════════════");

        runTest("""
                সংখ্যা বয়স = 20;
                সংখ্যা result = বয়স + নাম;
                """);
    }

    private static void runTest(String code) {
        try {
            List<Token> tokens = new Lexer(code).tokenize();
            ProgramNode ast    = new Parser(tokens).parseProgram();
            new SemanticAnalyzer().analyze(ast);

            // এখানে পৌঁছালে কোনো error নেই — unexpected
            System.out.println("  ⚠️  কোনো error ধরা পড়েনি!");

        } catch (RuntimeException e) {
            // Stack trace ছাড়া শুধু clean error message
            System.out.println("  ❌ Compiler Error:");
            System.out.println("     " + e.getMessage());
        }
    }
}
