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

        String code = """
                সংখ্যা বয়স = 20;
                সংখ্যা স্কোর = 10 + 5 * 2;

                যদি (বয়স >= 18) {
                    দেখাও("প্রাপ্তবয়স্ক");
                } নাহলে {
                    দেখাও("অপ্রাপ্তবয়স্ক");
                }
                """;

        System.out.println("═══════════════════════════════════════");
        System.out.println("  Source Code:");
        System.out.println("═══════════════════════════════════════");
        System.out.println(code);

        // ── Step 1: Lexer ─────────────────────────────────────────────────────
        System.out.println("═══════════════════════════════════════");
        System.out.println("  Lexer Output (Tokens):");
        System.out.println("═══════════════════════════════════════");
        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        for (Token tok : tokens) {
            System.out.println("  " + tok);
        }

        // ── Step 2: Parser → AST ──────────────────────────────────────────────
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  Parser শুরু হচ্ছে...");
        System.out.println("═══════════════════════════════════════");
        Parser parser = new Parser(tokens);
        ProgramNode ast = parser.parseProgram();

        // ── Step 3: AST Print ─────────────────────────────────────────────────
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  AST (Abstract Syntax Tree):");
        System.out.println("═══════════════════════════════════════");
        new ASTPrinter().print(ast);

        // ── Step 4: Semantic Analysis ─────────────────────────────────────────
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(ast);
    }
}
