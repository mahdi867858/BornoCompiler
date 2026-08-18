package ast;

public class AssignmentNode extends ASTNode {

    private final String variableName;
    private final ASTNode expression;

    public AssignmentNode(String variableName, ASTNode expression) {
        this.variableName = variableName;
        this.expression = expression;
    }

    public String getVariableName() {
        return variableName;
    }

    public ASTNode getExpression() {
        return expression;
    }
}
