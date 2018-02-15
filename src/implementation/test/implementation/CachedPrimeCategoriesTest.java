package implementation;

import org.junit.Test;

import static org.junit.Assert.*;

public class CachedPrimeCategoriesTest {
    @Test
    public void getBucketForCharacterAndCharacterCount() {
        int[] primes = {89, 281, 283, 787, 853, 857, 859, 863, 47, 499, 449, 569, 659, 67, 769, 967};

        CachedPrimeCategories testee = new CachedPrimeCategories(primes);
        int[] twoNines = testee.getBucketForCharacterAndCharacterCount(9, 2);
        int[] expectedTwoNines = {499};
        int[] oneNine = testee.getBucketForCharacterAndCharacterCount(9, 1);
        int[] expectedOneNie = {89, 859, 449, 569, 659, 769, 967};
        int[] oneTwo = testee.getBucketForCharacterAndCharacterCount(2, 1);
        int[] expectedOneTwo = {281, 283};

        assertArrayEquals("Should return the correct prime numbers", expectedTwoNines, twoNines);
        assertArrayEquals("Should return the correct prime numbers", expectedOneNie, oneNine);
        assertArrayEquals("Should return the correct prime numbers", expectedOneTwo, oneTwo);
    }

}