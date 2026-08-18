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

        // ══ Task 5A — Token Coverage ══════════════════════════════════════════
        runSection("5A — Full Token Coverage",
            """
            সংখ্যা a = 10;
            সংখ্যা b = 2;

            যদি (a >= b)
            {
                দেখাও("YES");
            }
            নাহলে
            {
                দেখাও("NO");
            }
            """, false, true);

        // ══ Task 5D — Compiler-style AST ══════════════════════════════════════
        runSection("5D — AST Format (compiler-style)",
            """
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
            """, false, true);

        // ══ Task 5E — Operator Precedence ══════════════════════════════════════
        runSection("5E-i — Arithmetic + Precedence (10 + 5 * 2)",
            """
            সংখ্যা result = 10 + 5 * 2;
            """, false, true);

        runSection("5E-ii — Parentheses ((10 + 5) * 2)",
            """
            সংখ্যা result = (10 + 5) * 2;
            """, false, true);

        // ══ Task 5E — Division by Zero ═════════════════════════════════════════
        runSection("5E-iii — Division by Zero (error আশা করা হচ্ছে)",
            """
            সংখ্যা a = 10 / 0;
            """, true, false);
    }

    // ─── Test runner ──────────────────────────────────────────────────────────

    private static void runSection(String label, String code,
                                   boolean expectError, boolean showAst) {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("  " + label);
        System.out.println("════════════════════════════════════════");

        try {
            // ── Lexer ──────────────────────────────────────────────────────
            List<Token> tokens = new Lexer(code).tokenize();

            // Token list (5A only)
            if (label.startsWith("5A")) {
                System.out.println("Tokens:");
                for (Token t : tokens) {
                    System.out.printf("  %-18s : %s%n",
                        t.getType(), t.getValue());
                }
                System.out.println();
            }

            // ── Parser ─────────────────────────────────────────────────────
            Parser parser   = new Parser(tokens);
            ProgramNode ast = parser.parseProgram();

            if (parser.hasErrors()) {
                for (String e : parser.getErrors())
                    System.out.println("  ⚠️  " + e);
            }

            // ── AST ────────────────────────────────────────────────────────
            if (showAst) {
                new ASTPrinter().print(ast);
                System.out.println();
            }

            // ── Semantic Analysis ──────────────────────────────────────────
            new SemanticAnalyzer().analyze(ast);

            if (expectError) System.out.println("  ❌ Error ধরা পড়েনি!");
            else             System.out.println("  ✅ PASS");

        } catch (RuntimeException e) {
            if (expectError) System.out.println("  ✅ PASS — " + e.getMessage());
            else             System.out.println("  ❌ FAIL — " + e.getMessage());
        }
    }
}
