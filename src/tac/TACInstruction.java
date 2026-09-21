package tac;

public class TACInstruction {

    public enum OpType {
        ASSIGN_BINARY("BinOp"),
        ASSIGN_UNARY("Unary"),
        ASSIGN_COPY("Copy"),
        PRINT("Print"),
        IF_FALSE_GOTO("IfFalseGoto"),
        IF_GOTO("IfGoto"),
        GOTO("Goto"),
        LABEL("Label");

        private final String label;

        OpType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final OpType opType;
    private final String result;
    private final String arg1;
    private final String op;
    private final String arg2;

    // Label constructor: L1:
    public static TACInstruction label(String labelName) {
        return new TACInstruction(OpType.LABEL, labelName, null, null, null);
    }

    // Binary operation constructor: result = arg1 op arg2
    public static TACInstruction binary(String result, String arg1, String op, String arg2) {
        return new TACInstruction(OpType.ASSIGN_BINARY, result, arg1, op, arg2);
    }

    // Unary operation constructor: result = op arg1
    public static TACInstruction unary(String result, String op, String arg1) {
        return new TACInstruction(OpType.ASSIGN_UNARY, result, arg1, op, null);
    }

    // Copy / assignment constructor: result = arg1
    public static TACInstruction copy(String result, String arg1) {
        return new TACInstruction(OpType.ASSIGN_COPY, result, arg1, null, null);
    }

    // Print constructor: দেখাও(arg1)
    public static TACInstruction print(String arg1) {
        return new TACInstruction(OpType.PRINT, null, arg1, null, null);
    }

    // Conditional jump: শর্ত arg1 মিথ্যা হলে result-এ যাও
    public static TACInstruction ifFalseGoto(String cond, String label) {
        return new TACInstruction(OpType.IF_FALSE_GOTO, label, cond, null, null);
    }

    // Conditional jump: শর্ত arg1 সত্য হলে result-এ যাও
    public static TACInstruction ifGoto(String cond, String label) {
        return new TACInstruction(OpType.IF_GOTO, label, cond, null, null);
    }

    // Unconditional jump: যাও label
    public static TACInstruction gotoLabel(String label) {
        return new TACInstruction(OpType.GOTO, label, null, null, null);
    }

    private TACInstruction(OpType opType, String result, String arg1, String op, String arg2) {
        this.opType = opType;
        this.result = result;
        this.arg1 = arg1;
        this.op = op;
        this.arg2 = arg2;
    }

    public OpType getOpType() {
        return opType;
    }

    public String getResult() {
        return result;
    }

    public String getArg1() {
        return arg1;
    }

    public String getOp() {
        return op;
    }

    public String getArg2() {
        return arg2;
    }

    /** Formats instruction according to Borno beautiful TAC specification */
    @Override
    public String toString() {
        switch (opType) {
            case LABEL:
                return "[" + opType.getLabel() + "] " + result + ":";
            case ASSIGN_BINARY:
                return "[" + opType.getLabel() + "] " + result + " = " + arg1 + " " + op + " " + arg2;
            case ASSIGN_UNARY:
                return "[" + opType.getLabel() + "] " + result + " = " + op + " " + arg1;
            case ASSIGN_COPY:
                return "[" + opType.getLabel() + "] " + result + " = " + arg1;
            case PRINT:
                if (arg1 != null && arg1.startsWith("দেখাও(")) {
                    return "[" + opType.getLabel() + "] " + arg1;
                }
                return "[" + opType.getLabel() + "] দেখাও(" + arg1 + ")";
            case IF_FALSE_GOTO:
                return "[" + opType.getLabel() + "] শর্ত " + arg1 + " মিথ্যা হলে " + result + "-এ যাও";
            case IF_GOTO:
                return "[" + opType.getLabel() + "] শর্ত " + arg1 + " সত্য হলে " + result + "-এ যাও";
            case GOTO:
                return "[" + opType.getLabel() + "] যাও " + result;
            default:
                return "";
        }
    }

    /** Returns conventional raw 3-address code line */
    public String toRawString() {
        switch (opType) {
            case LABEL:
                return result + ":";
            case ASSIGN_BINARY:
                return result + " = " + arg1 + " " + op + " " + arg2;
            case ASSIGN_UNARY:
                return result + " = " + op + " " + arg1;
            case ASSIGN_COPY:
                return result + " = " + arg1;
            case PRINT:
                return "print " + arg1;
            case IF_FALSE_GOTO:
                return "ifFalse " + arg1 + " goto " + result;
            case IF_GOTO:
                return "if " + arg1 + " goto " + result;
            case GOTO:
                return "goto " + result;
            default:
                return "";
        }
    }
}
