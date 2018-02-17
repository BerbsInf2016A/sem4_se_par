package implementation;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidatorTest {
    @Test
    public void isUsablePrimeCandidate() {
        assertFalse("Numbers containing a zero are invalid", Validator.isUsablePrimeCandidate(107));
        assertFalse("Numbers containing two ones are invalid", Validator.isUsablePrimeCandidate(311));
        assertTrue("Number should be valid", Validator.isUsablePrimeCandidate(233));
    }

    @Test
    public void validateFinalSet() {

        ValidatingPrimeSet validSet = new ValidatingPrimeSet();
        ValidatingPrimeSet invalidSet = new ValidatingPrimeSet();
        int[] primes = {89, 281, 283, 787, 853, 857, 859, 863, 467, 499, 449, 569, 659, 67, 769, 79};
        for (int prime : primes) {
            validSet.tryToAddEntry(prime);
        }

        assertTrue("Set should be valid", Validator.validateFinalSet(validSet));
        assertFalse("Empty should be invalid", Validator.validateFinalSet(invalidSet));
    }

}