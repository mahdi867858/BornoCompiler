package semantic;

import ast.*;
import java.util.List;

/**
 * SemanticAnalyzer — Borno Compiler
 *
 * চেক করে:
 *   1. Duplicate declaration  — একই scope-এ একই নাম দুবার declare
 *   2. Undeclared variable    — declare ছাড়া use
 *   3. Type mismatch          — declared type vs expression type
 *   4. IF condition type      — অবশ্যই BOOLEAN হতে হবে
 *   5. Arithmetic type        — শুধু NUMBER দিয়ে
 *   6. Cross-type comparison  — আলাদা type compare করা যাবে না
 */
public class SemanticAnalyzer {

    // Global (top-level) scope
    private final SymbolTable globalScope = new SymbolTable();

    // ─── Entry Point ──────────────────────────────────────────────────────────

    public void analyze(ProgramNode program) {
        System.out.println("═══════════════════════════════════════");
        System.out.println("  Semantic Analysis শুরু হচ্ছে...");
        System.out.println("═══════════════════════════════════════");

        analyzeStatements(program.getStatements(), globalScope);

        System.out.println("Semantic analysis completed successfully!");
    }

    // ─── Statement List ───────────────────────────────────────────────────────

    private void analyzeStatements(List<ASTNode> statements, SymbolTable scope) {
        for (ASTNode stmt : statements) {
            analyzeStatement(stmt, scope);
        }
    }

    private void analyzeStatement(ASTNode node, SymbolTable scope) {
        if (node instanceof AssignmentNode) {
            analyzeAssignment((AssignmentNode) node, scope);
        } else if (node instanceof IfNode) {
            analyzeIf((IfNode) node, scope);
        } else if (node instanceof PrintNode) {
            analyzePrint((PrintNode) node, scope);
        }
    }

    // ─── Assignment ───────────────────────────────────────────────────────────

    private void analyzeAssignment(AssignmentNode node, SymbolTable scope) {
        String varName     = node.getVariableName();
        String declaredStr = node.getDeclaredType(); // "NUMBER" | "STRING"
        Type   exprType    = analyzeExpression(node.getExpression(), scope);

        if (declaredStr != null) {
            // নতুন declaration: সংখ্যা a = ...
            Type declaredType = Type.valueOf(declaredStr);

            // Expression type মিলছে কিনা
            if (exprType != Type.UNKNOWN && exprType != declaredType) {
                throw new RuntimeException(
                    "[Type Error] '" + varName + "' declared as " + declaredType +
                    " কিন্তু expression দিচ্ছে " + exprType
                );
            }

            // current scope-এ রাখো (duplicate হলে SymbolTable নিজেই exception দেবে)
            scope.declare(varName, declaredType);
            System.out.println("  [✔] Declared: " + varName + " → " + declaredType);

        } else {
            // Re-assignment (keyword ছাড়া): চেক করো আগে declare হয়েছে কিনা
            if (!scope.exists(varName)) {
                throw new RuntimeException(
                    "[Semantic Error] Undeclared variable: '" + varName + "'"
                );
            }

            Type existingType = scope.getType(varName);
            if (exprType != Type.UNKNOWN && exprType != existingType) {
                throw new RuntimeException(
                    "[Type Error] '" + varName + "' is " + existingType +
                    " — " + exprType + " assign করা যাবে না"
                );
            }
            System.out.println("  [✔] Re-assigned: " + varName + " → " + existingType);
        }
    }

    // ─── If Statement ─────────────────────────────────────────────────────────

    private void analyzeIf(IfNode node, SymbolTable scope) {
        Type condType = analyzeExpression(node.getCondition(), scope);

        if (condType != Type.BOOLEAN) {
            throw new RuntimeException(
                "[Type Error] IF condition-এর type BOOLEAN হতে হবে, পেলাম: " + condType
            );
        }

        // Each block gets its own scope (child inherits parent's symbols)
        analyzeStatements(node.getThenBranch(), new SymbolTable(scope));
        analyzeStatements(node.getElseBranch(), new SymbolTable(scope));
        System.out.println("  [✔] If statement valid");
    }

    // ─── Print Statement ──────────────────────────────────────────────────────

    private void analyzePrint(PrintNode node, SymbolTable scope) {
        analyzeExpression(node.getExpression(), scope);
        System.out.println("  [✔] Print statement valid");
    }

    // ─── Expression Type Inference ────────────────────────────────────────────

    private Type analyzeExpression(ASTNode node, SymbolTable scope) {

        // Literal: সংখ্যা বা string?
        if (node instanceof LiteralNode) {
            String value = ((LiteralNode) node).getValue();
            try {
                Double.parseDouble(value);
                return Type.NUMBER;
            } catch (NumberFormatException e) {
                return Type.STRING;
            }
        }

        // Variable: current scope থেকে type নাও
        if (node instanceof VariableNode) {
            String name = ((VariableNode) node).getName();
            if (!scope.exists(name)) {
                throw new RuntimeException(
                    "[Semantic Error] Undeclared variable: '" + name + "'"
                );
            }
            return scope.getType(name);
        }

        // Binary Expression
        if (node instanceof BinaryExpressionNode) {
            BinaryExpressionNode bin = (BinaryExpressionNode) node;
            Type left  = analyzeExpression(bin.getLeft(), scope);
            Type right = analyzeExpression(bin.getRight(), scope);
            String op  = bin.getOperator();

            // Arithmetic: +, -, *, /, %
            if (op.equals("+") || op.equals("-") ||
                op.equals("*") || op.equals("/") || op.equals("%")) {

                if (left != Type.NUMBER || right != Type.NUMBER) {
                    throw new RuntimeException(
                        "[Type Error] Arithmetic '" + op +
                        "' শুধু NUMBER দিয়ে করা যাবে। পেলাম: " + left + ", " + right
                    );
                }
                return Type.NUMBER;
            }

            // Comparison: ==, !=, <, >, <=, >=
            if (op.equals("==") || op.equals("!=") ||
                op.equals("<")  || op.equals(">") ||
                op.equals("<=") || op.equals(">=")) {

                if (left != right) {
                    throw new RuntimeException(
                        "[Type Error] আলাদা type compare করা যাবে না: " + left + " vs " + right
                    );
                }
                return Type.BOOLEAN;
            }
        }

        return Type.UNKNOWN;
    }
}
