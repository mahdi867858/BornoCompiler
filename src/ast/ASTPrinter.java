package ast;

import java.util.List;

/**
 * ASTPrinter — Compiler-style syntax tree।
 * Format: indented tree with TYPE/NAME/VALUE labels
 */
public class ASTPrinter {

    public void print(ASTNode node) {
        System.out.println("PROGRAM");
        if (node instanceof ProgramNode) {
            List<ASTNode> stmts = ((ProgramNode) node).getStatements();
            for (int i = 0; i < stmts.size(); i++) {
                boolean last = (i == stmts.size() - 1);
                printNode(stmts.get(i), "│", last);
            }
        }
    }

    private void printNode(ASTNode node, String prefix, boolean isLast) {
        String connector   = isLast ? "└── " : "├── ";
        String childPrefix = prefix + (isLast ? "    " : "│   ");

        if (node instanceof AssignmentNode) {
            AssignmentNode n = (AssignmentNode) node;

            if (n.getDeclaredType() != null) {
                // নতুন declaration
                System.out.println(prefix + connector + "DECLARATION");
                System.out.println(childPrefix + "├── TYPE  : " + n.getDeclaredType());
                System.out.println(childPrefix + "├── NAME  : " + n.getVariableName());
                System.out.print  (childPrefix + "└── VALUE : ");
                printInline(n.getExpression());
                System.out.println();
            } else {
                // re-assignment
                System.out.println(prefix + connector + "ASSIGNMENT");
                System.out.println(childPrefix + "├── NAME  : " + n.getVariableName());
                System.out.print  (childPrefix + "└── VALUE : ");
                printInline(n.getExpression());
                System.out.println();
            }

        } else if (node instanceof PrintNode) {
            System.out.println(prefix + connector + "PRINT");
            System.out.print  (childPrefix + "└── EXPR  : ");
            printInline(((PrintNode) node).getExpression());
            System.out.println();

        } else if (node instanceof IfNode) {
            IfNode n = (IfNode) node;
            System.out.println(prefix + connector + "IF");

            // CONDITION
            System.out.print(childPrefix + "├── CONDITION : ");
            printInline(n.getCondition());
            System.out.println();

            // THEN
            boolean hasElse = n.hasElse();
            System.out.println(childPrefix + (hasElse ? "├── " : "└── ") + "THEN");
            String thenPfx = childPrefix + (hasElse ? "│   " : "    ");
            List<ASTNode> thenBranch = n.getThenBranch();
            for (int i = 0; i < thenBranch.size(); i++) {
                printNode(thenBranch.get(i), thenPfx, i == thenBranch.size() - 1);
            }

            // ELSE
            if (hasElse) {
                System.out.println(childPrefix + "└── ELSE");
                List<ASTNode> elseBranch = n.getElseBranch();
                for (int i = 0; i < elseBranch.size(); i++) {
                    printNode(elseBranch.get(i), childPrefix + "    ", i == elseBranch.size() - 1);
                }
            }

        } else {
            System.out.println(prefix + connector + node.getClass().getSimpleName());
        }
    }

    /** Expression-কে একলাইনে print করে — যেমন: (বয়স >= 18) */
    private void printInline(ASTNode node) {
        if (node instanceof LiteralNode) {
            System.out.print(((LiteralNode) node).getValue());

        } else if (node instanceof VariableNode) {
            System.out.print(((VariableNode) node).getName());

        } else if (node instanceof BinaryExpressionNode) {
            BinaryExpressionNode b = (BinaryExpressionNode) node;
            System.out.print("(");
            printInline(b.getLeft());
            System.out.print(" " + b.getOperator() + " ");
            printInline(b.getRight());
            System.out.print(")");

        } else {
            System.out.print(node.getClass().getSimpleName());
        }
    }
}
