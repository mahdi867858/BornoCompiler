package lexer;

import java.util.ArrayList;
import java.util.List;

import token.NumberHelper;
import token.Token;
import token.TokenType;

public class Lexer {

    private final String source;
    private int position;
    private char currentChar;

    // ─── Line / Column tracking ───────────────────────────────────────────────
    private int line = 1;
    private int col  = 1;

    // ─── Lexical error tracking ───────────────────────────────────────────────
    private final List<String> errors = new ArrayList<>();

    public Lexer(String source) {
        this.source = source != null ? source : "";
        this.position = 0;

        if (this.source.length() > 0) {
            currentChar = this.source.charAt(0);
        } else {
            currentChar = '\0';
        }
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    private void advance() {
        if (currentChar == '\n') {
            line++;
            col = 1;
        } else {
            col++;
        }
        position++;
        if (position >= source.length()) {
            currentChar = '\0';
        } else {
            currentChar = source.charAt(position);
        }
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(currentChar) || currentChar == '#') {
            if (currentChar == '#') {
                while (currentChar != '\0' && currentChar != '\n' && currentChar != '\r') {
                    advance();
                }
            } else {
                advance();
            }
        }
    }

    private boolean isBanglaChar(char c) {
        return (c >= '\u0980' && c <= '\u09FF') || c == '\u200C' || c == '\u200D';
    }

    private boolean isBanglaLetter(char c) {
        return isBanglaChar(c) && !NumberHelper.isBanglaDigit(c);
    }

