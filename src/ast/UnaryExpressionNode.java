package ast;

public class UnaryExpressionNode extends ASTNode {

    private final String operator;
    private final ASTNode expression;

    public UnaryExpressionNode(String operator, ASTNode expression) {
        this.operator = operator;
        this.expression = expression;
    }

    public String getOperator() {
        return operator;
    }

    public ASTNode getExpression() {
        return expression;
    }
}
