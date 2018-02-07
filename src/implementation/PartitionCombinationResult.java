package implementation;

import java.util.ArrayList;
import java.util.List;

public class PartitionCombinationResult {
    public int[] getSingle() {
        return single;
    }

    public void setSingle(int[] single) {
        this.single = single;
    }

    public List<int[]> getCombination() {
        return combination;
    }

    private int[] single;

    public void setCombination(List<int[]> combination) {
        this.combination = combination;
    }

    private List<int[]> combination;

    public boolean hasSingleResult() {return this.single != null;}
    public boolean hasCombinationResult() {return this.combination != null && this.combination.size() > 0 ;};

}
