package implementation;




import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class ValidatingPrimeSetTest {
    @Test
    public void addEntry() {
        ValidatingPrimeSet set = new ValidatingPrimeSet();
        List<Integer> primes = Arrays.asList(5, 7, 29, 47, 59, 61, 67, 79, 83, 89, 269, 463, 467, 487, 569, 599, 859, 883, 887);
        boolean isValid = true;
        for (Integer prime : primes) {
            if (!set.tryToAddEntry(prime)) {
                isValid = false;
                break;
            }
        }
        assertTrue(isValid);
    }


    @Test
    public void addEntry_TooMuch() {
        ValidatingPrimeSet set = new ValidatingPrimeSet();
        assertTrue(set.tryToAddEntry(1));
        assertFalse(set.tryToAddEntry(17));
    }

    @Test
    public void countOfMissingDigits() {
        ValidatingPrimeSet set = new ValidatingPrimeSet();
        set.tryToAddEntry(1);
        set.tryToAddEntry(3);

        assertEquals(2, set.countOfMissingOccurrences(2));
        assertEquals(2, set.countOfMissingOccurrences(3));
    }

    @Test
    public void isCountReached() {
        ValidatingPrimeSet set = new ValidatingPrimeSet();
        set.tryToAddEntry(1);
        set.tryToAddEntry(3);

        assertEquals(true, set.countReached(1));
        assertEquals(false, set.countReached(3));
    }

    @Test
    public void createNewSetBasedOnExistingOne() {
        ValidatingPrimeSet set = new ValidatingPrimeSet();
        set.tryToAddEntry(1);
        set.tryToAddEntry(3);

        ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);

        assertArrayEquals(set.getPrimes(), newSet.getPrimes());
    }
}