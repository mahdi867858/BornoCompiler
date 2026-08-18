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

        // ═══ TEST 1: Valid IF-ELSE ════════════════════════════════════════════
        test("TEST 1 — Valid IF-ELSE (error ছাড়া pass হওয়া উচিত)", """
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
                """, false);

        // ═══ TEST 2: IF condition NOT boolean ════════════════════════════════
        test("TEST 2 — IF condition NUMBER (error আশা করা হচ্ছে)", """
                সংখ্যা বয়স = 20;

                যদি (বয়স)
                {
                    সংখ্যা status = 1;
                }
                """, true);

        // ═══ TEST 3: Wrong declaration type ══════════════════════════════════
        test("TEST 3 — Type mismatch: সংখ্যা = string (error আশা করা হচ্ছে)", """
                সংখ্যা বয়স = "Mahdi";
                """, true);

        // ═══ TEST 4: Undeclared variable ══════════════════════════════════════
        test("TEST 4 — Undeclared variable (error আশা করা হচ্ছে)", """
                সংখ্যা বয়স = x + 10;
                """, true);
    }

    // ─── Runner ────────────────────────────────────────────────────────────────

    private static void test(String label, String code, boolean expectError) {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("  " + label);
        System.out.println("════════════════════════════════════════");

        try {
            List<Token> tokens = new Lexer(code).tokenize();
            ProgramNode ast    = new Parser(tokens).parseProgram();

            if (!expectError) {
                new ASTPrinter().print(ast);
                System.out.println();
            }

            new SemanticAnalyzer().analyze(ast);

            if (expectError) {
                System.out.println("  ❌ Error ধরা পড়েনি! Test FAIL।");
            } else {
                System.out.println("  ✅ PASS — Semantic analysis completed successfully!");
            }

        } catch (RuntimeException e) {
            if (expectError) {
                System.out.println("  ✅ PASS — Expected error ধরা পড়েছে:");
                System.out.println("     " + e.getMessage());
            } else {
                System.out.println("  ❌ FAIL — Unexpected error: " + e.getMessage());
            }
        }
    }
}
