package ast;

public class PrintNode extends ASTNode {

    private final ASTNode expression;

    public PrintNode(ASTNode expression) {
        this.expression = expression;
    }

    public ASTNode getExpression() {
        return expression;
    }
}