    private boolean isAsciiLetterOrUnderscore(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isIdentifierPart(char c) {
        return isBanglaChar(c) || isAsciiLetterOrUnderscore(c) || NumberHelper.isAsciiDigit(c);
    }

    private Token readNumber() {
        int startLine = line, startCol = col;
        StringBuilder number = new StringBuilder();
        boolean hasInvalidChar = false;
        while (currentChar != '\0' && (NumberHelper.isBanglaDigit(currentChar) || currentChar == '.' ||
                NumberHelper.isAsciiDigit(currentChar) || isAsciiLetterOrUnderscore(currentChar) || isBanglaLetter(currentChar))) {
            if (NumberHelper.isAsciiDigit(currentChar) || isAsciiLetterOrUnderscore(currentChar) || isBanglaLetter(currentChar)) {
                hasInvalidChar = true;
            }
            number.append(currentChar);
            advance();
        }
        String val = number.toString();
        if (hasInvalidChar) {
            errors.add("[LEXICAL ERROR]\n라인: " + startLine + "\nকলাম: " + startCol +
                       "\nসমস্যা: Invalid number format '" + val + "'. Use Bangla digits (০-৯) only.");
            return new Token(TokenType.UNKNOWN, val, startLine, startCol);
        }
        return new Token(TokenType.NUMBER, val, startLine, startCol);
    }

    private Token handleEnglishDigit() {
        int startLine = line, startCol = col;
        char firstChar = currentChar;
        StringBuilder num = new StringBuilder();
        while (currentChar != '\0' && (NumberHelper.isAsciiDigit(currentChar) || isAsciiLetterOrUnderscore(currentChar) ||
                isBanglaChar(currentChar) || currentChar == '.')) {
            num.append(currentChar);
            advance();
        }
        errors.add("[LEXICAL ERROR]\n라인: " + startLine + "\nকলাম: " + startCol +
                   "\nসমস্যা: English digit '" + firstChar + "' is not allowed. Use Bangla digits (০-৯).");
        return new Token(TokenType.UNKNOWN, num.toString(), startLine, startCol);
    }

    private Token handleEnglishIdentifierOrKeyword() {
        int startLine = line, startCol = col;
        StringBuilder word = new StringBuilder();
        while (currentChar != '\0' && (isAsciiLetterOrUnderscore(currentChar) || NumberHelper.isAsciiDigit(currentChar) || isBanglaChar(currentChar))) {
            word.append(currentChar);
            advance();
        }
        String value = word.toString();
        errors.add("[LEXICAL ERROR]\n라인: " + startLine + "\nকলাম: " + startCol +
                   "\nসমস্যা: English identifier/keyword '" + value + "' is not allowed. Use Bangla characters.");
        return new Token(TokenType.UNKNOWN, value, startLine, startCol);
    }

    private Token readIdentifierOrKeyword() {
        int startLine = line, startCol = col;
        StringBuilder word = new StringBuilder();
        boolean hasAscii = false;
        while (currentChar != '\0' && isIdentifierPart(currentChar)) {
            if (isAsciiLetterOrUnderscore(currentChar) || NumberHelper.isAsciiDigit(currentChar)) {
                hasAscii = true;
            }
            word.append(currentChar);
            advance();
        }
        String value = word.toString();

        if (hasAscii) {
            errors.add("[LEXICAL ERROR]\n라인: " + startLine + "\nকলাম: " + startCol +
                       "\nসমস্যা: Mixed identifier '" + value + "' is not allowed. Identifiers must be strictly Bangla.");
            return new Token(TokenType.UNKNOWN, value, startLine, startCol);
        }

        switch (value) {
            case "ধরি":
                return new Token(TokenType.DHORI, value, startLine, startCol);
            case "সংখ্যা":
                return new Token(TokenType.SONGKHA, value, startLine, startCol);
            case "বাক্য":
            case "লেখা":
                return new Token(TokenType.BAKKA, value, startLine, startCol);
            case "যদি":
                return new Token(TokenType.JODI, value, startLine, startCol);
            case "নাহলে":
                return new Token(TokenType.NAHOLE, value, startLine, startCol);
            case "যতক্ষণ":
                return new Token(TokenType.JOTOKKHON, value, startLine, startCol);
            case "দেখাও":
                return new Token(TokenType.DEKHAO, value, startLine, startCol);
            case "এবং":
                return new Token(TokenType.EBONG, value, startLine, startCol);
            case "অথবা":
                return new Token(TokenType.OTHBA, value, startLine, startCol);
            case "না":
                return new Token(TokenType.NA, value, startLine, startCol);
            default:
                return new Token(TokenType.IDENTIFIER, value, startLine, startCol);
        }
    }

    private Token readString() {
        int startLine = line, startCol = col;
        advance(); // skip opening quote
        StringBuilder str = new StringBuilder();
        while (currentChar != '\0' && currentChar != '"') {
            str.append(currentChar);
            advance();
        }
        if (currentChar == '"') {
            advance(); // skip closing quote
        } else {
            errors.add("[LEXICAL ERROR]\nলাইন: " + startLine + "\nকলাম: " + startCol +
                       "\nসমস্যা: স্ট্রিং সমাপ্ত করা হয়নি (Unclosed string literal)");
        }
        return new Token(TokenType.STRING, str.toString(), startLine, startCol);
    }

    public Token nextToken() {
        skipWhitespace();

        if (currentChar == '\0') {
            return new Token(TokenType.EOF, "", line, col);
        }

        if (NumberHelper.isBanglaDigit(currentChar)) {
            return readNumber();
        }

        if (NumberHelper.isAsciiDigit(currentChar)) {
            return handleEnglishDigit();
        }

        if (isAsciiLetterOrUnderscore(currentChar)) {
            return handleEnglishIdentifierOrKeyword();
        }

        if (isBanglaLetter(currentChar)) {
            return readIdentifierOrKeyword();
        }

        if (currentChar == '"') {
            return readString();
        }

        int tokLine = line, tokCol = col;

        switch (currentChar) {
            case '(':
                advance();
                return new Token(TokenType.LEFT_PAREN, "(", tokLine, tokCol);

            case ')':
                advance();
                return new Token(TokenType.RIGHT_PAREN, ")", tokLine, tokCol);

            case '{':
                advance();
                return new Token(TokenType.LEFT_BRACE, "{", tokLine, tokCol);

            case '}':
                advance();
                return new Token(TokenType.RIGHT_BRACE, "}", tokLine, tokCol);

            case '=':
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.EQUAL, "==", tokLine, tokCol);
                }
                return new Token(TokenType.ASSIGN, "=", tokLine, tokCol);

            case '!':
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.NOT_EQUAL, "!=", tokLine, tokCol);
                }
                errors.add("[LEXICAL ERROR]\nলাইন: " + tokLine + "\nকলাম: " + tokCol +
                           "\nসমস্যা: অপরিচিত ক্যারেক্টার '!' (সম্ভবত '!=' বোঝানো হয়েছে)");
                return new Token(TokenType.UNKNOWN, "!", tokLine, tokCol);

            case '<':
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.LESS_EQUAL, "<=", tokLine, tokCol);
                }
                return new Token(TokenType.LESS, "<", tokLine, tokCol);

            case '>':
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.GREATER_EQUAL, ">=", tokLine, tokCol);
                }
                return new Token(TokenType.GREATER, ">", tokLine, tokCol);

            case ';':
                advance();
                return new Token(TokenType.SEMICOLON, ";", tokLine, tokCol);

            case '+':
                advance();
                return new Token(TokenType.PLUS, "+", tokLine, tokCol);

            case '-':
                advance();
                return new Token(TokenType.MINUS, "-", tokLine, tokCol);

            case '*':
                advance();
                return new Token(TokenType.MULTIPLY, "*", tokLine, tokCol);

            case '/':
                advance();
                return new Token(TokenType.DIVIDE, "/", tokLine, tokCol);

            case '%':
                advance();
                return new Token(TokenType.MODULO, "%", tokLine, tokCol);

            default:
                char unknown = currentChar;
                advance();
                errors.add("[LEXICAL ERROR]\nলাইন: " + tokLine + "\nকলাম: " + tokCol +
                           "\nসমস্যা: অপরিচিত ক্যারেক্টার '" + unknown + "'");
                return new Token(TokenType.UNKNOWN, String.valueOf(unknown), tokLine, tokCol);
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
