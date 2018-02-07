package implementation;

import java.util.ArrayList;
import java.util.List;

public class Combinations {

    final static
    public int[] merge(final int[] ...arrays ) {
        int size = 0;
        for ( int[] a: arrays )
            size += a.length;

        int[] res = new int[size];

        int destPos = 0;
        for ( int i = 0; i < arrays.length; i++ ) {
            if ( i > 0 ) destPos += arrays[i-1].length;
            int length = arrays[i].length;
            System.arraycopy(arrays[i], 0, res, destPos, length);
        }

        return res;
    }

    private static int[] getSubset(int[] input, int[] subset) {
        int[] result = new int[subset.length];
        for (int i = 0; i < subset.length; i++)
            result[i] = input[subset[i]];
        return result;
    }

    public static List<int[]> combination(int[] elements, int length) {

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
    /*

    public static <T> List<List<T>> combination(List<T> values, int size) {

        if (0 == size) {
            return Collections.singletonList(Collections.<T> emptyList());
        }

        if (values.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<T>> combination = new LinkedList<List<T>>();

        T actual = values.iterator().next();

        List<T> subSet = new LinkedList<T>(values);
        subSet.remove(actual);

        List<List<T>> subSetCombination = combination(subSet, size - 1);

        for (List<T> set : subSetCombination) {
            List<T> newSet = new LinkedList<T>(set);
            newSet.add(0, actual);
            combination.add(newSet);
        }

        combination.addAll(combination(subSet, size));

        return combination;
    }
    */
}
