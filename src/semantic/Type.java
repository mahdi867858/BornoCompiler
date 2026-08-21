package semantic;

public enum Type {
    NUMBER,
    STRING,
    BOOLEAN,
    UNKNOWN;

    public String toBanglaString() {
        return switch (this) {
            case NUMBER -> "সংখ্যা";
            case STRING -> "বাক্য";
            case BOOLEAN -> "বুলিয়ান";
            case UNKNOWN -> "অজানা";
        };
    }
}
