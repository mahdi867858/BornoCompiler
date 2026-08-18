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

        // ══ Task 4 — Syntax Error Recovery ═══════════════════════════════════
        System.out.println("════════════════════════════════════════");
        System.out.println("  Task 4 — Syntax Error Recovery");
        System.out.println("  প্রথম statement-এ ';' নেই");
        System.out.println("════════════════════════════════════════");

        String code = """
                সংখ্যা বয়স = 20

                লেখা নাম = "Mahdi";
                """;

        System.out.println("Source:");
        System.out.println(code);

        List<Token> tokens = new Lexer(code).tokenize();
        Parser parser = new Parser(tokens);
        ProgramNode ast = parser.parseProgram();

        // Syntax errors দেখাও
        if (parser.hasErrors()) {
            System.out.println("⚠️  Syntax Error(s) ধরা পড়েছে:");
            for (String err : parser.getErrors()) {
                System.out.println("   " + err);
            }
            System.out.println();
        }

        // AST যা parse হয়েছে তা দেখাও
        System.out.println("Parsed AST (যতটুকু সম্ভব):");
        new ASTPrinter().print(ast);

        System.out.println("\nStatements parsed: " + ast.getStatements().size());

        // Valid statement-গুলোতে Semantic Analysis চলবে
        if (!ast.getStatements().isEmpty()) {
            System.out.println();
            try {
                new SemanticAnalyzer().analyze(ast);
            } catch (RuntimeException e) {
                System.out.println("  ❌ Semantic Error: " + e.getMessage());
            }
        }
    }
}
