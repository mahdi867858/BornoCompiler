package semantic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ast.*;
import token.NumberHelper;//Numberhelper


/**
 * SemanticAnalyzer — Borno Compiler
 *
 * Checks:
 *   1. Duplicate variable declaration in same scope
 *   2. Undeclared variable usage (in statements & expressions)
 *   3. Type mismatch (declared type vs expression type)
 *   4. IF condition type (must be BOOLEAN)
 *   5. Arithmetic operations on numeric types
 *   6. Division by zero at compile time
 *   7. Cross-type comparisons
 */
public class SemanticAnalyzer {

    // Global (top-level) scope
    private final SymbolTable globalScope = new SymbolTable();

    // Map of all declared symbols across all scopes (for symbol table display)
    private final Map<String, Type> allSymbols = new LinkedHashMap<>();

    // Log of successful semantic checks
    private final List<String> checkLogs = new ArrayList<>();

    // Collected semantic errors
    private final List<String> errors = new ArrayList<>();

    public SymbolTable getGlobalScope() {
        return globalScope;
    }

    public Map<String, Type> getAllSymbols() {
        return allSymbols;
    }

    public List<String> getCheckLogs() {
        return checkLogs;
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    // ─── Entry Point ──────────────────────────────────────────────────────────

    public void analyze(ProgramNode program) {
        checkLogs.clear();
        errors.clear();
        allSymbols.clear();
        analyzeStatements(program.getStatements(), globalScope);
    }

    public void analyzeQuiet(ProgramNode program, SymbolTable scope) {
        analyzeStatements(program.getStatements(), scope);
    }

    // ─── Statement List ───────────────────────────────────────────────────────

    private void analyzeStatements(List<ASTNode> statements, SymbolTable scope) {
        if (statements == null) return;
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
        } else if (node instanceof WhileNode) {
            analyzeWhile((WhileNode) node, scope);
        } else if (node instanceof ForNode) {
            analyzeFor((ForNode) node, scope);
        }
    }

    // ─── Assignment ───────────────────────────────────────────────────────────

    private void analyzeAssignment(AssignmentNode node, SymbolTable scope) {
        String varName     = node.getVariableName();
        String declaredStr = node.getDeclaredType(); // "NUMBER" | "STRING" | null
        Type   exprType    = analyzeExpression(node.getExpression(), scope);
        Object exprVal     = evaluateConstant(node.getExpression(), scope);

        if (declaredStr != null) {
            // নতুন declaration: ধরি সংখ্যা ক = ...
            Type declaredType = "NUMBER".equalsIgnoreCase(declaredStr) ? Type.NUMBER : Type.STRING;

            // Duplicate declaration check in current scope
            if (scope.existsInCurrentScope(varName)) {
                String err = "[SEMANTIC ERROR]\nDuplicate variable declaration: '" + varName + "'";
                errors.add(err);
                throw new RuntimeException(err);
            }

            // Expression type check
            if (exprType != Type.UNKNOWN && exprType != declaredType) {
                String err = "[SEMANTIC ERROR]\nType mismatch:\n'" + varName + "' এর ধরন " +
                             declaredType.toBanglaString() + ",\nকিন্তু expression এর ধরন " +
                             exprType.toBanglaString() + "।";
                errors.add(err);
                throw new RuntimeException(err);
            }

            scope.declare(varName, declaredType, exprVal);
            allSymbols.put(varName, declaredType);
            checkLogs.add("[✓] " + varName + " → " + declaredType.toBanglaString());

        } else {
            // Re-assignment (keyword ছাড়া): ক = ...
            if (!scope.exists(varName)) {
                String err = "[SEMANTIC ERROR]\nUndeclared variable: '" + varName + "'";
                errors.add(err);
                throw new RuntimeException(err);
            }

            Type existingType = scope.getType(varName);
            if (exprType != Type.UNKNOWN && exprType != existingType) {
                String err = "[SEMANTIC ERROR]\nType mismatch:\n'" + varName + "' এর ধরন " +
                             existingType.toBanglaString() + ",\nকিন্তু expression এর ধরন " +
                             exprType.toBanglaString() + "।";
                errors.add(err);
                throw new RuntimeException(err);
            }
            scope.setValue(varName, exprVal);
            checkLogs.add("[✓] " + varName + " (পুনঃনির্ধারণ) → " + existingType.toBanglaString());
        }
    }

    // ─── If Statement ─────────────────────────────────────────────────────────

