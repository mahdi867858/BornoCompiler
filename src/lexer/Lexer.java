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

    private Token readNumber() {
        StringBuilder number = new StringBuilder();
        while (currentChar != '\0' && Character.isDigit(currentChar)) {
            number.append(currentChar);
            advance();
        }
        return new Token(TokenType.NUMBER, number.toString());
    }

    private boolean isIdentifierChar(char c) {
        return Character.isLetter(c) || Character.isDigit(c) || c == '_' || (c >= '\u0980' && c <= '\u09FF');
    }

    private Token readIdentifierOrKeyword() {
        StringBuilder word = new StringBuilder();
        while (currentChar != '\0' && isIdentifierChar(currentChar)) {
            word.append(currentChar);
            advance();
        }
        String value = word.toString();
        switch (value) {
            case "সংখ্যা":
            case "লেখা":
            case "যদি":
            case "নাহলে":
            case "যতক্ষণ":
            case "দেখাও":
                return new Token(TokenType.KEYWORD, value);
            default:
                return new Token(TokenType.IDENTIFIER, value);
        }
    }

    private Token readString() {
        advance(); // skip opening quote
        StringBuilder str = new StringBuilder();
        while (currentChar != '\0' && currentChar != '"') {
            str.append(currentChar);
            advance();
        }
        advance(); // skip closing quote
        return new Token(TokenType.STRING, str.toString());
    }

    public Token nextToken() {
        skipWhitespace();

        if (currentChar == '\0') {
            return new Token(TokenType.EOF, "");
        }

        if (Character.isDigit(currentChar)) {
            return readNumber();
        }

        if (isIdentifierChar(currentChar)) {
            return readIdentifierOrKeyword();
        }

        if (currentChar == '"') {
            return readString();
        }

        switch (currentChar) {
            case '(':
                advance();
                return new Token(TokenType.LEFT_PAREN, "(");

            case ')':
                advance();
                return new Token(TokenType.RIGHT_PAREN, ")");

            case '{':
                advance();
                return new Token(TokenType.LEFT_BRACE, "{");

            case '}':
                advance();
                return new Token(TokenType.RIGHT_BRACE, "}");

            case '=':
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.EQUAL, "==");
                }
                return new Token(TokenType.ASSIGN, "=");

            case '!':
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.NOT_EQUAL, "!=");
                }
                return new Token(TokenType.UNKNOWN, "!");

            case '<':
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.LESS_EQUAL, "<=");
                }
                return new Token(TokenType.LESS, "<");

            case '>':
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.GREATER_EQUAL, ">=");
                }
                return new Token(TokenType.GREATER, ">");

            case ';':
                advance();
                return new Token(TokenType.SEMICOLON, ";");

            case '+':
                advance();
                return new Token(TokenType.PLUS, "+");

            case '-':
                advance();
                return new Token(TokenType.MINUS, "-");

            case '*':
                advance();
                return new Token(TokenType.MULTIPLY, "*");

            case '/':
                advance();
                return new Token(TokenType.DIVIDE, "/");

            case '%':
                advance();
                return new Token(TokenType.MODULO, "%");

            default:
                char unknown = currentChar;
                advance();
                return new Token(TokenType.UNKNOWN, String.valueOf(unknown));
        }
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        do {
            token = nextToken();
            tokens.add(token);
        } while (token.getType() != TokenType.EOF);
        return tokens;
    }
}
