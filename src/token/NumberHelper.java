package token;

/**
 * NumberHelper — Utility for handling Bangla digits (০-৯) and ASCII numbers.
 */
public class NumberHelper {

    /**
     * Checks if a character is a Bangla digit ('০'-'৯').
     */
    public static boolean isBanglaDigit(char c) {
        return c >= '\u09E6' && c <= '\u09EF';
    }

    /**
     * Checks if a character is an ASCII / English digit ('0'-'9').
     */
    public static boolean isAsciiDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Checks if a character is a valid decimal digit in Borno language (Bangla digits '০'-'৯' only).
     */
    public static boolean isDigitChar(char c) {
        return isBanglaDigit(c);
    }

    /**
     * Converts any Bangla digits in the string to their ASCII equivalents ('0'-'9').
     */
    public static String toAsciiDigits(String bangla) {
        if (bangla == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : bangla.toCharArray()) {
            if (c >= '\u09E6' && c <= '\u09EF') {
                sb.append((char) ('0' + (c - '\u09E6')));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Converts ASCII digits in the string to Bangla digits ('০'-'৯').
     */
    public static String toBanglaDigits(String ascii) {
        if (ascii == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : ascii.toCharArray()) {
            if (c >= '0' && c <= '9') {
                sb.append((char) ('\u09E6' + (c - '0')));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Convenient alias for toBanglaDigits.
     */
    public static String toBangla(String ascii) {
        return toBanglaDigits(ascii);
    }

    /**
     * Determines whether the given string represents a valid integer or floating point number.
     */
    public static boolean isNumber(String s) {
        if (s == null || s.trim().isEmpty()) return false;
        String ascii = toAsciiDigits(s.trim());
        try {
            Double.parseDouble(ascii);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses a string (which may contain Bangla digits) as a double.
     */
    public static double parseDouble(String s) {
        return Double.parseDouble(toAsciiDigits(s.trim()));
    }
}
