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

        // ═══ TEST 1: Simple WHILE ═════════════════════════════════════════════
        test("TEST 1 — Simple WHILE loop", """
                সংখ্যা i = 0;

                যতক্ষণ (i < 10)
                {
                    i = i + 1;
                }
                """, false);

        // ═══ TEST 2: WHILE with IF-ELSE inside ════════════════════════════════
        test("TEST 2 — WHILE + IF-ELSE nested", """
                সংখ্যা i = 0;

                যতক্ষণ (i < 10)
                {
                    যদি (i >= 5)
                    {
                        i = i + 1;
                    }
                    নাহলে
                    {
                        i = i + 2;
                    }
                }
                """, false);

        // ═══ TEST 3: WHILE condition not boolean ══════════════════════════════
        test("TEST 3 — WHILE condition NUMBER (error আশা করা হচ্ছে)", """
                সংখ্যা i = 0;

                যতক্ষণ (i)
                {
                    i = i + 1;
                }
                """, true);
    }

    private static void test(String label, String code, boolean expectError) {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("  " + label);
        System.out.println("════════════════════════════════════════");
        try {
            List<Token> tokens = new Lexer(code).tokenize();
            ProgramNode ast    = new Parser(tokens).parseProgram();

            System.out.println("  AST:");
            new ASTPrinter().print(ast);
            System.out.println();

            new SemanticAnalyzer().analyze(ast);

            System.out.println("Parsing completed successfully!");
            System.out.println("Semantic analysis completed successfully!");

            if (expectError) System.out.println("  ❌ Error ধরা পড়েনি! Test FAIL।");
            else             System.out.println("  ✅ PASS");

        } catch (RuntimeException e) {
            if (expectError) System.out.println("  ✅ PASS — " + e.getMessage());
            else             System.out.println("  ❌ FAIL — " + e.getMessage());
        }
    }
}
