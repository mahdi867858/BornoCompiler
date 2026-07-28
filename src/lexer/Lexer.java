package lexer;

import java.util.ArrayList;
import java.util.List;

import token.Token;
import token.TokenType;

public class Lexer {

    private final String source;
    private int position;
    private char currentChar;

    public Lexer(String source) {
        this.source = source;
        this.position = 0;

        if (source.length() > 0) {
            currentChar = source.charAt(0);
        } else {
            currentChar = '\0';
        }
    }

    private void advance() {
        position++;
        if (position >= source.length()) {
            currentChar = '\0';
        } else {
            currentChar = source.charAt(position);
        }
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(currentChar)) {
            advance();
        }
    }

    public List<Token> tokenize() {
        return new ArrayList<>();
    }
}
