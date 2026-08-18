package ast;

/**
 * ASTPrinter — AST-কে সুন্দর tree আকারে print করে।
 * এটা একটা visitor-style utility class।
 */
public class ASTPrinter {

    public void print(ASTNode node) {
        printNode(node, "", true);
    }

    private void printNode(ASTNode node, String prefix, boolean isLast) {
        String connector = isLast ? "└── " : "├── ";

        if (node instanceof ProgramNode) {
            System.out.println("Program");
            var stmts = ((ProgramNode) node).getStatements();
            for (int i = 0; i < stmts.size(); i++) {
                printNode(stmts.get(i), "", i == stmts.size() - 1);
            }

        } else if (node instanceof AssignmentNode) {
            AssignmentNode n = (AssignmentNode) node;
            System.out.println(prefix + connector + "Assignment: " + n.getVariableName());
            printNode(n.getExpression(), prefix + (isLast ? "    " : "│   "), true);

        } else if (node instanceof PrintNode) {
            System.out.println(prefix + connector + "Print");
            printNode(((PrintNode) node).getExpression(), prefix + (isLast ? "    " : "│   "), true);

        } else if (node instanceof IfNode) {
            IfNode n = (IfNode) node;
            System.out.println(prefix + connector + "If");
            String childPrefix = prefix + (isLast ? "    " : "│   ");
            System.out.println(childPrefix + "├── Condition");
            printNode(n.getCondition(), childPrefix + "│   ", true);
            System.out.println(childPrefix + (n.hasElse() ? "├── " : "└── ") + "Then");
            printNode(n.getThenBranch(), childPrefix + (n.hasElse() ? "│   " : "    "), true);
            if (n.hasElse()) {
                System.out.println(childPrefix + "└── Else");
                printNode(n.getElseBranch(), childPrefix + "    ", true);
            }

        } else if (node instanceof BlockNode) {
            BlockNode b = (BlockNode) node;
            System.out.println(prefix + connector + "Block");
            var stmts = b.getStatements();
            for (int i = 0; i < stmts.size(); i++) {
                printNode(stmts.get(i), prefix + (isLast ? "    " : "│   "), i == stmts.size() - 1);
            }

        } else if (node instanceof BinaryExpressionNode) {
            BinaryExpressionNode b = (BinaryExpressionNode) node;
            System.out.println(prefix + connector + "BinaryExpr: " + b.getOperator());
            String childPrefix = prefix + (isLast ? "    " : "│   ");
            printNode(b.getLeft(), childPrefix, false);
            printNode(b.getRight(), childPrefix, true);

        } else if (node instanceof LiteralNode) {
            System.out.println(prefix + connector + "Literal: " + ((LiteralNode) node).getValue());

        } else if (node instanceof VariableNode) {
            System.out.println(prefix + connector + "Variable: " + ((VariableNode) node).getName());

        } else {
            System.out.println(prefix + connector + "Unknown: " + node.getClass().getSimpleName());
        }
    }
}
