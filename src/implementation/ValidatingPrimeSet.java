package implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ValidatingPrimeSet {
    int nextInsertIndex;
    private int[] entries;

    public ValidatingPrimeSet(int[] values) {
        this.entries = Arrays.copyOf(values, Configuration.instance.maxNumberOfPrimesPerSet);
        this.nextInsertIndex = (int) Arrays.stream(this.entries).filter(t -> t != 0).count();
    }

    public ValidatingPrimeSet() {

        this.entries = new int[Configuration.instance.maxNumberOfPrimesPerSet];
        this.nextInsertIndex = 0;
    }

    public ValidatingPrimeSet(ValidatingPrimeSet set) {

        this.entries = Arrays.copyOf(set.entries, set.entries.length);
        this.nextInsertIndex = set.nextInsertIndex;
    }

    private static List<Integer> cloneList(List<Integer> list) {
        List<Integer> clone = new ArrayList<>(list.size());
        for (Integer item : list) clone.add(new Integer(item));
        return clone;
    }

    public static int[] countOfDigits(int[] primes) {
        String joinedString = Arrays.toString(primes);
        int[] counts = new int[9];
        counts[0] = joinedString.length() - joinedString.replace("1", "").length();
        counts[1] = joinedString.length() - joinedString.replace("2", "").length();
        counts[2] = joinedString.length() - joinedString.replace("3", "").length();
        counts[3] = joinedString.length() - joinedString.replace("4", "").length();
        counts[4] = joinedString.length() - joinedString.replace("5", "").length();
        counts[5] = joinedString.length() - joinedString.replace("6", "").length();
        counts[6] = joinedString.length() - joinedString.replace("7", "").length();
        counts[7] = joinedString.length() - joinedString.replace("8", "").length();
        counts[8] = joinedString.length() - joinedString.replace("9", "").length();
        return counts;
    }

    public int[] getPrimes() {
        return entries;
    }

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

    private boolean isCountReached(int digit) {
        if (this.nextInsertIndex == 0) return false;
        int currentCount = this.countDigitInArray(digit);
        if (currentCount >= digit) return true;
        return false;
    }

    public boolean addEntry(Integer newEntry) {
        for (int entry : this.entries) {
            if (entry == 0) {
                break;
            }
            if (entry == newEntry) {
                return false;
            }
        }
        if (this.nextInsertIndex != 0) {
            Map<Integer, Integer> newPrimeCounts = CachedIntegerCounts.countCharsInInteger(newEntry);
            for (Map.Entry<Integer, Integer> entry : newPrimeCounts.entrySet()) {
                //TODO
                //int keyValue = entry.getKey() - 48;
                int keyValue = entry.getKey();
                int keyCount = entry.getValue();
                int missingCount = this.countOfMissingDigit(keyValue);
                if (keyCount > missingCount) {
                    return false;
                }
            }
        }

        this.entries[nextInsertIndex] = newEntry;
        nextInsertIndex++;
        // TODO Reverse would be better, Sort is needed in combination with equals and hashcode
        //Arrays.sort(this.entries);
        if (nextInsertIndex >= this.entries.length) {
            // TODO check!
            int g = 0;
        }

        return true;
    }

    public boolean countReached(int i) {
        return this.isCountReached(i);
    }

    int countOccurrences(int arr[], int n, int x) {
        int res = 0;
        for (int i = 0; i < n; i++)
            if (x == arr[i])
                res++;
        return res;
    }

    public int countOfMissingDigit(int digitValue) {
        int count = this.countDigitInArray(digitValue);
        return digitValue - count;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof ValidatingPrimeSet)) return false;
        ValidatingPrimeSet otherSet = (ValidatingPrimeSet) other;
        if (this.entries.length != otherSet.entries.length) return false;
        return Arrays.equals(this.entries, otherSet.entries);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.entries);
    }
}
