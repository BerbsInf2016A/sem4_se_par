package implementation;

import java.util.Arrays;
import java.util.Map;

/**
 * A set of prime numbers which validates, if a new entry can be added.
 */
public class ValidatingPrimeSet {
    /**
     * The index for the next insertion.
     */
    private int nextInsertIndex;
    /**
     * The prime numbers in this set.
     */
    private int[] entries;

    /**
     * Constructor for the ValidatingPrimeSet.
     */
    public ValidatingPrimeSet() {

        this.entries = new int[Configuration.instance.maxNumberOfPrimesPerSet];
        this.nextInsertIndex = 0;
    }

    /**
     * Creates a new ValidatingPrimeSet, based on a existing one.
     *
     * @param set The existing one, which is used as template for the new one.
     */
    public ValidatingPrimeSet(ValidatingPrimeSet set) {

        this.entries = Arrays.copyOf(set.entries, set.entries.length);
        this.nextInsertIndex = set.nextInsertIndex;
    }

    /**
     * Getter for the prime numbers in this set.
     *
     * @return An integer array containing the prime numbers of this set.
     */
    public int[] getPrimes() {
        return entries;
    }

    /**
     * Get the count of occurrences of a digit in the set.
     *
     * @param digit The digit to search for.
     * @return The count of occurrences.
     */
    private int countDigitInArray(int digit) {
        int counter = 0;
        for (int entry : this.entries) {
            if (entry == 0) break;
            int p = entry;
            while (p != 0) {
                int val = p % 10;
                if (val == digit) counter++;
                p = p / 10;
            }
        }
        return counter;
    }

    /**
     * Check if the count for a specified digit is already reached.
     *
     * @param digit The digit to check against.
     * @return True if the count has been reached, false if not.
     */
    private boolean isCountReached(int digit) {
        if (this.nextInsertIndex == 0) return false;
        int currentCount = this.countDigitInArray(digit);
        if (currentCount >= digit) return true;
        return false;
    }

    /**
     * Tries to add an entry to the set.
     *
     * @param newEntry The new entry, which should be added.
     * @return True if the entry could be added, false if not.
     */
    public boolean tryToAddEntry(Integer newEntry) {
        // The new entry can be added, if the set is empty.
        if (this.nextInsertIndex == 0) {
            this.entries[nextInsertIndex] = newEntry;
            nextInsertIndex++;
            return true;
        }

        // Iterate the existing entries and check if the new entry
        // is already part of the set.
        for (int entry : this.entries) {
            if (entry == 0) break;
            if (entry == newEntry) return false;
        }
        // Check if the new entry can be added, without violating the count rules.
        if (this.nextInsertIndex != 0) {
            Map<Integer, Integer> newPrimeCounts = CachedIntegerCounts.countCharsInInteger(newEntry);
            for (Map.Entry<Integer, Integer> entry : newPrimeCounts.entrySet()) {
                int keyValue = entry.getKey();
                int keyCount = entry.getValue();
                int missingCount = this.countOfMissingOccurrences(keyValue);
                if (keyCount > missingCount) return false;
            }
        }

        // Add the entry.
        this.entries[nextInsertIndex] = newEntry;
        nextInsertIndex++;

        return true;
    }

    /**
     * True if the count for the parameter has already been reached.
     *
     * @param digitValue The value of the digit to check.
     * @return True if the count has already been reached, false if not.
     */
    public boolean countReached(int digitValue) {
        return this.isCountReached(digitValue);
    }


    /**
     * Get the count of missing digit occurrences in the set.
     *
     * @param digitValue The value of the digit to check.
     * @return The count of missing occurrences.
     */
    public int countOfMissingOccurrences(int digitValue) {
        int count = this.countDigitInArray(digitValue);
        return digitValue - count;
    }
}
