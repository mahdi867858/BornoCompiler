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

        // ══════════════════════════════════════════════════════════════
        //  BornoCompiler — Bangla Programming Language
        //  Stages: Lexer → Parser → AST → Semantic Analysis
        // ══════════════════════════════════════════════════════════════

        String code = """
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

        System.out.println("════════════════════════════════════════");
        System.out.println("  BornoCompiler — Source Code");
        System.out.println("════════════════════════════════════════");
        System.out.println(code);

        // ── Step 1: Lexer ─────────────────────────────────────────────
        System.out.println("════════════════════════════════════════");
        System.out.println("  Lexer Output (Tokens)");
        System.out.println("════════════════════════════════════════");
        List<Token> tokens = new Lexer(code).tokenize();
        for (Token t : tokens) System.out.println("  " + t);

        // ── Step 2: Parser → AST ──────────────────────────────────────
        System.out.println("\n════════════════════════════════════════");
        System.out.println("  Parser → AST");
        System.out.println("════════════════════════════════════════");
        ProgramNode ast = new Parser(tokens).parseProgram();
        new ASTPrinter().print(ast);

        System.out.println("\nParsing completed successfully!");
        System.out.println("Statements: " + ast.getStatements().size());

        // ── Step 3: Semantic Analysis ─────────────────────────────────
        System.out.println();
        new SemanticAnalyzer().analyze(ast);
    }
}
