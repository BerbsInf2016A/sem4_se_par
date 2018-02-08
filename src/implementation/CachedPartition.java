package implementation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The partition class creates the partition for a given number.
 * E.g. The 4 can be partitioned to: 4; 3+1; 2+2; 2+1+1 and 1+1+1+1.
 */
public class CachedPartition {
    private static HashMap<Integer, ArrayList<ArrayList<Integer>>> cachedValues = new HashMap<>();

    /**
     * Calculates the partition or returns a cached value.
     *
     * @param n The number, which should be partitioned.
     * @return A list of the different partitions.
     */
    public static ArrayList<ArrayList<Integer>> partition(int n) {
        if (cachedValues.containsKey(n)) {
            return cachedValues.get(n);
        }
        ArrayList<ArrayList<Integer>> result = innerPartition(n);
        cachedValues.put(n, result);
        return result;
    }

    /**
     * The actual method, which calculates the partitions.
     *
     * @param n The number, which should be partitioned.
     * @return A list of the partitions.
     */
    private static ArrayList<ArrayList<Integer>> innerPartition(int n) {
        ArrayList<String> result = new ArrayList<>();
        partition(n, n, "", result); // Call the recursive method to calculate the partitions.
        ArrayList<ArrayList<Integer>> intResult = new ArrayList<>(); // Convert the result to integers.
        for (String entry : result) {
            String[] numberStrings = entry.split(" ");
            ArrayList<Integer> numbers = new ArrayList<>();
            for (String numberString : numberStrings) {
                if (numberString.length() == 0) continue;
                numbers.add(Integer.parseInt(numberString));
            }
            intResult.add(numbers);
        }
        return intResult;
    }

    /**
     * A recursive method to calculate the partitions.
     *
     * @param n      The number to create the partition for.
     * @param max    The upper bound.
     * @param prefix The prefix, or already set parts of the partition.
     * @param result An array of strings, containing the different partitions.
     */
    private static void partition(int n, int max, String prefix, ArrayList<String> result) {
        if (n == 0) {
            result.add(prefix);
            return;
        }

        for (int i = Math.min(max, n); i >= 1; i--) {
            partition(n - i, i, prefix + " " + i, result);
        }
    }
}