    private void analyzeIf(IfNode node, SymbolTable scope) {
        Type condType = analyzeExpression(node.getCondition(), scope);

        if (condType != Type.BOOLEAN) {
            String err = "[SEMANTIC ERROR]\nIF condition-এর type BOOLEAN হতে হবে,\n" +
                         "কিন্তু পাওয়া গেছে: " + condType.toBanglaString();
            errors.add(err);
            throw new RuntimeException(err);
        }

        checkLogs.add("[✓] IF condition → BOOLEAN");

        // then block — নিজস্ব child scope
        if (!node.getThenBranch().isEmpty()) {
            SymbolTable thenScope = new SymbolTable(scope);
            analyzeStatements(node.getThenBranch(), thenScope);
            checkLogs.add("[✓] THEN scope valid");
        }

        // else block — নিজস্ব child scope
        if (node.hasElse() && !node.getElseBranch().isEmpty()) {
            SymbolTable elseScope = new SymbolTable(scope);
            analyzeStatements(node.getElseBranch(), elseScope);
            checkLogs.add("[✓] ELSE scope valid");
        }
    }

    // ─── Print Statement ──────────────────────────────────────────────────────

    private void analyzePrint(PrintNode node, SymbolTable scope) {
        analyzeExpression(node.getExpression(), scope);
        checkLogs.add("[✓] PRINT statement valid");
    }

    // ─── While Statement ──────────────────────────────────────────────────────

    private void analyzeWhile(WhileNode node, SymbolTable scope) {
        Type condType = analyzeExpression(node.getCondition(), scope);

        if (condType != Type.BOOLEAN && condType != Type.UNKNOWN) {
            String err = "[SEMANTIC ERROR]\nWHILE condition-এর type BOOLEAN হতে হবে,\n" +
                         "কিন্তু পাওয়া গেছে: " + condType.toBanglaString();
            errors.add(err);
            throw new RuntimeException(err);
        }

        checkLogs.add("[✓] WHILE condition → BOOLEAN");

        if (node.getBody() != null && !node.getBody().isEmpty()) {
            SymbolTable loopScope = new SymbolTable(scope);
            analyzeStatements(node.getBody(), loopScope);
            checkLogs.add("[✓] WHILE body scope valid");
        }
    }

    // ─── For Statement ────────────────────────────────────────────────────────

    private void analyzeFor(ForNode node, SymbolTable scope) {
        SymbolTable loopScope = new SymbolTable(scope);

        if (node.getInit() != null) {
            analyzeStatement(node.getInit(), loopScope);
            checkLogs.add("[✓] FOR init valid");
        }

        if (node.getCondition() != null) {
            Type condType = analyzeExpression(node.getCondition(), loopScope);
            if (condType != Type.BOOLEAN && condType != Type.UNKNOWN) {
                String err = "[SEMANTIC ERROR]\nFOR condition-এর type BOOLEAN হতে হবে,\n" +
                             "কিন্তু পাওয়া গেছে: " + condType.toBanglaString();
                errors.add(err);
                throw new RuntimeException(err);
            }
            checkLogs.add("[✓] FOR condition → BOOLEAN");
        }

        if (node.getBody() != null && !node.getBody().isEmpty()) {
            analyzeStatements(node.getBody(), loopScope);
            checkLogs.add("[✓] FOR body scope valid");
        }

        if (node.getUpdate() != null) {
            analyzeStatement(node.getUpdate(), loopScope);
            checkLogs.add("[✓] FOR update valid");
        }
    }

    // ─── Constant Value Evaluation ────────────────────────────────────────────

    public Object evaluateConstant(ASTNode node, SymbolTable scope) {
        if (node == null) return null;

        if (node instanceof LiteralNode) {
            String val = ((LiteralNode) node).getValue();
            if (NumberHelper.isNumber(val)) {
                return NumberHelper.parseDouble(val);
            }
            return val;
        }

        if (node instanceof VariableNode) {
            String name = ((VariableNode) node).getName();
            return scope.getValue(name);
        }

        if (node instanceof UnaryExpressionNode) {
            UnaryExpressionNode un = (UnaryExpressionNode) node;
            Object val = evaluateConstant(un.getExpression(), scope);
            String op = un.getOperator();
            if (op.equals("-") && val instanceof Double) {
                return -((Double) val);
            } else if (op.equals("+") && val instanceof Double) {
                return val;
            } else if ((op.equals("!") || op.equals("না")) && val instanceof Boolean) {
                return !((Boolean) val);
            }
        }

        if (node instanceof BinaryExpressionNode) {
            BinaryExpressionNode bin = (BinaryExpressionNode) node;
            Object leftVal = evaluateConstant(bin.getLeft(), scope);
            Object rightVal = evaluateConstant(bin.getRight(), scope);
            String op = bin.getOperator();

            if (op.equals("+")) {
                if (leftVal instanceof String || rightVal instanceof String) {
                    if (leftVal != null && rightVal != null) {
                        return leftVal.toString() + rightVal.toString();
                    }
                } else if (leftVal instanceof Double && rightVal instanceof Double) {
                    return (Double) leftVal + (Double) rightVal;
                }
            } else if (op.equals("-")) {
                if (leftVal instanceof Double && rightVal instanceof Double) {
                    return (Double) leftVal - (Double) rightVal;
                }
            } else if (op.equals("*")) {
                if (leftVal instanceof Double && rightVal instanceof Double) {
                    return (Double) leftVal * (Double) rightVal;
                }
            } else if (op.equals("/")) {
                if (leftVal instanceof Double && rightVal instanceof Double) {
                    if ((Double) rightVal != 0.0) {
                        return (Double) leftVal / (Double) rightVal;
                    }
                }
            } else if (op.equals("%")) {
                if (leftVal instanceof Double && rightVal instanceof Double) {
                    if ((Double) rightVal != 0.0) {
                        return (Double) leftVal % (Double) rightVal;
                    }
                }
            }
        }
        return null;
    }

