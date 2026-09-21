package main;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ast.ASTPrinter;
import ast.ProgramNode;
import codegen.PythonGenerator;
import codegen.WasmGenerator;
import lexer.Lexer;
import parser.Parser;
import semantic.SemanticAnalyzer;
import semantic.Type;
import tac.TACGenerator;
import tac.TACProgram;
import token.NumberHelper;
import token.Token;
import token.TokenType;

/**
 * Borno Compiler Platform — Official Product Release
 *
 * An interactive Java-based compiler driver for the Borno Bangla programming language.
 */
public class Main {

    // ─── Box-drawing & Line separators ─────────────────────────────────────────
    static final String BOX_TOP    = "╔══════════════════════════════════════════╗";
    static final String BOX_BOT    = "╚══════════════════════════════════════════╝";
    static final String LINE_HEAVY = "============================================================";
    static final String LINE_LIGHT = "------------------------------------------------------------";

    // ─── Demo Source Code ──────────────────────────────────────────────────────
    static final String DEMO_SOURCE =
            "ধরি সংখ্যা বয়স = ২০;\n" +
            "ধরি বাক্য নাম = \"মাহদি\";\n" +
            "\n" +
            "যদি (বয়স >= ১৮) {\n" +
            "    ধরি সংখ্যা অবস্থা = ১;\n" +
            "}\n" +
            "নাহলে {\n" +
            "    ধরি সংখ্যা অবস্থা = ০;\n" +
            "}";

    public static void main(String[] args) {
        // Ensure UTF-8 output on standard out/err streams
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        while (true) {
            printMainMenu();
            System.out.print("Choose: ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleDemoMenu(scanner);
                case "2" -> handleCustomCode(scanner);
                case "3" -> runAutomatedTestSuite();
                case "4" -> {
                    System.out.println();
                    System.out.println("ধন্যবাদ! Borno Compiler বন্ধ হচ্ছে...");
                    System.out.println();
                    return;
                }
                default -> {
                    System.out.println();
                    System.out.println("অনুগ্রহ করে ১, ২, ৩ অথবা ৪ নির্বাচন করুন।");
                    System.out.println();
                }
            }
        }
    }

    // =========================================================================
    // 1. MAIN MENU
    // =========================================================================

    static void printMainMenu() {
        System.out.println();
        System.out.println(BOX_TOP);
        System.out.println("║        BORNO COMPILER PLATFORM           ║");
        System.out.println(BOX_BOT);
        System.out.println();
        System.out.println("1. Demo Test Case");
        System.out.println("2. Enter Your Own Bangla Code");
        System.out.println("3. Automated Test Suite");
        System.out.println("4. Exit");
        System.out.println();
    }

    // =========================================================================
    // 2. DEMO TEST CASE SUBMENU
    // =========================================================================

