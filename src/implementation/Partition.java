package implementation;

import java.util.ArrayList;
import java.util.HashMap;

public class Partition {
    private static HashMap<Integer, ArrayList<ArrayList<Integer>>> cachedValues = new HashMap<>();


    public static ArrayList<ArrayList<Integer>> partition(int n) {
        if (cachedValues.containsKey(n)) {
            return cachedValues.get(n);
        }
        ArrayList<ArrayList<Integer>> result = innerPartition(n);
        cachedValues.put(n, result);
        return result;
    }

    private static ArrayList<ArrayList<Integer>> innerPartition(int n) {
        ArrayList<String> result = new ArrayList<>();
        partition(n, n, "", result);
        ArrayList<ArrayList<Integer>> intResult = new ArrayList<>();
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
