package implementation;

import com.sun.deploy.util.StringUtils;

import java.lang.reflect.Array;
import java.nio.charset.CharacterCodingException;
import java.util.*;
import java.util.stream.Collectors;

public class ValidatingPrimeSet {
    public ValidatingPrimeSet(int[] values) {
        this.entries = Arrays.copyOf(values, Configuration.instance.maxNumberOfPrimesPerSet);
        this.nextInsertIndex = (int) Arrays.stream(this.entries).filter(t -> t != 0).count();
    }

    private static List<Integer> cloneList(List<Integer> list) {
        List<Integer> clone = new ArrayList<>(list.size());
        for (Integer item : list) clone.add(new Integer(item));
        return clone;
    }

    public int[] getPrimes() {
        return entries;
    }
    int nextInsertIndex;

    private int[] entries;

    public ValidatingPrimeSet() {

        this.entries = new int[Configuration.instance.maxNumberOfPrimesPerSet];
        this.nextInsertIndex = 0;
    }

    public ValidatingPrimeSet(ValidatingPrimeSet set) {

        this.entries = Arrays.copyOf(set.entries, set.entries.length);
        this.nextInsertIndex = set.nextInsertIndex;
    }


    private boolean isCountReached(Character value){
        if(this.nextInsertIndex == 0 ) return false;
        int numericValue = Character.getNumericValue(value);
        int counter = 0;
        int[] entries1 = this.entries;
        for (int i = 0, entries1Length = entries1.length; i < entries1Length; i++) {
            int entry = entries1[i];
            if (entry == 0 ) break;
            counter += String.valueOf(entry).chars().filter(digit -> digit == value).count();
            if (counter == numericValue) return true;
        }

        return false;

        // Long count = Arrays.toString(this.entries).chars().filter( digit -> digit == value).count();
        // return Character.getNumericValue(value) == count;
    }
    private Map<Character, Integer> countCharsInString(String value){
        int len = value.length();
        Map<Character, Integer> numChars = new HashMap<Character, Integer>(Math.min(len, 26));

        for (int i = 0; i < len; ++i)        {
            char charAt = value.charAt(i);
            if (!numChars.containsKey(charAt))
            {
                numChars.put(charAt, 1);
            }
            else
            {
                numChars.put(charAt, numChars.get(charAt) + 1);
            }
        }
        return numChars;
    }
    public boolean addEntry(Integer newEntry) {
        for (int entry : this.entries ) {
            if (entry == 0) { break; }
            if (entry == newEntry) {
                return false;
            }
        }
        if (this.nextInsertIndex != 0) {
            String prime = newEntry.toString();
            Map<Character, Integer> newPrimeCounts = this.countCharsInString(prime);
            for (Map.Entry<Character, Integer> entry : newPrimeCounts.entrySet() ) {
                int keyValue = entry.getKey() - 48;
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
        return this.isCountReached(Character.forDigit(i, 10));
    }

    int countOccurrences(int arr[], int n, int x)
    {
        int res = 0;
        for (int i=0; i<n; i++)
            if (x == arr[i])
                res++;
        return res;
    }

    public int countOfMissingDigit(int digitValue) {
        String joinedString = Arrays.toString(this.entries);
        long count = joinedString.chars().filter( d -> d-48 == digitValue).count();
        return digitValue - (int)count;
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof ValidatingPrimeSet))return false;
        ValidatingPrimeSet otherSet = (ValidatingPrimeSet)other;
        if (this.entries.length != otherSet.entries.length) return false;
        return Arrays.equals(this.entries, otherSet.entries);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.entries);
    }

    public static int[] countOfDigits(int[] primes) {
        String joinedString = Arrays.toString(primes);
        int[] counts = new int[9];
        counts[0] =  joinedString.length() - joinedString.replace("1", "").length();
        counts[1] =  joinedString.length() - joinedString.replace("2", "").length();
        counts[2] =  joinedString.length() - joinedString.replace("3", "").length();
        counts[3] =  joinedString.length() - joinedString.replace("4", "").length();
        counts[4] =  joinedString.length() - joinedString.replace("5", "").length();
        counts[5] =  joinedString.length() - joinedString.replace("6", "").length();
        counts[6] =  joinedString.length() - joinedString.replace("7", "").length();
        counts[7] =  joinedString.length() - joinedString.replace("8", "").length();
        counts[8] =  joinedString.length() - joinedString.replace("9", "").length();
        return counts;
    }
}
