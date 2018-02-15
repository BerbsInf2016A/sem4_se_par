package implementation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Counts the characters (1-9) in integers and caches the result to avoid multiple calculations.
 */
class CachedIntegerCounts {
    /**
     * The cache to save the values.
     */
    private static final ConcurrentMap<Integer, Map<Integer, Integer>> cache = new ConcurrentHashMap<>();

    /**
     * Counts the characters in a integer.
     *
     * @param value The int value to analyze.
     * @return A map containing the different characters and the count of their occurrence.
     */
    public static Map<Integer, Integer> countCharsInInteger(int value) {
        if (cache.containsKey(value)) {
            return cache.get(value);
        }
        int p = value;
        Map<Integer, Integer> numChars = new HashMap<>();

        while (p != 0) {
            int val = p % 10;
            if (!numChars.containsKey(val)) {
                numChars.put(val, 1);
            } else {
                numChars.put(val, numChars.get(val) + 1);
            }
            p = p / 10;
        }

        cache.put(value, numChars);

        return numChars;
    }
}
