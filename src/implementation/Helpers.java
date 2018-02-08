package implementation;

import java.util.List;

public class Helpers {
    /**
     * Converting a list of Integers to an int array.
     *
     * @param list The list of Integers, which should be converted.
     * @return The int array.
     */
    public static int[] toIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        for (int i = 0; i < ret.length; i++)
            ret[i] = list.get(i);
        return ret;
    }
}
