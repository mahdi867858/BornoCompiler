package ast;

import java.util.List;

/**
 * ASTPrinter — AST-কে সুন্দর tree আকারে print করে।
 */
public class ASTPrinter {

    public void print(ASTNode node) {
        printNode(node, "", true);
    }

    private void printNode(ASTNode node, String prefix, boolean isLast) {
        String branch = isLast ? "└── " : "├── ";
        String childPrefix = prefix + (isLast ? "    " : "│   ");

        if (node instanceof ProgramNode) {
            System.out.println("Program");
            List<ASTNode> stmts = ((ProgramNode) node).getStatements();
            for (int i = 0; i < stmts.size(); i++) {
                printNode(stmts.get(i), "", i == stmts.size() - 1);
            }

        } else if (node instanceof AssignmentNode) {
            AssignmentNode n = (AssignmentNode) node;
            String typeTag = n.getDeclaredType() != null ? " [" + n.getDeclaredType() + "]" : "";
            System.out.println(prefix + branch + "Assignment: " + n.getVariableName() + typeTag);
            printNode(n.getExpression(), childPrefix, true);

        } else if (node instanceof PrintNode) {
            System.out.println(prefix + branch + "Print");
            printNode(((PrintNode) node).getExpression(), childPrefix, true);

        } else if (node instanceof IfNode) {
            IfNode n = (IfNode) node;
            System.out.println(prefix + branch + "If");

            // Condition
            System.out.println(childPrefix + "├── Condition");
            printNode(n.getCondition(), childPrefix + "│   ", true);

            // Then branch
            boolean hasElse = n.hasElse();
            System.out.println(childPrefix + (hasElse ? "├── " : "└── ") + "Then");
            String thenPrefix = childPrefix + (hasElse ? "│   " : "    ");
            List<ASTNode> thenStmts = n.getThenBranch();
            for (int i = 0; i < thenStmts.size(); i++) {
                printNode(thenStmts.get(i), thenPrefix, i == thenStmts.size() - 1);
            }

            // Else branch
            if (hasElse) {
                System.out.println(childPrefix + "└── Else");
                List<ASTNode> elseStmts = n.getElseBranch();
                for (int i = 0; i < elseStmts.size(); i++) {
                    printNode(elseStmts.get(i), childPrefix + "    ", i == elseStmts.size() - 1);
                }
            }

        } else if (node instanceof WhileNode) {
            WhileNode w = (WhileNode) node;
            System.out.println(prefix + branch + "While");
            System.out.println(childPrefix + "├── Condition");
            printNode(w.getCondition(), childPrefix + "│   ", true);
            System.out.println(childPrefix + "└── Body");
            List<ASTNode> body = w.getBody();
            for (int i = 0; i < body.size(); i++) {
                printNode(body.get(i), childPrefix + "    ", i == body.size() - 1);
            }

        } else if (node instanceof BinaryExpressionNode) {
            BinaryExpressionNode b = (BinaryExpressionNode) node;
            System.out.println(prefix + branch + "BinaryExpr: " + b.getOperator());
            printNode(b.getLeft(), childPrefix, false);
            printNode(b.getRight(), childPrefix, true);

        } else if (node instanceof LiteralNode) {
            System.out.println(prefix + branch + "Literal: " + ((LiteralNode) node).getValue());

        } else if (node instanceof VariableNode) {
            System.out.println(prefix + branch + "Variable: " + ((VariableNode) node).getName());

        } else {
            System.out.println(prefix + branch + node.getClass().getSimpleName());
        }
    }
}
