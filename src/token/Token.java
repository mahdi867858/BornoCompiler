package token;

public class Token {

    private TokenType type;
    private String value;
    private int line;
    private int col;

    // Full constructor (used by updated Lexer)
    public Token(TokenType type, String value, int line, int col) {
        this.type  = type;
        this.value = value;
        this.line  = line;
        this.col   = col;
    }

    // Backward-compatible constructor (existing code that doesn't track position)
    public Token(TokenType type, String value) {
        this(type, value, 0, 0);
    }

    public TokenType getType()  { return type;  }
    public String    getValue() { return value; }
    public int       getLine()  { return line;  }
    public int       getCol()   { return col;   }

    @Override
    public String toString() {
        return type + " : " + value;
    }
}