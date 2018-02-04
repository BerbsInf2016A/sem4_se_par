package implementation;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;


public class ConcurrentPrimeFinderTest {
    @Test
    public void isValid() throws Exception {
        String value = "5,7,29,47,59,61,67,79,83,89,269,463,467,487,569,599,859,883,887";
        boolean retValue = Validator.isValid(value);
        Assert.assertTrue(retValue);
    }

    @Test
    public void addEntry() throws Exception {
        // TODO Check
        ValidatingPrimeSet set = new ValidatingPrimeSet(21);
        List<Long> primes = Arrays.asList(5L,7L,29L,47L,59L,61L,67L,79L,83L,89L,269L,463L,467L,487L,569L,599L,859L,883L,887L);
        boolean isValid = true;
        for (Long prime : primes ) {
            if (!set.addEntry(prime) ){
                isValid = false;
                break;
            }
        }
        Assert.assertTrue(isValid);
    }

    @Test
    public void isPrime_6_noPrime(){
        ConcurrentPrimeFinder finder = new ConcurrentPrimeFinder();

        Assert.assertFalse(finder.isPrime(6));

    }

    @Test
    public void isPrime_3_Prime(){
        ConcurrentPrimeFinder finder = new ConcurrentPrimeFinder();

        Assert.assertTrue(finder.isPrime(3));

    }
    @Test
    public void primesFrom0To1000_checkCount(){
        ConcurrentPrimeFinder finder = new ConcurrentPrimeFinder();
        List<Long> primes = finder.findPrimes(0, 1000);

        Assert.assertEquals("There should be 168 primes between 0 and 1000", 168, primes.size());
    }

    @Test
    public void primesFrom0To100(){
        ConcurrentPrimeFinder finder = new ConcurrentPrimeFinder();
        List<Long> primes = finder.findPrimes(0, 100);

        List<Long> expected = Arrays.asList(2L,3L,5L,7L,11L,13L,17L,19L,23L,29L,31L,37L,41L,43L,47L,53L,59L,61L,67L,71L,73L,79L,83L,89L,97L);

        //Assert.assertEquals("There should be 25 primes between 0 and 100", 25, primes.size());
        Assert.assertEquals("All primes should be found", expected, primes);

    }

}
