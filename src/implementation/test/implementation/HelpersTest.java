package implementation;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class HelpersTest {
    @Test
    public void toIntArray() {
        List<Integer> ints = Arrays.asList(1, 2, 3, 4, 55);
        int[] result = Helpers.toIntArray(ints);

        assertEquals("Count should be equal", ints.size(), result.length);
        for (int value : ints) {
            IntStream innerStream = Arrays.stream(result).filter(t -> t == value);
            assertEquals("Should be contained only once", 1, innerStream.count());
        }
    }

    @Test
    public void getNumbersContainingDigit() {
        List<Integer> ints = Arrays.asList(1, 2, 3, 4, 55, 8787, 89, 45, 6386);

        int[] numbersContainingDigitEight = Helpers.getNumbersContainingDigit(ints, 8);
        int[] numbersContainingDigitFive = Helpers.getNumbersContainingDigit(ints, 5);
        int[] numbersContainingDigitTwo = Helpers.getNumbersContainingDigit(ints, 2);

        int[] expectedEights = {8787, 89, 6386};
        int[] expectedFives = {55, 45};
        int[] expectedTwos = {2};

        assertArrayEquals("Should contain all numbers containing eight", expectedEights, numbersContainingDigitEight);
        assertArrayEquals("Should contain all numbers containing five", expectedFives, numbersContainingDigitFive);
        assertArrayEquals("Should contain all numbers containing two", numbersContainingDigitTwo, expectedTwos);

    }

}