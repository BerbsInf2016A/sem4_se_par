package implementation;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class PartitionCombinationGeneratorTest {
    @Test
    public void getCombinations_unfiltered() {
        int[] primes = {2, 89, 233, 281, 283, 787, 853, 857, 859, 863, 47, 499, 449, 569, 659, 67, 769, 967};

        CachedPrimeCategories primeCategories = new CachedPrimeCategories(primes);
        PartitionCombinationGenerator.setCategories(primeCategories);

        PartitionCombinationResult combinations = PartitionCombinationGenerator.getCombinations(2,
                3, new int[0]);


        List<int[]> expectedCombinations = Arrays.asList(new int[]{283, 853}, new int[]{283, 863}, new int[]{853, 863});

        assertTrue("Should  contain single result", combinations.hasSingleResult());
        assertEquals("Should only contain one single result", 1, combinations.getSingle().length);
        assertEquals("Single result should be correct", 233, combinations.getSingle()[0]);
        assertTrue("Should contain combinations results", combinations.hasCombinationResult());
        assertEquals("Should contain three combination results", 3, combinations.getCombinations().size());
        for (int i = 0; i < expectedCombinations.size(); i++) {
            int[] expected = expectedCombinations.get(i);
            int[] actual = combinations.getCombinations().get(i);
            assertArrayEquals("Combination results should be correct", expected, actual);
        }
    }

    @Test
    public void getCombinations_filtered() {
        int[] primes = {2, 89, 233, 281, 283, 787, 853, 857, 859, 863, 47, 499, 449, 569, 659, 67, 769, 967};

        CachedPrimeCategories primeCategories = new CachedPrimeCategories(primes);
        PartitionCombinationGenerator.setCategories(primeCategories);

        // The following would be a invalid set, but this is only for tests.
        int[] existingSet = {283, 2, 89, 233};

        PartitionCombinationResult combinations = PartitionCombinationGenerator.getCombinations(2,
                3, existingSet);


        List<int[]> expectedCombinations = Arrays.asList(new int[]{853, 863});

        assertFalse("Should not contain a single result", combinations.hasSingleResult());
        assertTrue("Should contain combinations results", combinations.hasCombinationResult());
        assertEquals("Should contain only one combination results", 1, combinations.getCombinations().size());
        for (int i = 0; i < expectedCombinations.size(); i++) {
            int[] expected = expectedCombinations.get(i);
            int[] actual = combinations.getCombinations().get(i);
            assertArrayEquals("Combination results should be correct", expected, actual);
        }
    }

}