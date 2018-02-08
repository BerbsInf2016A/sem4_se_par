package implementation;

import java.util.List;

/**
 * A class representing a partition combination result.
 */
public class PartitionCombinationResult {
    /**
     * A single result fulfills the partition size with one single prime.
     */
    private int[] single;
    /**
     * The combinations which fulfill the partition.
     */
    private List<int[]> combinations;

    /**
     * Getter for the single prime results.
     *
     * @return An integer array containing all primes which fulfill the partition as single number.
     */
    public int[] getSingle() {
        return single;
    }

    /**
     * Sets the single result.
     *
     * @param single The single results.
     */
    public void setSingle(int[] single) {
        this.single = single;
    }

    /**
     * Gets the combined results.
     *
     * @return A list of combinations, which fulfill the partition.
     */
    public List<int[]> getCombinations() {
        return combinations;
    }

    /**
     * Sets the combinations.
     *
     * @param combination The combinations
     */
    public void setCombination(List<int[]> combination) {
        this.combinations = combination;
    }

    /**
     * True, if there are results for single primes.
     *
     * @return True if there are single results, false if not.
     */
    public boolean hasSingleResult() {
        return this.single != null;
    }

    /**
     * True, if there are combined results.
     *
     * @return True if there are results, false if not.
     */
    public boolean hasCombinationResult() {
        return this.combinations != null && this.combinations.size() > 0;
    }
}
