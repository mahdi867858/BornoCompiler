package token;

public enum TokenType {

    // Keywords
    KEYWORD,

    // Identifier
    IDENTIFIER,

    // Literals
    NUMBER,
    STRING,

    // Operators
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULO,

    ASSIGN,

    EQUAL,
    NOT_EQUAL,
    LESS,
    GREATER,
    LESS_EQUAL,
    GREATER_EQUAL,

    // Symbols
    LEFT_PAREN,
    RIGHT_PAREN,
    LEFT_BRACE,
    RIGHT_BRACE,
    SEMICOLON,

    // End
    EOF,
    UNKNOWN
}