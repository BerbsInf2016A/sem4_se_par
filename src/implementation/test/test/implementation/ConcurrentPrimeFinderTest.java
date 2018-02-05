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
        ValidatingPrimeSet set = new ValidatingPrimeSet();
        List<Integer> primes = Arrays.asList(5,7,29,47,59,61,67,79,83,89,269,463,467,487,569,599,859,883,887);
        boolean isValid = true;
        for (Integer prime : primes ) {
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
        List<Integer> primes = finder.findPrimes(0, 1000);

        Assert.assertEquals("There should be 168 primes between 0 and 1000", 168, primes.size());
    }

    @Test
    public void primesFrom0To100(){
        ConcurrentPrimeFinder finder = new ConcurrentPrimeFinder();
        List<Integer> primes = finder.findPrimes(0, 100);

        List<Integer> expected = Arrays.asList(2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97);

        //Assert.assertEquals("There should be 25 primes between 0 and 100", 25, primes.size());
        Assert.assertEquals("All primes should be found", expected, primes);

    }

}
