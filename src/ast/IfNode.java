package ast;

import java.util.List;

public class IfNode extends ASTNode {

    private final ASTNode condition;
    private final List<ASTNode> thenBranch;
    private final List<ASTNode> elseBranch; // empty list যদি else না থাকে

    public IfNode(
            ASTNode condition,
            List<ASTNode> thenBranch,
            List<ASTNode> elseBranch) {

        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public List<ASTNode> getThenBranch() {
        return thenBranch;
    }

    public List<ASTNode> getElseBranch() {
        return elseBranch;
    }

    public boolean hasElse() {
        return elseBranch != null && !elseBranch.isEmpty();
    }
}
