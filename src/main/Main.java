package main;

import lexer.Lexer;
import token.Token;

public class Main {

    public static void main(String[] args) {

        Lexer lexer = new Lexer();

        String code = "সংখ্যা বয়স = 20;";

        for (Token token : lexer.tokenize(code)) {
            System.out.println(token);
        }

    }

}
