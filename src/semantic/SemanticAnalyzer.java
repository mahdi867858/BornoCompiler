package semantic;

import ast.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SemanticAnalyzer — Borno Compiler
 *
 * কাজ:
 *   1. Variable declaration check — ব্যবহারের আগে declare হয়েছে কিনা
 *   2. Duplicate declaration check — একই নাম দুইবার declare করা হলে error
 *   3. Type tracking — সংখ্যা / লেখা আলাদা করে রাখা
 */
public class SemanticAnalyzer {

    // varName → type ("সংখ্যা" বা "লেখা")
    private final Map<String, String> symbolTable = new HashMap<>();
    private boolean hasError = false;

    // ─── Entry Point ──────────────────────────────────────────────────────────

    public boolean analyze(ProgramNode program) {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  Semantic Analysis শুরু হচ্ছে...");
        System.out.println("═══════════════════════════════════════");

        analyzeBlock(program.getStatements());

        if (!hasError) {
            System.out.println("\n✅ Semantic Analysis সফল! কোনো error নেই।");
        } else {
            System.out.println("\n❌ Semantic Analysis ব্যর্থ! উপরের error গুলো ঠিক করুন।");
        }
        return !hasError;
    }

    // ─── Block / Statement list ───────────────────────────────────────────────

    private void analyzeBlock(List<ASTNode> statements) {
        for (ASTNode stmt : statements) {
            analyzeStatement(stmt);
        }
    }

    // ─── Statement Dispatch ───────────────────────────────────────────────────

    private void analyzeStatement(ASTNode node) {
        if (node instanceof AssignmentNode) {
            analyzeAssignment((AssignmentNode) node);
        } else if (node instanceof PrintNode) {
            analyzePrint((PrintNode) node);
        } else if (node instanceof IfNode) {
            analyzeIf((IfNode) node);
        } else if (node instanceof BlockNode) {
            analyzeBlock(((BlockNode) node).getStatements());
        }
    }

    // ─── Assignment: সংখ্যা বয়স = 20; ───────────────────────────────────────

    private void analyzeAssignment(AssignmentNode node) {
        String varName = node.getVariableName();

        // Duplicate declaration check
        if (symbolTable.containsKey(varName)) {
            error("Variable '" + varName + "' আগেই declare করা হয়েছে! (type: " + symbolTable.get(varName) + ")");
            return;
        }

        // Expression valid কিনা check করো
        String exprType = inferType(node.getExpression());

        // Symbol table-এ রাখো
        symbolTable.put(varName, exprType);
        System.out.println("  [Semantic] ✔ Declared: " + varName + " → " + exprType);
    }

    // ─── Print: দেখাও(...); ──────────────────────────────────────────────────

    private void analyzePrint(PrintNode node) {
        inferType(node.getExpression()); // type check করো
        System.out.println("  [Semantic] ✔ Print statement valid");
    }

    // ─── If/Else: যদি (...) { } নাহলে { } ───────────────────────────────────

    private void analyzeIf(IfNode node) {
        inferType(node.getCondition()); // condition type check

        // then branch
        if (node.getThenBranch() instanceof BlockNode) {
            analyzeBlock(((BlockNode) node.getThenBranch()).getStatements());
        }

        // else branch (যদি থাকে)
        if (node.hasElse()) {
            if (node.getElseBranch() instanceof BlockNode) {
                analyzeBlock(((BlockNode) node.getElseBranch()).getStatements());
            }
        }

        System.out.println("  [Semantic] ✔ If statement valid");
    }

    // ─── Type Inference ───────────────────────────────────────────────────────

    /**
     * Expression-এর type বের করো।
     * Return: "সংখ্যা" | "লেখা" | "boolean"
     */
    private String inferType(ASTNode node) {
        if (node instanceof LiteralNode) {
            String val = ((LiteralNode) node).getValue();
            // সব literal number হলে সংখ্যা, নাহলে লেখা
            try {
                Double.parseDouble(val);
                return "সংখ্যা";
            } catch (NumberFormatException e) {
                return "লেখা";
            }
        }

        if (node instanceof VariableNode) {
            String varName = ((VariableNode) node).getName();
            if (!symbolTable.containsKey(varName)) {
                error("Variable '" + varName + "' declare করা হয়নি!");
                return "unknown";
            }
            return symbolTable.get(varName);
        }

        if (node instanceof BinaryExpressionNode) {
            BinaryExpressionNode bin = (BinaryExpressionNode) node;
            String leftType  = inferType(bin.getLeft());
            String rightType = inferType(bin.getRight());
            String op        = bin.getOperator();

            // Comparison operator → boolean
            switch (op) {
                case "==": case "!=":
                case "<":  case ">":
                case "<=": case ">=":
                    return "boolean";
            }

            // Arithmetic → type match করতে হবে
            if (!leftType.equals(rightType)) {
                error("Type mismatch! '" + leftType + "' এবং '" + rightType +
                      "' একসাথে '" + op + "' দিয়ে ব্যবহার করা যাবে না।");
            }

            return leftType;
        }

        return "unknown";
    }

    // ─── Error Reporting ─────────────────────────────────────────────────────

    private void error(String message) {
        System.out.println("  [Semantic Error] ❌ " + message);
        hasError = true;
    }
}
