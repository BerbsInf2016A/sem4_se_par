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

    public void addCombination(List<int[]> combination) {
        if (this.combination == null) {this.combination = new ArrayList<>();}
        this.combination = combination;
    }

    private int[] single;
    private List<int[]> combination;

    public boolean hasSingleResult() {return this.single != null;}
    public boolean hasCombinationResult() {return this.combination != null && this.combination.size() > 0 ;};

}
