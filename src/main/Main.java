package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lexer.Lexer;
import token.Token;

public class Main {

    public static void main(String[] args) {
        String[] files = {
            "examples/test1.brn",
            "examples/test2.brn",
            "examples/test3.brn",
            "examples/test4.brn"
        };

        for (String file : files) {
            System.out.println("--- Running " + file + " ---");
            try {
                String source = Files.readString(Path.of(file));
                Lexer lexer = new Lexer(source);
                for (Token token : lexer.tokenize()) {
                    System.out.println(token);
                }
            } catch (IOException e) {
                System.err.println("Error reading file " + file + ": " + e.getMessage());
            }
            System.out.println();
        }
    }

}
