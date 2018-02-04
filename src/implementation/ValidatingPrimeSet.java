package implementation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValidatingPrimeSet {
    private static List<Long> cloneList(List<Long> list) {
        List<Long> clone = new ArrayList<>(list.size());
        for (Long item : list) clone.add(new Long(item));
        return clone;
    }

    public List<Long> getPrimes() {
        return entries;
    }

    private List<Long> entries;
    int[] counts;

    public ValidatingPrimeSet() {
        this.entries = new ArrayList<>();
        this.counts = new int[10];
    }

    public ValidatingPrimeSet(ValidatingPrimeSet set) {
        this.entries = cloneList(set.entries);
        this.counts = set.counts.clone();
    }

    public void clear(){
        this.entries.clear();
        Arrays.fill(this.counts, 0);
    }



    public boolean addEntry(Long newEntry) {
        if (this.entries.contains(newEntry)){
            return false;
        }
        String prime = newEntry.toString();
        for (int i = 0; i < prime.length(); i++) {
            Character c = prime.charAt(i);
            switch (c) {
                case '0':
                    return false;
                case '1':
                    if (this.counts[1] == 1) { return false; }
                    this.counts[1] = this.counts[1] + 1;
                    break;
                case '2':
                    if (this.counts[2] == 2) { return false; }
                    this.counts[2] = this.counts[2] + 1;
                    break;
                case '3':
                    if (this.counts[3] == 3) { return false; }
                    this.counts[3] = this.counts[3] + 1;
                    break;
                case '4':
                    if (this.counts[4] == 4) { return false; }
                    this.counts[4] = this.counts[4] + 1;
                    break;
                case '5':
                    if (this.counts[5] == 5) { return false; }
                    this.counts[5] = this.counts[5] + 1;
                    break;
                case '6':
                    if (this.counts[6] == 6) { return false; }
                    this.counts[6] = this.counts[6] + 1;
                    break;
                case '7':
                    if (this.counts[7] == 7) { return false; }
                    this.counts[7] = this.counts[7] + 1;
                    break;
                case '8':
                    if (this.counts[8] == 8) { return false; }
                    this.counts[8] = this.counts[8] + 1;
                    break;
                case '9':
                    if (this.counts[9] == 9) { return false; }
                    this.counts[9] = this.counts[9] + 1;
                    break;
            }
        }
        this.entries.add(newEntry);
        return true;
    }

    public boolean countReached(int i) {
        if (this.counts[i] == i) {
            return true;
        } else {
            return false;
        }
    }

    public int countOfMissingDigit(int digit) {
        return digit - this.counts[digit];
    }
}
