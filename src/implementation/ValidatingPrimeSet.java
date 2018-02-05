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

    public List<Integer> getPrimes() {
        return entries;
    }

    private List<Integer> entries;

    public ValidatingPrimeSet() {
        this.entries = new ArrayList<>();
    }

    public ValidatingPrimeSet(ValidatingPrimeSet set) {
        this.entries = cloneList(set.entries);
    }

    public void clear(){
        this.entries.clear();
    }



    private boolean isCountReached(Character value){
        Long count = this.entries.stream().map(t -> t.toString()).collect(Collectors.joining())
                .chars().filter( digit -> digit == value).count();
        return Character.getNumericValue(value) == count;
    }

    public boolean addEntry(Integer newEntry) {
        if (this.entries.contains(newEntry)){
            return false;
        }
        String prime = newEntry.toString();
        for (int i = 0; i < prime.length(); i++) {
            Character c = prime.charAt(i);
            if( isCountReached(c)) {
                return false;
            }
        }
        this.entries.add(newEntry);
        Collections.sort(this.entries);
        return true;
    }

    public boolean countReached(int i) {
        return this.isCountReached(Character.forDigit(i, 10));
    }

    public int countOfMissingDigit(int digitValue) {
        String joinedString = this.entries.stream().map(t -> t.toString()).collect(Collectors.joining());
        long count = joinedString.chars().filter( d -> d-48 == digitValue).count();
        return digitValue - (int)count;
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof ValidatingPrimeSet))return false;
        ValidatingPrimeSet otherSet = (ValidatingPrimeSet)other;
        if (this.entries.size() != otherSet.entries.size()) return false;
        for (int i = 0; i < this.entries.size(); i++) {
            if (this.entries.get(i) != otherSet.entries.get(i)) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.entries);
    }
}
