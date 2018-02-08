package implementation;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Categorizes integers by their count of containing digits.
 */
public class CachedPrimeCategories {
    /**
     * The primes which should be categorized.
     */
    private final int[] primes;

    /**
     * A cache for already calculated values.
     */
    private final ConcurrentHashMap<String, int[]> cache;

    /**
     * Constructor for the CachedPrimeCategories.
     *
     * @param primes
     */
    public CachedPrimeCategories(int[] primes) {
        this.cache = new ConcurrentHashMap<>();
        this.primes = primes;
    }

    /**
     * Analyzes an integer, if it contains a specific character in terms of a specified count.
     *
     * @param number The number to check.
     * @param value  The value of the character, which should be counted.
     * @param count  The wanted count.
     * @return True if the number fulfills the requirements, false if not.
     */
    private static boolean filterForCharacterAndCount(int number, char value, int count) {
        String stringValue = String.valueOf(number);
        return stringValue.chars().filter(t -> t == value).count() == count;
    }

    /**
     * Get all primes which contain the specified digit exactly x times (specified by count).
     *
     * @param digit The value of the digit.
     * @param count The count of wanted occurrences.
     * @return All prime numbers fulfilling the requirements.
     */
    public int[] getBucketForCharacterAndCharacterCount(int digit, int count) {
        char charValue = Character.forDigit(digit, 10);
        String key = digit + "_" + count;
        if (this.cache.containsKey(key)) {
            return this.cache.get(key);
        } else {
            this.cache.put(key, Arrays.stream(this.primes).filter(t -> filterForCharacterAndCount(t, charValue, count)).toArray());
        }
        return this.cache.get(key);
    }
}
