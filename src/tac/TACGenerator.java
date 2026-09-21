package tac;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import ast.*;
import token.NumberHelper;

/**
 * TACGenerator — Generates beautiful, annotated Three Address Code (TAC) from Borno AST.
 */
public class TACGenerator {

    private int tempCounter = 1;
    private int labelCounter = 1;

    public TACProgram generate(ProgramNode program) {
        tempCounter = 1;
        labelCounter = 1;
        TACProgram tac = new TACProgram();
        if (program != null) {
            for (ASTNode stmt : program.getStatements()) {
                generateStatement(stmt, tac);
            }
        }
        return tac;
    }

    private String newTemp() {
        return "t" + (tempCounter++);
    }

    private String newLabel() {
        return "L" + (labelCounter++);
    }

    private void generateStatement(ASTNode node, TACProgram tac) {
        if (node instanceof AssignmentNode) {
            AssignmentNode assign = (AssignmentNode) node;
            String exprPlace = generateExpression(assign.getExpression(), tac);
            tac.add(TACInstruction.copy(assign.getVariableName(), exprPlace));
        } else if (node instanceof PrintNode) {
            PrintNode print = (PrintNode) node;
            String exprPlace = generateExpression(print.getExpression(), tac);
            tac.add(TACInstruction.print(exprPlace));
        } else if (node instanceof IfNode) {
            IfNode ifNode = (IfNode) node;
            String condPlace = generateExpression(ifNode.getCondition(), tac);

            if (ifNode.hasElse()) {
                String elseLabel = newLabel();
                String endLabel = newLabel();

                tac.add(TACInstruction.ifFalseGoto(condPlace, elseLabel));

                List<ASTNode> thenBranch = ifNode.getThenBranch();
                if (thenBranch != null) {
                    for (ASTNode s : thenBranch) {
                        generateStatement(s, tac);
                    }
                }

                tac.add(TACInstruction.gotoLabel(endLabel));
                tac.add(TACInstruction.label(elseLabel));

                List<ASTNode> elseBranch = ifNode.getElseBranch();
                if (elseBranch != null) {
                    for (ASTNode s : elseBranch) {
                        generateStatement(s, tac);
                    }
                }

                tac.add(TACInstruction.label(endLabel));
            } else {
                String endLabel = newLabel();

                tac.add(TACInstruction.ifFalseGoto(condPlace, endLabel));

                List<ASTNode> thenBranch = ifNode.getThenBranch();
                if (thenBranch != null) {
                    for (ASTNode s : thenBranch) {
                        generateStatement(s, tac);
                    }
                }

                tac.add(TACInstruction.label(endLabel));
            }
        } else if (node instanceof BlockNode) {
            BlockNode block = (BlockNode) node;
            for (ASTNode s : block.getStatements()) {
                generateStatement(s, tac);
            }
        }
    }

    private String generateExpression(ASTNode node, TACProgram tac) {
        if (node instanceof LiteralNode) {
            String val = ((LiteralNode) node).getValue();
            if (NumberHelper.isNumber(val)) {
                // Keep original Bangla digits (e.g. ২০, ১৮, ১৫)
                return val;
            } else {
                if (val.startsWith("\"") && val.endsWith("\"")) {
                    return val;
                }
                return "\"" + val + "\"";
            }
        } else if (node instanceof VariableNode) {
            return ((VariableNode) node).getName();
        } else if (node instanceof BinaryExpressionNode) {
            BinaryExpressionNode bin = (BinaryExpressionNode) node;
            String leftPlace = generateExpression(bin.getLeft(), tac);
            String rightPlace = generateExpression(bin.getRight(), tac);
            String temp = newTemp();
            tac.add(TACInstruction.binary(temp, leftPlace, bin.getOperator(), rightPlace));
            return temp;
        }
        return "0";
    }

    public static File writeToFile(TACProgram tac, String filePath) throws IOException {
        Path path = Path.of(filePath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.writeString(path, tac.toString(), StandardCharsets.UTF_8);
        return path.toFile();
    }
}
