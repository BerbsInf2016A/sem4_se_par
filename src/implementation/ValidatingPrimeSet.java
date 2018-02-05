package implementation;

import java.lang.reflect.Array;
import java.nio.charset.CharacterCodingException;
import java.util.*;
import java.util.stream.Collectors;

public class ValidatingPrimeSet {
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
        Long count = Arrays.toString(this.entries).chars().filter( digit -> digit == value).count();
        return Character.getNumericValue(value) == count;
    }

    public boolean addEntry(Integer newEntry) {
        for (int entry : this.entries ) {
            if (entry == newEntry) return false;
        }
        String prime = newEntry.toString();
        for (int i = 0; i < prime.length(); i++) {
            Character c = prime.charAt(i);
            if( isCountReached(c)) {
                return false;
            }
        }
        this.entries[nextInsertIndex] = newEntry;
        nextInsertIndex++;
        // TODO Reverse would be better.
        Arrays.sort(this.entries);
        if (nextInsertIndex >= this.entries.length) {
            // TODO check!
            int g = 0;
        }

        return true;
    }

    public boolean countReached(int i) {
        return this.isCountReached(Character.forDigit(i, 10));
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
}
