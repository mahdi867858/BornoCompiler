package tac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import token.NumberHelper;

public class TACProgram {

    private final List<TACInstruction> instructions = new ArrayList<>();

    public void add(TACInstruction instruction) {
        if (instruction != null) {
            instructions.add(instruction);
        }
    }

    public List<TACInstruction> getInstructions() {
        return Collections.unmodifiableList(instructions);
    }

    public boolean isEmpty() {
        return instructions.isEmpty();
    }

    public int size() {
        return instructions.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("  নং   ধরন            নির্দেশ\n");
        sb.append("  --------------------------------------------------\n");
        for (int i = 0; i < instructions.size(); i++) {
            TACInstruction inst = instructions.get(i);
            String numBangla = NumberHelper.toBangla(String.valueOf(i + 1)) + ".";
            sb.append("  ").append(numBangla).append(" ").append(inst.toString()).append("\n");
        }
        return sb.toString();
    }

    public String toRawString() {
        StringBuilder sb = new StringBuilder();
        for (TACInstruction inst : instructions) {
            if (inst.getOpType() == TACInstruction.OpType.LABEL) {
                sb.append(inst.toRawString()).append("\n");
            } else {
                sb.append("    ").append(inst.toRawString()).append("\n");
            }
        }
        return sb.toString();
    }
}
