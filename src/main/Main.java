package main;

import lexer.Lexer;
import token.Token;

public class Main {

    public static void main(String[] args) {

        String code = """
        সংখ্যা বয়স = 20;

        যদি (বয়স >= 18)
        {
            দেখাও("Adult");
        }
        """;

        Lexer lexer = new Lexer(code);

        for (Token token : lexer.tokenize()) {
            System.out.println(token);
        }

    }

}
