package main;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import ast.ProgramNode;
import codegen.PythonGenerator;
import codegen.WasmGenerator;
import lexer.Lexer;
import parser.Parser;
import semantic.SemanticAnalyzer;
import tac.TACGenerator;
import tac.TACProgram;
import token.Token;

public class TestRunner {

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        System.out.println("============================================================");
        System.out.println("BORNO COMPILER — NEW FEATURES TEST RUNNER (PARTS 1-11)");
        System.out.println("============================================================\n");

        runTestCase(
            "TEST 1 — Unary Constant",
            "ধরি সংখ্যা ক = -৫;",
            true
        );

        runTestCase(
            "TEST 2 — Unary with Variable",
            "ধরি সংখ্যা ক = ১০;\nধরি সংখ্যা খ = -ক;",
            true
        );

        runTestCase(
            "TEST 3 — Unary + Binary",
            "ধরি সংখ্যা ক = ১০;\nধরি সংখ্যা খ = ২০;\nধরি সংখ্যা গ = -ক + খ;",
            true
        );

        runTestCase(
            "TEST 4 — While Loop (যতক্ষণ)",
            "ধরি সংখ্যা ক = ০;\nযতক্ষণ (ক < ৫) {\n    দেখাও(ক);\n    ক = ক + ১;\n}",
            true
        );

        runTestCase(
            "TEST 5 — While Initially False",
            "ধরি সংখ্যা ক = ১০;\nযতক্ষণ (ক < ৫) {\n    দেখাও(ক);\n}",
            true
        );

        runTestCase(
            "TEST 6 — For Loop ('for')",
            "for (ধরি সংখ্যা ক = ০; ক < ৫; ক = ক + ১) {\n    দেখাও(ক);\n}",
            true
        );

        runTestCase(
            "TEST 6B — For Loop Native Bangla ('ফর')",
            "ফর (ধরি সংখ্যা ক = ০; ক < ৫; ক = ক + ১) {\n    দেখাও(ক);\n}",
            true
        );

        runTestCase(
            "TEST 7 — If inside While",
            "ধরি সংখ্যা ক = ০;\nযতক্ষণ (ক < ৫) {\n    যদি (ক == ২) {\n        দেখাও(ক);\n    }\n    ক = ক + ১;\n}",
            true
        );

        runTestCase(
            "TEST 8 — While inside If",
            "ধরি সংখ্যা ক = ১;\nযদি (ক == ১) {\n    ধরি সংখ্যা খ = ০;\n    যতক্ষণ (খ < ৩) {\n        দেখাও(খ);\n        খ = খ + ১;\n    }\n}",
            true
        );

        runTestCase(
            "TEST 9 — Nested Loops (While inside While)",
            "ধরি সংখ্যা ক = ০;\nযতক্ষণ (ক < ২) {\n    ধরি সংখ্যা খ = ০;\n    যতক্ষণ (খ < ২) {\n        দেখাও(খ);\n        খ = খ + ১;\n    }\n    ক = ক + ১;\n}",
            true
        );

        runTestCase(
            "TEST 9B — For inside While",
            "ধরি সংখ্যা ক = ০;\nযতক্ষণ (ক < ২) {\n    for (ধরি সংখ্যা খ = ০; খ < ২; খ = খ + ১) {\n        দেখাও(খ);\n    }\n    ক = ক + ১;\n}",
            true
        );

        runTestCase(
            "TEST 10 — Existing If-Else (Regression Verification)",
            "ধরি সংখ্যা ক = ১০;\nযদি (ক > ৫) {\n    দেখাও(ক);\n} নাহলে {\n    দেখাও(০);\n}",
            true
        );
    }

    private static void runTestCase(String title, String source, boolean testPythonWasm) {
        System.out.println("------------------------------------------------------------");
        System.out.println(title);
        System.out.println("------------------------------------------------------------");
        System.out.println("Source:");
        System.out.println(source);
        System.out.println();

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        if (lexer.hasErrors()) {
            System.out.println("LEXER ERROR:");
            for (String err : lexer.getErrors()) System.out.println(err);
            return;
        }

        Parser parser = new Parser(tokens);
        ProgramNode ast = parser.parseProgram();
        if (parser.hasErrors()) {
            System.out.println("PARSER ERROR:");
            for (String err : parser.getErrors()) System.out.println(err);
            return;
        }

        SemanticAnalyzer semantic = new SemanticAnalyzer();
        try {
            semantic.analyze(ast);
        } catch (Exception e) {
            System.out.println("SEMANTIC ERROR: " + e.getMessage());
            return;
        }

        TACGenerator tacGen = new TACGenerator();
        TACProgram tac = tacGen.generate(ast);
        System.out.println("Raw TAC:");
        System.out.print(tac.toRawString());
        System.out.println();
        System.out.println("Annotated TAC:");
        System.out.print(tac.toString());

        if (testPythonWasm) {
            PythonGenerator pyGen = new PythonGenerator();
            String pyCode = pyGen.generate(tac);
            System.out.println("Generated Python:");
            System.out.println(pyCode);

            WasmGenerator wasmGen = new WasmGenerator();
            String watCode = wasmGen.generate(ast);
            System.out.println("Generated WAT (WebAssembly):");
            System.out.println(watCode);
        }
        System.out.println("STATUS: PASSED ✓\n");
    }
}
