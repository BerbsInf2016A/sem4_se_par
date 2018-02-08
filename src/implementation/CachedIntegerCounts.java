package implementation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CachedIntegerCounts {
    private static ConcurrentMap<Integer, Map<Integer,Integer>> cache = new ConcurrentHashMap<>();

    public static Map<Integer, Integer> countCharsInInteger(int value){
        if(cache.containsKey(value)) {
            return cache.get(value);
        }
        int p = value;
        Map<Integer, Integer> numChars = new HashMap<>();

        while (p != 0) {
            int val = p % 10;
            if (!numChars.containsKey(val))
            {
                numChars.put(val, 1);
            }
            else
            {
                numChars.put(val, numChars.get(val) + 1);
            }
            p = p / 10;
        }

        cache.put(value, numChars);

        return numChars;
    }
}
