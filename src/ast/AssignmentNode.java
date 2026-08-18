package ast;

public class AssignmentNode extends ASTNode {

    private final String variableName;
    private final ASTNode expression;
    private final String declaredType; // "NUMBER" | "STRING" | null (re-assignment)

    public AssignmentNode(
            String variableName,
            ASTNode expression,
            String declaredType) {

        this.variableName = variableName;
        this.expression = expression;
        this.declaredType = declaredType;
    }

    public String getVariableName() {
        return variableName;
    }

    public ASTNode getExpression() {
        return expression;
    }

    public String getDeclaredType() {
        return declaredType;
    }
}
