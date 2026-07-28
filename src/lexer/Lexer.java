package lexer;

import java.util.ArrayList;
import java.util.List;

import token.Token;
import token.TokenType;

public class Lexer {

    public List<Token> tokenize(String source) {

        List<Token> tokens = new ArrayList<>();

        String[] words = source.split("\\s+");

        for (String word : words) {

            if (word.equals("সংখ্যা")) {
                tokens.add(new Token(TokenType.KEYWORD, word));
            }

            else if (word.matches("[0-9]+;?")) {

                if (word.endsWith(";")) {

                    tokens.add(new Token(
                            TokenType.NUMBER,
                            word.substring(0, word.length() - 1)));

                    tokens.add(new Token(TokenType.SEMICOLON, ";"));

                } else {
                    tokens.add(new Token(TokenType.NUMBER, word));
                }

            }

            else if (word.equals("=")) {
                tokens.add(new Token(TokenType.ASSIGN, word));
            }

            else {
                tokens.add(new Token(TokenType.IDENTIFIER, word));
            }

        }

        tokens.add(new Token(TokenType.EOF, ""));

        return tokens;
    }

}
