package ast;

public class IfNode extends ASTNode {

    private final ASTNode condition;
    private final ASTNode thenBranch;
    private final ASTNode elseBranch; // null যদি else না থাকে

    public IfNode(ASTNode condition, ASTNode thenBranch, ASTNode elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public ASTNode getThenBranch() {
        return thenBranch;
    }

    public ASTNode getElseBranch() {
        return elseBranch;
    }

    public boolean hasElse() {
        return elseBranch != null;
    }
}
