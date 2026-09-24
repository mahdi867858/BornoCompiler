package ast;

import java.util.List;

public class ForNode extends ASTNode {

    private final ASTNode init;
    private final ASTNode condition;
    private final ASTNode update;
    private final List<ASTNode> body;

    public ForNode(ASTNode init, ASTNode condition, ASTNode update, List<ASTNode> body) {
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    public ASTNode getInit() {
        return init;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public ASTNode getUpdate() {
        return update;
    }

    public List<ASTNode> getBody() {
        return body;
    }
}
