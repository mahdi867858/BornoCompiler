package main;

import lexer.Lexer;
import token.Token;

public class Main {

    public static void main(String[] args) {

        System.out.println("--- Test 1 ---");
        runTest("সংখ্যা a = 10;");

        System.out.println("\n--- Test 2 ---");
        runTest("লেখা নাম = \"Mahdi\";");

        System.out.println("\n--- Test 3 ---");
        runTest("""
        যদি (a >= 10)
        {
            দেখাও("OK");
        }
        """);
    }

    private static void runTest(String code) {
        Lexer lexer = new Lexer(code);
        for (Token token : lexer.tokenize()) {
            System.out.println(token);
        }
    }

}