    static void handleDemoMenu(Scanner scanner) {
        while (true) {
            System.out.println();
            System.out.println(BOX_TOP);
            System.out.println("║             DEMO TEST CASE               ║");
            System.out.println(BOX_BOT);
            System.out.println();
            System.out.println("1. Show Source Code");
            System.out.println("2. Run Lexer");
            System.out.println("3. Run Parser");
            System.out.println("4. Run Semantic Analyzer");
            System.out.println("5. Generate Three Address Code (TAC)");
            System.out.println("6. Generate Python Target Code");
            System.out.println("7. Generate WebAssembly Target Code (Optional)");
            System.out.println("8. Run Full Pipeline");
            System.out.println("9. Back to Main Menu");
            System.out.println();
            System.out.print("Choose: ");
            if (!scanner.hasNextLine()) {
                return;
            }
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    System.out.println();
                    printSourceCode(DEMO_SOURCE);
                }
                case "2" -> {
                    System.out.println();
                    runLexerPhase(DEMO_SOURCE);
                }
                case "3" -> {
                    System.out.println();
                    runParserPhase(DEMO_SOURCE);
                }
                case "4" -> {
                    System.out.println();
                    runSemanticPhase(DEMO_SOURCE);
                }
                case "5" -> {
                    System.out.println();
                    runTACPhase(DEMO_SOURCE);
                }
                case "6" -> {
                    System.out.println();
                    runPythonPhase(DEMO_SOURCE);
                }
                case "7" -> {
                    System.out.println();
                    runWasmPhase(DEMO_SOURCE);
                }
                case "8" -> {
                    System.out.println();
                    runFullPipelinePhase(DEMO_SOURCE);
                }
                case "9" -> {
                    return;
                }
                default -> {
                    System.out.println();
                    System.out.println("অনুগ্রহ করে ১ থেকে ৯ এর মধ্যে নির্বাচন করুন।");
                }
            }
        }
    }

    // =========================================================================
    // 3. CUSTOM BANGLA CODE MODE
    // =========================================================================

    static void handleCustomCode(Scanner scanner) {
        System.out.println();
        System.out.println(BOX_TOP);
        System.out.println("║          CUSTOM BANGLA CODE              ║");
        System.out.println(BOX_BOT);
        System.out.println();
        System.out.println("Enter your Bangla source code.");
        System.out.println("Type END on a separate line to finish.");
        System.out.println();
        System.out.println("Example:");
        System.out.println();
        System.out.println("> ধরি সংখ্যা বয়স = ২০;");
        System.out.println("> ধরি বাক্য নাম = \"মাহদি\";");
        System.out.println("> যদি (বয়স >= ১৮) {");
        System.out.println(">     দেখাও(\"আপনি প্রাপ্তবয়স্ক\");");
        System.out.println("> }");
        System.out.println("> END");
        System.out.println();

        StringBuilder sb = new StringBuilder();
        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine();
            if (line.trim().equalsIgnoreCase("END")) {
                break;
            }
            sb.append(line).append("\n");
        }

        String customSource = sb.toString().trim();
        if (customSource.isEmpty()) {
            System.out.println();
            System.out.println("কোনো সোর্স কোড দেওয়া হয়নি।");
            return;
        }

        while (true) {
            System.out.println();
            System.out.println(BOX_TOP);
            System.out.println("║             CUSTOM CODE MENU             ║");
            System.out.println(BOX_BOT);
            System.out.println();
            System.out.println("1. Show Source Code");
            System.out.println("2. Run Lexer");
            System.out.println("3. Run Parser");
            System.out.println("4. Run Semantic Analyzer");
            System.out.println("5. Generate Three Address Code (TAC)");
            System.out.println("6. Generate Python Target Code");
            System.out.println("7. Generate WebAssembly Target Code (Optional)");
            System.out.println("8. Run Full Pipeline");
            System.out.println("9. Back");
            System.out.println();
            System.out.print("Choose: ");
            if (!scanner.hasNextLine()) {
                return;
            }
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    System.out.println();
                    printSourceCode(customSource);
                }
                case "2" -> {
                    System.out.println();
                    runLexerPhase(customSource);
                }
                case "3" -> {
                    System.out.println();
                    runParserPhase(customSource);
                }
                case "4" -> {
                    System.out.println();
                    runSemanticPhase(customSource);
                }
                case "5" -> {
                    System.out.println();
                    runTACPhase(customSource);
                }
                case "6" -> {
                    System.out.println();
                    runPythonPhase(customSource);
                }
                case "7" -> {
                    System.out.println();
                    runWasmPhase(customSource);
                }
                case "8" -> {
                    System.out.println();
                    runFullPipelinePhase(customSource);
                }
                case "9" -> {
                    return;
                }
                default -> {
                    System.out.println();
                    System.out.println("অনুগ্রহ করে ১ থেকে ৯ এর মধ্যে নির্বাচন করুন।");
                }
            }
        }
    }

    // =========================================================================
    // INDEPENDENT PIPELINE PHASES
    // =========================================================================

    /** Prints source code with clean line numbering. */
    static void printSourceCode(String source) {
        System.out.println("SOURCE CODE");
        System.out.println(LINE_LIGHT);
        String[] lines = source.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            System.out.printf("%2d | %s%n", i + 1, lines[i]);
        }
        System.out.println(LINE_LIGHT);
    }

    /** Runs ONLY the Lexer and displays tokens. */
    static void runLexerPhase(String source) {
        printSourceCode(source);
        System.out.println();
        System.out.println("LEXER OUTPUT");
        System.out.println(LINE_LIGHT);

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        int totalTokens = 0;
        for (Token t : tokens) {
            if (t.getType() == TokenType.EOF) continue;
            System.out.printf("%-14s : %-16s (লাইন %s)%n",
                    t.getType(), "'" + t.getValue() + "'", NumberHelper.toBangla(String.valueOf(t.getLine())));
            totalTokens++;
        }

        System.out.println();
        System.out.println("মোট টোকেন: " + NumberHelper.toBangla(String.valueOf(totalTokens)));
        System.out.println();

        if (lexer.hasErrors()) {
            System.out.println("Lexer Status: ERROR");
            System.out.println();
            for (String err : lexer.getErrors()) {
                System.out.println(err);
            }
        } else {
            System.out.println("Lexer Status: OK");
        }
    }

    /** Runs ONLY the Parser and displays the AST. */
    static void runParserPhase(String source) {
        printSourceCode(source);
        System.out.println();
        System.out.println("PARSER OUTPUT — ABSTRACT SYNTAX TREE");
        System.out.println(LINE_LIGHT);

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        if (lexer.hasErrors()) {
            System.out.println("[LEXICAL ERROR PREVENTS SYNTAX ANALYSIS]");
            for (String err : lexer.getErrors()) {
                System.out.println(err);
            }
            System.out.println();
            System.out.println("Parser Status: NOT COMPLETED / BLOCKED BY LEXICAL ERROR");
            return;
        }

        Parser parser = new Parser(tokens);
        ProgramNode ast = parser.parseProgram();

        new ASTPrinter().print(ast);
        System.out.println();

        if (parser.hasErrors()) {
            System.out.println("Parser Status: ERROR");
            System.out.println();
            for (String err : parser.getErrors()) {
                System.out.println(err);
                System.out.println();
            }
        } else {
            System.out.println("Parser Status: OK");
        }
    }

    /** Runs ONLY the Semantic Analyzer and displays the Symbol Table & checks. */
    static void runSemanticPhase(String source) {
        printSourceCode(source);
        System.out.println();
        System.out.println("SEMANTIC ANALYSIS");
        System.out.println(LINE_LIGHT);

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        if (lexer.hasErrors()) {
            System.out.println("[LEXICAL ERROR PREVENTS SEMANTIC ANALYSIS]");
            for (String err : lexer.getErrors()) {
                System.out.println(err);
            }
            System.out.println();
            System.out.println("Semantic Status: NOT COMPLETED / BLOCKED BY LEXICAL ERROR");
            return;
        }

        Parser parser = new Parser(tokens);
        ProgramNode ast = parser.parseProgram();

        if (parser.hasErrors()) {
            System.out.println("[SYNTAX ERROR PREVENTS SEMANTIC ANALYSIS]");
            for (String err : parser.getErrors()) {
                System.out.println(err);
            }
            System.out.println();
            System.out.println("Semantic Status: NOT COMPLETED / BLOCKED BY SYNTAX ERROR");
            return;
        }

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        boolean hasSemanticError = false;
        String errorMessage = null;

        try {
            analyzer.analyze(ast);
        } catch (RuntimeException e) {
            hasSemanticError = true;
            errorMessage = e.getMessage();
        }

        // Print Symbol Table if we have symbols or if no fatal error occurred before collecting
        Map<String, Type> symbols = analyzer.getAllSymbols();
        if (!symbols.isEmpty()) {
            System.out.println("Symbol Table:");
            System.out.println();
            System.out.printf("%-10s | %-6s | %-11s%n", "Name", "Type", "Initialized");
            System.out.println("-----------+--------+------------");
            for (Map.Entry<String, Type> entry : symbols.entrySet()) {
                System.out.printf("%-10s | %-6s | হ্যাঁ%n",
                        entry.getKey(), entry.getValue().toBanglaString());
            }
            System.out.println();
        }

        // Print individual check logs
        List<String> checkLogs = analyzer.getCheckLogs();
        if (!checkLogs.isEmpty()) {
            for (String log : checkLogs) {
                System.out.println(log);
            }
            System.out.println();
        }

        if (hasSemanticError) {
            System.out.println(errorMessage != null ? errorMessage : "[SEMANTIC ERROR] Semantic analysis failed.");
            System.out.println();
            System.out.println("Semantic Status: ERROR");
        } else {
            System.out.println("Semantic analysis completed successfully.");
        }
    }

    /** Runs ONLY the TAC phase (Lexer -> Parser -> Semantic -> TAC) */
    static void runTACPhase(String source) {
        printSourceCode(source);
        System.out.println();
        System.out.println("THREE ADDRESS CODE (TAC)");
        System.out.println(LINE_LIGHT);

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        if (lexer.hasErrors()) {
            System.out.println("[LEXICAL ERROR PREVENTS TAC GENERATION]");
            for (String err : lexer.getErrors()) {
                System.out.println(err);
            }
            System.out.println();
            System.out.println("TAC Status: NOT COMPLETED / BLOCKED BY LEXICAL ERROR");
            return;
        }

        Parser parser = new Parser(tokens);
        ProgramNode ast = parser.parseProgram();

        if (parser.hasErrors()) {
            System.out.println("[SYNTAX ERROR PREVENTS TAC GENERATION]");
            for (String err : parser.getErrors()) {
                System.out.println(err);
            }
            System.out.println();
            System.out.println("TAC Status: NOT COMPLETED / BLOCKED BY SYNTAX ERROR");
            return;
        }

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        try {
            analyzer.analyze(ast);
        } catch (RuntimeException e) {
            System.out.println("[SEMANTIC ERROR PREVENTS TAC GENERATION]");
            System.out.println(e.getMessage());
            System.out.println();
            System.out.println("TAC Status: NOT COMPLETED / BLOCKED BY SEMANTIC ERROR");
            return;
        }

        TACGenerator tacGen = new TACGenerator();
        TACProgram tac = tacGen.generate(ast);
        System.out.print(tac.toString());
        System.out.println();

        try {
            File tacFile = TACGenerator.writeToFile(tac, "output/program.tac");
            System.out.println("Generated: " + tacFile.getPath().replace('\\', '/'));
        } catch (IOException e) {
            System.out.println("Warning: Could not save output/program.tac: " + e.getMessage());
        }

        System.out.println("TAC Status: OK");
    }

    /** Runs ONLY the Python Target Code generation phase */
    static void runPythonPhase(String source) {
        printSourceCode(source);
        System.out.println();
        System.out.println("PYTHON TARGET CODE");
        System.out.println(LINE_LIGHT);

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        if (lexer.hasErrors()) {
            System.out.println("[LEXICAL ERROR PREVENTS PYTHON CODE GENERATION]");
            for (String err : lexer.getErrors()) {
                System.out.println(err);
            }
            System.out.println();
            System.out.println("Python Target Code Status: NOT COMPLETED / BLOCKED BY LEXICAL ERROR");
            return;
        }

        Parser parser = new Parser(tokens);
        ProgramNode ast = parser.parseProgram();

        if (parser.hasErrors()) {
            System.out.println("[SYNTAX ERROR PREVENTS PYTHON CODE GENERATION]");
            for (String err : parser.getErrors()) {
                System.out.println(err);
            }
            System.out.println();
            System.out.println("Python Target Code Status: NOT COMPLETED / BLOCKED BY SYNTAX ERROR");
            return;
        }

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        try {
            analyzer.analyze(ast);
        } catch (RuntimeException e) {
            System.out.println("[SEMANTIC ERROR PREVENTS PYTHON CODE GENERATION]");
            System.out.println(e.getMessage());
            System.out.println();
            System.out.println("Python Target Code Status: NOT COMPLETED / BLOCKED BY SEMANTIC ERROR");
            return;
        }

        PythonGenerator pyGen = new PythonGenerator();
        String pythonCode = pyGen.generate(ast);
        System.out.print(pythonCode);
        System.out.println();

        try {
            File pyFile = PythonGenerator.writeToFile(pythonCode, "output/program.py");
            System.out.println("Generated: " + pyFile.getPath().replace('\\', '/'));
        } catch (IOException e) {
            System.out.println("Warning: Could not save output/program.py: " + e.getMessage());
        }

        System.out.println("Python Target Code Status: OK");
    }

    /** Runs ONLY the WebAssembly Target Code generation phase */
    static void runWasmPhase(String source) {
        printSourceCode(source);
        System.out.println();
        System.out.println("WEBASSEMBLY TARGET CODE (.wat)");
        System.out.println(LINE_LIGHT);

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        if (lexer.hasErrors()) {
            System.out.println("[LEXICAL ERROR PREVENTS WEBASSEMBLY CODE GENERATION]");
            for (String err : lexer.getErrors()) {
                System.out.println(err);
            }
            System.out.println();
            System.out.println("WebAssembly Target Code Status: NOT COMPLETED / BLOCKED BY LEXICAL ERROR");
            return;
        }

        Parser parser = new Parser(tokens);
        ProgramNode ast = parser.parseProgram();

        if (parser.hasErrors()) {
            System.out.println("[SYNTAX ERROR PREVENTS WEBASSEMBLY CODE GENERATION]");
            for (String err : parser.getErrors()) {
                System.out.println(err);
            }
            System.out.println();
            System.out.println("WebAssembly Target Code Status: NOT COMPLETED / BLOCKED BY SYNTAX ERROR");
            return;
        }

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        try {
            analyzer.analyze(ast);
        } catch (RuntimeException e) {
            System.out.println("[SEMANTIC ERROR PREVENTS WEBASSEMBLY CODE GENERATION]");
            System.out.println(e.getMessage());
            System.out.println();
            System.out.println("WebAssembly Target Code Status: NOT COMPLETED / BLOCKED BY SEMANTIC ERROR");
            return;
        }

        WasmGenerator wasmGen = new WasmGenerator();
        String watCode = wasmGen.generate(ast);
        System.out.print(watCode);
        System.out.println();

        try {
            File watFile = WasmGenerator.writeToFile(watCode, "output/program.wat");
            System.out.println("Generated: " + watFile.getPath().replace('\\', '/'));

            boolean wasmOk = WasmGenerator.compileWatToWasm("output/program.wat", "output/program.wasm");
            if (wasmOk) {
                System.out.println("Compiled: output/program.wasm (via wat2wasm)");
            } else {
                System.out.println("[Optional WASM Binary]: wat2wasm tool not installed or skipped; .wat output generated successfully.");
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not save output/program.wat: " + e.getMessage());
        }

        System.out.println("WebAssembly Target Code Status: OK");
    }

    /**
     * Runs the FULL COMPILER PIPELINE sequentially:
     * 1. Lexical Analysis (Tokens)
     * 2. Syntax Analysis (AST)
     * 3. Semantic Analysis (Symbol Table & Type Checks)
     * 4. Three Address Code Generation (TAC)
     * 5. Python Target Code Generation
     * 6. WebAssembly Target Code Generation
     */
    static void runFullPipelinePhase(String source) {
        System.out.println(LINE_HEAVY);
        System.out.println("     FULL PIPELINE EXECUTION (LEXER -> PARSER -> SEMANTIC -> TAC -> PYTHON -> WASM)");
        System.out.println(LINE_HEAVY);
        System.out.println();
        printSourceCode(source);

        // -------------------------------------------------------------
        // PHASE 1: LEXICAL ANALYSIS
        // -------------------------------------------------------------
        System.out.println();
        System.out.println("============================================================");
        System.out.println("PHASE 1: LEXICAL ANALYSIS (লেক্সিক্যাল অ্যানালাইসিস)");
        System.out.println("============================================================");

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        int totalTokens = 0;
        for (Token t : tokens) {
            if (t.getType() == TokenType.EOF) continue;
            System.out.printf("%-14s : %-16s (লাইন %s)%n",
                    t.getType(), "'" + t.getValue() + "'", NumberHelper.toBangla(String.valueOf(t.getLine())));
            totalTokens++;
        }

        System.out.println();
        System.out.println("মোট টোকেন: " + NumberHelper.toBangla(String.valueOf(totalTokens)));
        System.out.println();

        if (lexer.hasErrors()) {
            System.out.println("Lexer Status: ERROR ✗");
            System.out.println();
            for (String err : lexer.getErrors()) {
                System.out.println(err);
            }
            System.out.println();
            System.out.println(LINE_HEAVY);
            System.out.println("PIPELINE HALTED: Lexical errors detected. Later phases skipped.");
            System.out.println(LINE_HEAVY);
            System.out.println();
            System.out.println("========== FULL PIPELINE RESULT ==========");
            System.out.println("Status: ERROR (Failed at Lexical Analysis ✗)");
            System.out.println(LINE_HEAVY);
            return;
        } else {
            System.out.println("Lexer Status: OK ✓");
        }

        // -------------------------------------------------------------
        // PHASE 2: SYNTAX ANALYSIS (PARSER)
        // -------------------------------------------------------------
        System.out.println();
        System.out.println("============================================================");
        System.out.println("PHASE 2: SYNTAX ANALYSIS (সিনট্যাক্স অ্যানালাইসিস / AST)");
        System.out.println("============================================================");

        Parser parser = new Parser(tokens);
        ProgramNode ast = parser.parseProgram();

        new ASTPrinter().print(ast);
        System.out.println();

        if (parser.hasErrors()) {
            System.out.println("Parser Status: ERROR ✗");
            System.out.println();
            for (String err : parser.getErrors()) {
                System.out.println(err);
                System.out.println();
            }
            System.out.println(LINE_HEAVY);
            System.out.println("PIPELINE HALTED: Syntax errors detected. Later phases skipped.");
            System.out.println(LINE_HEAVY);
            System.out.println();
            System.out.println("========== FULL PIPELINE RESULT ==========");
            System.out.println("Status: ERROR (Failed at Syntax Analysis ✗)");
            System.out.println(LINE_HEAVY);
            return;
        } else {
            System.out.println("Parser Status: OK ✓");
        }

        // -------------------------------------------------------------
        // PHASE 3: SEMANTIC ANALYSIS
        // -------------------------------------------------------------
        System.out.println();
        System.out.println("============================================================");
        System.out.println("PHASE 3: SEMANTIC ANALYSIS (সিম্যান্টিক অ্যানালাইসিস)");
        System.out.println("============================================================");

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        boolean hasSemanticError = false;
        String errorMessage = null;

        try {
            analyzer.analyze(ast);
        } catch (RuntimeException e) {
            hasSemanticError = true;
            errorMessage = e.getMessage();
        }

        Map<String, Type> symbols = analyzer.getAllSymbols();
        if (!symbols.isEmpty()) {
            System.out.println("Symbol Table:");
            System.out.println();
            System.out.printf("%-10s | %-6s | %-11s%n", "Name", "Type", "Initialized");
            System.out.println("-----------+--------+------------");
            for (Map.Entry<String, Type> entry : symbols.entrySet()) {
                System.out.printf("%-10s | %-6s | হ্যাঁ%n",
                        entry.getKey(), entry.getValue().toBanglaString());
            }
            System.out.println();
        }

        List<String> checkLogs = analyzer.getCheckLogs();
        if (!checkLogs.isEmpty()) {
            for (String log : checkLogs) {
                System.out.println(log);
            }
            System.out.println();
        }

        if (hasSemanticError) {
            System.out.println(errorMessage != null ? errorMessage : "[SEMANTIC ERROR] Semantic analysis failed.");
            System.out.println();
            System.out.println("Semantic Status: ERROR ✗");
            System.out.println();
            System.out.println(LINE_HEAVY);
            System.out.println("PIPELINE HALTED: Semantic errors detected. Code generation phases skipped.");
            System.out.println(LINE_HEAVY);
            System.out.println();
            System.out.println("========== FULL PIPELINE RESULT ==========");
            System.out.println("Status: ERROR (Failed at Semantic Analysis ✗)");
            System.out.println(LINE_HEAVY);
            return;
        } else {
            System.out.println("Semantic analysis completed successfully.");
            System.out.println("Semantic Status: OK ✓");
        }

        // -------------------------------------------------------------
        // PHASE 4: THREE ADDRESS CODE (TAC) GENERATION
        // -------------------------------------------------------------
        System.out.println();
        System.out.println("============================================================");
        System.out.println("PHASE 4: THREE ADDRESS CODE (TAC) (ইন্টারমিডিয়েট কোড)");
        System.out.println("============================================================");

        TACGenerator tacGen = new TACGenerator();
        TACProgram tac = tacGen.generate(ast);
        System.out.print(tac.toString());
        System.out.println();

        try {
            File tacFile = TACGenerator.writeToFile(tac, "output/program.tac");
            System.out.println("Generated: " + tacFile.getPath().replace('\\', '/'));
        } catch (IOException e) {
            System.out.println("Warning: Could not save output/program.tac: " + e.getMessage());
        }
        System.out.println("TAC Status: OK ✓");

        // -------------------------------------------------------------
        // PHASE 5: PYTHON TARGET CODE GENERATION
        // -------------------------------------------------------------
        System.out.println();
        System.out.println("============================================================");
        System.out.println("PHASE 5: PYTHON TARGET CODE (পাইথন টার্গেট কোড)");
        System.out.println("============================================================");

        PythonGenerator pyGen = new PythonGenerator();
        String pyCode = pyGen.generate(ast);
        System.out.print(pyCode);
        System.out.println();

        try {
            File pyFile = PythonGenerator.writeToFile(pyCode, "output/program.py");
            System.out.println("Generated: " + pyFile.getPath().replace('\\', '/'));
        } catch (IOException e) {
            System.out.println("Warning: Could not save output/program.py: " + e.getMessage());
        }
        System.out.println("Python Status: OK ✓");

        // -------------------------------------------------------------
        // PHASE 6: WEBASSEMBLY TARGET CODE GENERATION
        // -------------------------------------------------------------
        System.out.println();
        System.out.println("============================================================");
        System.out.println("PHASE 6: WEBASSEMBLY TARGET CODE (.wat) (ওয়েবঅ্যাসেম্বলি)");
        System.out.println("============================================================");

        WasmGenerator wasmGen = new WasmGenerator();
        String watCode = wasmGen.generate(ast);
        System.out.print(watCode);
        System.out.println();

        try {
            File watFile = WasmGenerator.writeToFile(watCode, "output/program.wat");
            System.out.println("Generated: " + watFile.getPath().replace('\\', '/'));

            boolean wasmOk = WasmGenerator.compileWatToWasm("output/program.wat", "output/program.wasm");
            if (wasmOk) {
                System.out.println("Compiled: output/program.wasm (via wat2wasm)");
            } else {
                System.out.println("[Optional WASM Binary]: wat2wasm tool not installed or skipped; .wat output generated successfully.");
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not save output/program.wat: " + e.getMessage());
        }
        System.out.println("WebAssembly Status: OK ✓");

        // -------------------------------------------------------------
        // FULL PIPELINE RESULT
        // -------------------------------------------------------------
        System.out.println();
        System.out.println(LINE_HEAVY);
        System.out.println("========== FULL PIPELINE RESULT ==========");
        System.out.println("Status: OK");
        System.out.println("All 6 phases completed successfully ✓");
        System.out.println(LINE_HEAVY);
    }

    // =========================================================================
    // 4. AUTOMATED TEST SUITE
    // =========================================================================

    static void runAutomatedTestSuite() {
        System.out.println();
        System.out.println(LINE_HEAVY);
        System.out.println("AUTOMATED TEST SUITE — BORNO COMPILER");
        System.out.println(LINE_HEAVY);
        System.out.println();

        int passed = 0;
        int failed = 0;
        int errorsFoundCount = 0;
        int noErrorsCount = 0;

        TestCase[] testCases = new TestCase[]{
            // ── Bangla-Only Rule Enforcement Tests ─────────────────────────
            new TestCase(
                1,
                "Bangla Digits Accepted",
                "ধরি সংখ্যা বয়স = ২০;",
                "Ensuring Bangla digits (০-৯) are correctly accepted as numeric literals.",
                "No errors — Bangla digits accepted successfully",
                TestExpectation.SUCCESS,
                null
            ),
            new TestCase(
                2,
                "English Digits Rejected",
                "ধরি সংখ্যা বয়স = 20;",
                "Enforcing rejection of ASCII/English digits (0-9) as numeric literals at the Lexer stage.",
                "Lexical Error — English digit '2' is not allowed. Use Bangla digits (০-৯).",
                TestExpectation.LEXICAL_ERROR,
                "English digit '2' is not allowed"
            ),
            new TestCase(
                3,
                "Bangla Identifier Accepted",
                "ধরি সংখ্যা বয়স = ২০;",
                "Ensuring identifiers containing Bangla Unicode characters are valid.",
                "No errors — Bangla identifier accepted successfully",
                TestExpectation.SUCCESS,
                null
            ),
            new TestCase(
                4,
                "English Identifier Rejected",
                "ধরি সংখ্যা age = ২০;",
                "Enforcing rejection of English identifiers (age) at the Lexer stage.",
                "Lexical Error — English identifier/keyword 'age' is not allowed. Use Bangla characters.",
                TestExpectation.LEXICAL_ERROR,
                "English identifier/keyword 'age' is not allowed"
            ),
            new TestCase(
                5,
                "Mixed Identifier Rejected",
                "ধরি সংখ্যা বয়স123 = ২০;",
                "Enforcing rejection of mixed Bangla-English identifiers (বয়স123) at the Lexer stage.",
                "Lexical Error — Mixed identifier 'বয়স123' is not allowed. Identifiers must be strictly Bangla.",
                TestExpectation.LEXICAL_ERROR,
                "Mixed identifier 'বয়স123' is not allowed"
            ),
            new TestCase(
                6,
                "English Keyword Rejected",
                "if (বয়স >= ১৮) {\n    দেখাও(\"হ্যালো\");\n}",
                "Enforcing rejection of English keywords (if) at the Lexer stage.",
                "Lexical Error — English identifier/keyword 'if' is not allowed. Use Bangla characters.",
                TestExpectation.LEXICAL_ERROR,
                "English identifier/keyword 'if' is not allowed"
            ),
            new TestCase(
                7,
                "Mixed English/Bangla Code Rejected",
                "dhori সংখ্যা বয়স = ২০;",
                "Enforcing rejection of transliterated English keywords (dhori) at the Lexer stage.",
                "Lexical Error — English identifier/keyword 'dhori' is not allowed. Use Bangla characters.",
                TestExpectation.LEXICAL_ERROR,
                "English identifier/keyword 'dhori' is not allowed"
            ),
            new TestCase(
                8,
                "Completely Valid Bangla Program",
                "ধরি সংখ্যা বয়স = ২০;\n" +
                "ধরি বাক্য নাম = \"মাহদি\";\n" +
                "\n" +
                "যদি (বয়স >= ১৮) {\n" +
                "    দেখাও(নাম);\n" +
                "}\n" +
                "নাহলে {\n" +
                "    দেখাও(\"অপ্রাপ্তবয়স্ক\");\n" +
                "}",
                "Full program with declarations, IF-ELSE logic, and দেখাও print statements in pure Bangla.",
                "No errors — Program compiles successfully",
                TestExpectation.SUCCESS,
                null
            ),

            // ── Semantic & Syntax Error Tests ───────────────────────────────
            new TestCase(
                9,
                "Type Mismatch (সংখ্যা = বাক্য)",
                "ধরি সংখ্যা বয়স = \"মাহদি\";",
                "Assigning a string (বাক্য) literal to a numeric (সংখ্যা) variable.",
                "Semantic Error — Type mismatch: 'বয়স' এর ধরন সংখ্যা, কিন্তু expression এর ধরন বাক্য",
                TestExpectation.SEMANTIC_ERROR,
                "Type mismatch"
            ),
            new TestCase(
                10,
                "Division by Zero (১০ / ০)",
                "ধরি সংখ্যা ফলাফল = ১০ / ০;",
                "Detection of compile-time division by zero in arithmetic expression.",
                "Semantic Error — Division by zero is not allowed.",
                TestExpectation.SEMANTIC_ERROR,
                "Division by zero is not allowed."
            ),
            new TestCase(
                11,
                "Undeclared Variable in Statement",
                "দেখাও(নাম);",
                "Attempting to use an undeclared variable 'নাম' inside a দেখাও statement.",
                "Semantic Error — Undeclared variable: 'নাম'",
                TestExpectation.SEMANTIC_ERROR,
                "Undeclared variable: 'নাম'"
            ),
            new TestCase(
                12,
                "Undeclared Variable in Expression",
                "ধরি সংখ্যা ফলাফল = বয়স + ১০;",
                "Attempting to reference an undeclared variable 'বয়স' inside an arithmetic expression.",
                "Semantic Error — Undeclared variable: 'বয়স'",
                TestExpectation.SEMANTIC_ERROR,
                "Undeclared variable: 'বয়স'"
            ),
            new TestCase(
                13,
                "Duplicate Variable Declaration",
                "ধরি সংখ্যা বয়স = ২০;\n" +
                "ধরি সংখ্যা বয়স = ৩০;",
                "Declaring the same variable name 'বয়স' multiple times within the same scope.",
                "Semantic Error — Duplicate variable declaration: 'বয়স'",
                TestExpectation.SEMANTIC_ERROR,
                "Duplicate variable declaration: 'বয়স'"
            ),
            new TestCase(
                14,
                "Invalid IF Condition (Non-Boolean)",
                "ধরি সংখ্যা বয়স = ২০;\n" +
                "যদি (বয়স) {\n" +
                "    দেখাও(\"হ্যালো\");\n" +
                "}",
                "Using a non-boolean numeric variable as an IF condition.",
                "Semantic Error — IF condition-এর type BOOLEAN হতে হবে",
                TestExpectation.SEMANTIC_ERROR,
                "IF condition-এর type BOOLEAN হতে হবে"
            ),
            new TestCase(
                15,
                "Syntax Error — Missing Semicolon",
                "ধরি সংখ্যা বয়স = ২০",
                "Missing semicolon (;) at the end of a variable declaration statement.",
                "Syntax Error — ';' আশা করা হয়েছিল declaration-এর শেষে",
                TestExpectation.SYNTAX_ERROR,
                "';' আশা করা হয়েছিল"
            ),
            new TestCase(
                16,
                "Lexical Error — Invalid Character ($)",
                "ধরি সংখ্যা $বয়স = ২০;",
                "Encountering an unrecognized character '$' in variable declaration.",
                "Lexical Error — অপরিচিত ক্যারেক্টার '$'",
                TestExpectation.LEXICAL_ERROR,
                "অপরিচিত ক্যারেক্টার '$'"
            ),
            new TestCase(
                17,
                "Lexical Error — Unclosed String Literal",
                "ধরি বাক্য বার্তা = \"অসমাপ্ত বাক্য;",
                "Unterminated string literal missing a closing double quote.",
                "Lexical Error — স্ট্রিং সমাপ্ত করা হয়নি (Unclosed string literal)",
                TestExpectation.LEXICAL_ERROR,
                "স্ট্রিং সমাপ্ত করা হয়নি"
            ),

            // ── Additional Valid Test Cases ─────────────────────────────────
            new TestCase(
                18,
                "Valid Variable Declaration (সংখ্যা ও বাক্য)",
                "ধরি সংখ্যা বয়স = ২৫;\n" +
                "ধরি বাক্য নাম = \"রাকিব\";",
                "Valid declaration and initialization of numeric (সংখ্যা) and string (বাক্য) variables.",
                "No errors — Program compiles successfully",
                TestExpectation.SUCCESS,
                null
            ),
            new TestCase(
                19,
                "Valid Arithmetic Expression with Precedence",
                "ধরি সংখ্যা যোগফল = ১০ + ২০ * ৩ - ৫;",
                "Valid compound arithmetic expression evaluating operator precedence (+, *, -).",
                "No errors — Program compiles successfully",
                TestExpectation.SUCCESS,
                null
            ),
            new TestCase(
                20,
                "Valid String Variable & Print Statement",
                "ধরি বাক্য বার্তা = \"স্বাগতম বর্ণ কম্পাইলারে\";\n" +
                "দেখাও(বার্তা);",
                "Declaring a string variable and printing it with the দেখাও statement.",
                "No errors — Program compiles successfully",
                TestExpectation.SUCCESS,
                null
            ),
            new TestCase(
                21,
                "Valid Nested IF-ELSE and Block Scoping",
                "ধরি সংখ্যা ক = ১০;\n" +
                "যদি (ক > ৫) {\n" +
                "    ধরি সংখ্যা খ = ২০;\n" +
                "    যদি (খ > ১৫) {\n" +
                "        ধরি সংখ্যা গ = ক + খ;\n" +
                "        দেখাও(\"সফল\");\n" +
                "    }\n" +
                "}",
                "Valid nested IF conditions and proper block-level variable scoping.",
                "No errors — Program compiles successfully",
                TestExpectation.SUCCESS,
                null
            ),
            new TestCase(
                22,
                "Complete Valid Demo Program",
                DEMO_SOURCE,
                "Complete multi-statement demo program with declarations, condition branching, and scopes.",
                "No errors — Program compiles successfully",
                TestExpectation.SUCCESS,
                null
            ),

            // ── Intentional Failure Test (Demonstrating Fail Detection) ─────
            new TestCase(
                23,
                "Intentional Failure Demo [INTENTIONAL FAILURE TEST]",
                "ধরি সংখ্যা ফলাফল = ১০ / ২;",
                "Intentional failure demonstration: valid arithmetic expression tested against an expected division-by-zero error to demonstrate fail reporting.",
                "Semantic Error — Division by zero is not allowed.",
                TestExpectation.SEMANTIC_ERROR,
                "Division by zero is not allowed."
            )
        };

        for (TestCase tc : testCases) {
            TestRunResult res = runSingleTestCase(tc);
            if (res.passed) {
                passed++;
            } else {
                failed++;
            }
            if (res.actualErrorFound) {
                errorsFoundCount++;
            } else {
                noErrorsCount++;
            }
        }

        System.out.println(LINE_HEAVY);
        System.out.println("AUTOMATED TEST SUMMARY");
        System.out.println(LINE_HEAVY);
        System.out.printf("Total Tests    : %d%n", testCases.length);
        System.out.printf("Passed         : %d%n", passed);
        System.out.printf("Failed         : %d%n", failed);
        System.out.printf("Errors Found   : %d%n", errorsFoundCount);
        System.out.printf("No Errors      : %d%n", noErrorsCount);
        System.out.println(LINE_HEAVY);
        System.out.println();
        if (failed == 0) {
            System.out.println("All tests passed ✓");
        } else {
            System.out.printf("Result: %d/%d Passed (%d Intentional Failure for Demo) ✗%n",
                    passed, testCases.length, failed);
        }
        System.out.println();
    }

    enum TestExpectation {
        SUCCESS,
        SEMANTIC_ERROR,
        SYNTAX_ERROR,
        LEXICAL_ERROR
    }

    static class TestCase {
        int id;
        String name;
        String source;
        String description;
        String expectedDesc;
        TestExpectation expectation;
        String errorSubstring;

        TestCase(int id, String name, String source, String description,
                 String expectedDesc, TestExpectation expectation, String errorSubstring) {
            this.id = id;
            this.name = name;
            this.source = source;
            this.description = description;
            this.expectedDesc = expectedDesc;
            this.expectation = expectation;
            this.errorSubstring = errorSubstring;
        }
    }

    static class TestRunResult {
        boolean passed;
        boolean actualErrorFound;

        TestRunResult(boolean passed, boolean actualErrorFound) {
            this.passed = passed;
            this.actualErrorFound = actualErrorFound;
        }
    }

    static TestRunResult runSingleTestCase(TestCase tc) {
        System.out.println(LINE_HEAVY);
        System.out.printf("TEST CASE %d — %s%n", tc.id, tc.name);
        System.out.println(LINE_HEAVY);
        System.out.println();
        printSourceCode(tc.source);
        System.out.println();
        System.out.println("TEST:");
        System.out.println(tc.description);
        System.out.println();
        System.out.println("EXPECTED:");
        System.out.println(tc.expectedDesc);
        System.out.println();

        Lexer lexer = new Lexer(tc.source);
        List<Token> tokens = lexer.tokenize();

        // 1. Check Lexical Phase
        if (lexer.hasErrors()) {
            String lexErr = String.join("\n", lexer.getErrors());
            System.out.println("ACTUAL:");
            System.out.println(lexErr);
            System.out.println();

            boolean matched = tc.expectation == TestExpectation.LEXICAL_ERROR &&
                    (tc.errorSubstring == null || lexErr.contains(tc.errorSubstring));

            System.out.println("ERROR FOUND:");
            if (matched) {
                System.out.println("YES ✓");
            } else {
                System.out.println("UNEXPECTED ERROR FOUND ✗");
            }
            System.out.println();
            System.out.println("RESULT:");
            System.out.println(matched ? "✓ PASS" : "✗ FAIL");
            System.out.println();
            return new TestRunResult(matched, true);
        }

        // 2. Check Parser Phase
        Parser parser = new Parser(tokens);
        ProgramNode ast = parser.parseProgram();

        if (parser.hasErrors()) {
            String synErr = String.join("\n", parser.getErrors());
            System.out.println("ACTUAL:");
            System.out.println(synErr);
            System.out.println();

            boolean matched = tc.expectation == TestExpectation.SYNTAX_ERROR &&
                    (tc.errorSubstring == null || synErr.contains(tc.errorSubstring));

            System.out.println("ERROR FOUND:");
            if (matched) {
                System.out.println("YES ✓");
            } else {
                System.out.println("UNEXPECTED ERROR FOUND ✗");
            }
            System.out.println();
            System.out.println("RESULT:");
            System.out.println(matched ? "✓ PASS" : "✗ FAIL");
            System.out.println();
            return new TestRunResult(matched, true);
        }

        // 3. Check Semantic Phase
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        try {
            analyzer.analyze(ast);

            System.out.println("ACTUAL:");
            System.out.println("No errors found.");
            System.out.println();

            if (tc.expectation == TestExpectation.SUCCESS) {
                System.out.println("ERROR FOUND:");
                System.out.println("NO ✓");
                System.out.println();
                System.out.println("RESULT:");
                System.out.println("✓ PASS");
                System.out.println();
                return new TestRunResult(true, false);
            } else {
                // Expected an error, but none occurred (e.g. intentional failure test)
                System.out.println("ERROR FOUND:");
                System.out.println("NO ✗");
                System.out.println();
                System.out.println("RESULT:");
                System.out.println("✗ FAIL");
                System.out.println();
                return new TestRunResult(false, false);
            }

        } catch (RuntimeException e) {
            String semErr = e.getMessage() != null ? e.getMessage() : "Unknown semantic error";
            System.out.println("ACTUAL:");
            System.out.println(semErr);
            System.out.println();

            boolean matched = tc.expectation == TestExpectation.SEMANTIC_ERROR &&
                    (tc.errorSubstring == null || semErr.contains(tc.errorSubstring));

            System.out.println("ERROR FOUND:");
            if (matched) {
                System.out.println("YES ✓");
            } else {
                System.out.println("UNEXPECTED ERROR FOUND ✗");
            }
            System.out.println();
            System.out.println("RESULT:");
            System.out.println(matched ? "✓ PASS" : "✗ FAIL");
            System.out.println();
            return new TestRunResult(matched, true);
        }
    }
}
