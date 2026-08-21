package token;

public enum TokenType {

    // Keywords
    DHORI,          // ধরি
    SONGKHA,        // সংখ্যা
    BAKKA,          // বাক্য / লেখা
    JODI,           // যদি
    NAHOLE,         // নাহলে
    JOTOKKHON,      // যতক্ষণ
    DEKHAO,         // দেখাও
    EBONG,          // এবং
    OTHBA,          // অথবা
    NA,             // না

    // Identifiers & Literals
    IDENTIFIER,
    NUMBER,
    STRING,

    // Operators
    PLUS,           // +
    MINUS,          // -
    MULTIPLY,       // *
    DIVIDE,         // /
    MODULO,         // %

    ASSIGN,         // =

    EQUAL,          // ==
    NOT_EQUAL,      // !=
    LESS,           // <
    GREATER,        // >
    LESS_EQUAL,     // <=
    GREATER_EQUAL,  // >=

    // Symbols
    LEFT_PAREN,     // (
    RIGHT_PAREN,    // )
    LEFT_BRACE,     // {
    RIGHT_BRACE,    // }
    SEMICOLON,      // ;

    // End / Unknown
    EOF,
    UNKNOWN
}