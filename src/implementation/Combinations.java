package implementation;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to build combinations and some necessary helper methods.
 */
class Combinations {

    /**
     * Merges several arrays into one.
     *
     * @param arrays The arrays which should be merged.
     * @return The merged array.
     */
    static public int[] merge(final int[]... arrays) {
        int size = 0;
        for (int[] a : arrays)
            size += a.length;

        int[] result = new int[size];

        int destPos = 0;
        for (int i = 0; i < arrays.length; i++) {
            if (i > 0) destPos += arrays[i - 1].length;
            int length = arrays[i].length;
            System.arraycopy(arrays[i], 0, result, destPos, length);
        }

        return result;
    }

    /**
     * Gets a subset from an array.
     *
     * @param input  The input array, which is the source of the values, which should be copied.
     * @param subset An array containing the wanted subset indices.
     * @return The subset.
     */
    private static int[] getSubset(int[] input, int[] subset) {
        int[] result = new int[subset.length];
        for (int i = 0; i < subset.length; i++)
            result[i] = input[subset[i]];
        return result;
    }

    /**
     * Builds the combination of a given length, out of the given elements.
     *
     * @param elements The elements to combine.
     * @param length   The length of the wanted combinations.
     * @return The combinations of the elements in the given length.
     */
    public static List<int[]> combination(int[] elements, int length) {
        List<int[]> subsets = new ArrayList<>();

        int[] s = new int[length];                  // here we'll keep indices pointing to elements in input array

        if (length <= elements.length) {
            // first index sequence: 0, 1, 2, ...
            for (int i = 0; (s[i] = i) < length - 1; i++) ;
            subsets.add(getSubset(elements, s));
            while (true) {
                int i;
                // find position of item that can be incremented
                for (i = length - 1; i >= 0 && s[i] == elements.length - length + i; i--) ;
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
}
