package implementation;

import java.util.List;

public class PartitionCombinationResult {
    private int[] single;
    private List<int[]> combination;

    public int[] getSingle() {
        return single;
    }

    public void setSingle(int[] single) {
        this.single = single;
    }

    public List<int[]> getCombination() {
        return combination;
    }

    public void setCombination(List<int[]> combination) {
        this.combination = combination;
    }

    public boolean hasSingleResult() {
        return this.single != null;
    }

    public boolean hasCombinationResult() {
        return this.combination != null && this.combination.size() > 0;
    }

    ;

}
