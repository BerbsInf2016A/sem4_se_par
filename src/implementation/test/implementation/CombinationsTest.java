package implementation;


import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;


public class CombinationsTest {
    @Test
    public void mergeArray() {
        int[] firstArray = {1, 2, 3};
        int[] secondArray = {4, 5, 6};
        int[] expected = {1, 2, 3, 4, 5, 6};
        int[] mergedArray = Combinations.merge(firstArray, secondArray);

        assertArrayEquals("Array should be merged", expected, mergedArray);
    }

    @Test
    public void getCombination() {
        int[] elements = {1, 2, 3, 4};

        List<int[]> expectedWithALengthOfTwo = Arrays.asList(new int[]{1, 2}, new int[]{1, 3}, new int[]{1, 4}
                , new int[]{2, 3}, new int[]{2, 4}, new int[]{3, 4});
        List<int[]> combinationsLengthOfTwo = Combinations.combination(elements, 2);

        for (int i = 0; i < expectedWithALengthOfTwo.size(); i++) {
            int[] expected = expectedWithALengthOfTwo.get(i);
            int[] actual = combinationsLengthOfTwo.get(i);
            assertArrayEquals("Return value should contain the expected combination", expected, actual);
        }
    }

}