package ast;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import token.NumberHelper;

/**
 * ASTPrinter — Compiler-style Abstract Syntax Tree printer for Borno Compiler.
 * Formats tree with TYPE / NAME / VALUE labels in clean hierarchical structure.
 */
public class ASTPrinter {

    private final PrintStream out;

    public ASTPrinter() {
        this(System.out);
    }

    public ASTPrinter(PrintStream out) {
        this.out = out;
    }

    public void print(ASTNode node) {
        out.println("PROGRAM");
        out.println("│");
        if (node instanceof ProgramNode) {
            List<ASTNode> stmts = ((ProgramNode) node).getStatements();
            for (int i = 0; i < stmts.size(); i++) {
                boolean last = (i == stmts.size() - 1);
                printNode(stmts.get(i), "", last);
                if (!last) {
                    out.println("│");
                }
            }
        } else if (node != null) {
            printNode(node, "", true);
        }
    }

    public String printToString(ASTNode node) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8);
        new ASTPrinter(ps).print(node);
        return baos.toString(StandardCharsets.UTF_8);
    }

    private void printNode(ASTNode node, String prefix, boolean isLast) {
        String connector   = isLast ? "└── " : "├── ";
        String childPrefix = prefix + (isLast ? "    " : "│   ");

        if (node instanceof AssignmentNode) {
            AssignmentNode n = (AssignmentNode) node;

            if (n.getDeclaredType() != null) {
                // নতুন declaration
                String typeLabel = "NUMBER".equalsIgnoreCase(n.getDeclaredType()) ? "সংখ্যা" :
                                   ("STRING".equalsIgnoreCase(n.getDeclaredType()) ? "বাক্য" : n.getDeclaredType());

                out.println(prefix + connector + "DECLARATION");
                out.println(childPrefix + "├── TYPE  : " + typeLabel);
                out.println(childPrefix + "├── NAME  : " + n.getVariableName());
                out.print  (childPrefix + "└── VALUE : ");
                printInline(n.getExpression());
                out.println();
            } else {
                // re-assignment
                out.println(prefix + connector + "ASSIGNMENT");
                out.println(childPrefix + "├── NAME  : " + n.getVariableName());
                out.print  (childPrefix + "└── VALUE : ");
                printInline(n.getExpression());
                out.println();
            }

        } else if (node instanceof PrintNode) {
            out.println(prefix + connector + "PRINT");
            out.print  (childPrefix + "└── EXPR  : ");
            printInline(((PrintNode) node).getExpression());
            out.println();

        } else if (node instanceof IfNode) {
            IfNode n = (IfNode) node;
            out.println(prefix + connector + "IF");

            // CONDITION
            out.print(childPrefix + "├── CONDITION : ");
            printInline(n.getCondition());
            out.println();

            // THEN
            boolean hasElse = n.hasElse();
            out.println(childPrefix + (hasElse ? "├── " : "└── ") + "THEN");
            String thenPfx = childPrefix + (hasElse ? "│   " : "    ");
            List<ASTNode> thenBranch = n.getThenBranch();
            for (int i = 0; i < thenBranch.size(); i++) {
                printNode(thenBranch.get(i), thenPfx, i == thenBranch.size() - 1);
            }

            // ELSE
            if (hasElse) {
                out.println(childPrefix + "└── ELSE");
                List<ASTNode> elseBranch = n.getElseBranch();
                for (int i = 0; i < elseBranch.size(); i++) {
                    printNode(elseBranch.get(i), childPrefix + "    ", i == elseBranch.size() - 1);
                }
            }

        } else if (node != null) {
            out.println(prefix + connector + node.getClass().getSimpleName());
        }
    }

    /** Expression-কে একলাইনে print করে — যেমন: (বয়স >= ১৮) */
    private void printInline(ASTNode node) {
        if (node instanceof LiteralNode) {
            String val = ((LiteralNode) node).getValue();
            if (NumberHelper.isNumber(val)) {
                out.print(val);
            } else {
                if (val.startsWith("\"") && val.endsWith("\"")) {
                    out.print(val);
                } else {
                    out.print("\"" + val + "\"");
                }
            }

        } else if (node instanceof VariableNode) {
            out.print(((VariableNode) node).getName());

        } else if (node instanceof BinaryExpressionNode) {
            BinaryExpressionNode b = (BinaryExpressionNode) node;
            out.print("(");
            printInline(b.getLeft());
            out.print(" " + b.getOperator() + " ");
            printInline(b.getRight());
            out.print(")");

        } else if (node != null) {
            out.print(node.getClass().getSimpleName());
        }
    }
}
