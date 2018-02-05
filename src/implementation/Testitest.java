package implementation;

import java.util.ArrayList;
import java.util.List;

public class Testitest {

    public static List<int[]> getCombinations(int[] elements, int length){
        List<int[]> subsets = new ArrayList<>();

        int[] s = new int[length];                  // here we'll keep indices
        // pointing to elements in input array

        if (length <= elements.length) {
            // first index sequence: 0, 1, 2, ...
            for (int i = 0; (s[i] = i) < length - 1; i++);
            subsets.add(getSubset(elements, s));
            for(;;) {
                int i;
                // find position of item that can be incremented
                for (i = length - 1; i >= 0 && s[i] == elements.length - length + i; i--);
                if (i < 0) {
                    break;
                }
                s[i]++;                    // increment this item
                for (++i; i < length; i++) {    // fill up remaining items
                    s[i] = s[i - 1] + 1;
                }
                subsets.add(getSubset(elements, s));
            }
        }
        return subsets;
    }


    private static int[] getSubset(int[] input, int[] subset) {
        int[] result = new int[subset.length];
        for (int i = 0; i < subset.length; i++)
            result[i] = input[subset[i]];
        return result;
    }
}
