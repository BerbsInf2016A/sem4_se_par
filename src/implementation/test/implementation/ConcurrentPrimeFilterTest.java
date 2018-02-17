package implementation;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ConcurrentPrimeFilterTest {
    @Test
    public void isPrime_6_noPrime() {
        Assert.assertFalse(ConcurrentPrimeFilter.isPrime(6));
    }

    @Test
    public void isPrime_3_Prime() {
        Assert.assertTrue(ConcurrentPrimeFilter.isPrime(3));
    }

    @Test
    public void primesFrom0To1000_checkCount() {
        ConcurrentPrimeFilter finder = new ConcurrentPrimeFilter();
        List<Integer> primes = finder.filterPrimes(0, 1000);

        Assert.assertEquals("There should be 168 primes between 0 and 1000", 168, primes.size());
    }

    @Test
    public void primesFrom0To100() {
        ConcurrentPrimeFilter finder = new ConcurrentPrimeFilter();
        List<Integer> primes = finder.filterPrimes(0, 100);

        List<Integer> expected = Arrays.asList(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97);

        //Assert.assertEquals("There should be 25 primes between 0 and 100", 25, primes.size());
        Assert.assertEquals("All primes should be found", expected, primes);

    }

}