    // ─── Expression Type Inference ────────────────────────────────────────────

    private Type analyzeExpression(ASTNode node, SymbolTable scope) {
        if (node == null) return Type.UNKNOWN;

        // Literal: সংখ্যা বা বাক্য
        if (node instanceof LiteralNode) {
            String value = ((LiteralNode) node).getValue();
            if (NumberHelper.isNumber(value)) {
                return Type.NUMBER;
            }
            return Type.STRING;
        }

        // Variable: current scope থেকে type বের করো
        if (node instanceof VariableNode) {
            String name = ((VariableNode) node).getName();
            if (!scope.exists(name)) {
                String err = "[SEMANTIC ERROR]\nUndeclared variable: '" + name + "'";
                errors.add(err);
                throw new RuntimeException(err);
            }
            return scope.getType(name);
        }

        // Unary Expression
        if (node instanceof UnaryExpressionNode) {
            UnaryExpressionNode un = (UnaryExpressionNode) node;
            Type operandType = analyzeExpression(un.getExpression(), scope);
            String op = un.getOperator();

            if (op.equals("-") || op.equals("+")) {
                if (operandType != Type.NUMBER && operandType != Type.UNKNOWN) {
                    String err = "[SEMANTIC ERROR]\nUnary '" + op +
                                 "' শুধুমাত্র সংখ্যা (NUMBER) এর জন্য প্রযোজ্য। পেলাম: " +
                                 operandType.toBanglaString();
                    errors.add(err);
                    throw new RuntimeException(err);
                }
                return Type.NUMBER;
            }

            if (op.equals("!") || op.equals("না")) {
                if (operandType != Type.BOOLEAN && operandType != Type.UNKNOWN) {
                    String err = "[SEMANTIC ERROR]\nUnary '" + op +
                                 "' শুধুমাত্র বুলিয়ান (BOOLEAN) এর জন্য প্রযোজ্য। পেলাম: " +
                                 operandType.toBanglaString();
                    errors.add(err);
                    throw new RuntimeException(err);
                }
                return Type.BOOLEAN;
            }
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

                // Compile-time division by zero check (both literals & known variables/expressions)
                if (op.equals("/") || op.equals("%")) {
                    Object rightVal = evaluateConstant(bin.getRight(), scope);
                    if (rightVal instanceof Number && ((Number) rightVal).doubleValue() == 0.0) {
                        String err = "[SEMANTIC ERROR]\nDivision by zero is not allowed.";
                        errors.add(err);
                        throw new RuntimeException(err);
                    }
                }

                // String concatenation with '+'
                if (op.equals("+") && (left == Type.STRING || right == Type.STRING)) {
                    return Type.STRING;
                }

                if (left != Type.NUMBER || right != Type.NUMBER) {
                    String err = "[SEMANTIC ERROR]\nArithmetic '" + op +
                                 "' শুধুমাত্র সংখ্যা (NUMBER) এর জন্য প্রযোজ্য। পেলাম: " +
                                 left.toBanglaString() + ", " + right.toBanglaString();
                    errors.add(err);
                    throw new RuntimeException(err);
                }

                return Type.NUMBER;
            }

            // Comparison: ==, !=, <, >, <=, >=
            if (op.equals("==") || op.equals("!=") ||
                op.equals("<")  || op.equals(">") ||
                op.equals("<=") || op.equals(">=")) {

                if (left != right && left != Type.UNKNOWN && right != Type.UNKNOWN) {
                    String err = "[SEMANTIC ERROR]\nType mismatch: আলাদা ধরনের মধ্যে তুলনা সম্ভব নয় (" +
                                 left.toBanglaString() + " এবং " + right.toBanglaString() + ")।";
                    errors.add(err);
                    throw new RuntimeException(err);
                }
                return Type.BOOLEAN;
            }
        }

        return Type.UNKNOWN;
    }
}
