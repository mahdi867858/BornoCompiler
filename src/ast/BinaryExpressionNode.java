package ast;

public class BinaryExpressionNode extends ASTNode {

    private final ASTNode left;
    private final String operator;
    private final ASTNode right;

    public BinaryExpressionNode(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public ASTNode getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public ASTNode getRight() {
        return right;
    }
}
