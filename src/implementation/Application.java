package implementation;


import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static implementation.Combinations.merge;

public class Application {
    public static int countDigits(long number) {
        number = Math.abs(number);

        int count = 0;

        if(number<10) {
            count++;
        }
        else {
            count += countDigits(number/10) + 1;
        }

        return count;
    }

    private void printNumberCounts(List<Integer> numbers){

        int[] array = new int[10];
        for (Integer number : numbers) {
            String adjustedValue = number.toString().replace("0", "");
            for (int i = 0; i < adjustedValue.length(); i++) {
                Character c = adjustedValue.charAt(i);
                switch (c) {
                    case ',':
                        continue;
                    case '0':
                        // System.out.println("Validated: False Value: " + value);
                    case '1':
                        array[1] = array[1] + 1;
                        break;
                    case '2':
                        array[2] = array[2] + 1;
                        break;
                    case '3':
                        array[3] = array[3] + 1;
                        break;
                    case '4':
                        array[4] = array[4] + 1;
                        break;
                    case '5':
                        array[5] = array[5] + 1;
                        break;
                    case '6':
                        array[6] = array[6] + 1;
                        break;
                    case '7':
                        array[7] = array[7] + 1;
                        break;
                    case '8':
                        array[8] = array[8] + 1;
                        break;
                    case '9':
                        array[9] = array[9] + 1;
                        break;
                }
            }
        }

        for (int i = 0; i < 10; i++) {
            int digitCount = array[i];
            System.out.println(i + " " + digitCount);
        }

    }
    int[] toIntArray(List<Integer> list){
        int[] ret = new int[list.size()];
        for(int i = 0;i < ret.length;i++)
            ret[i] = list.get(i);
        return ret;
    }

    private int[] getNumbersContainingDigit(List<Integer> source, int digit){
        ArrayList<Integer> hits = new ArrayList<>();

        for (int entry : source ) {
            String value = String.valueOf(entry);
            for (Character c : value.toCharArray() ) {
                    if (Character.getNumericValue(c) == digit){
                        hits.add(entry);
                    }
            }
        }
        return toIntArray(hits.stream().distinct().collect(Collectors.toList()));
    }

    public void run() {

        long runtimeStart = System.currentTimeMillis();

        // Generate
        ConcurrentPrimeFinder finder = new ConcurrentPrimeFinder();
        List<Integer> p = finder.findPrimes(0, 1000);
        System.out.println("Found primes "  +  p.size());
        p = p.stream().filter(t -> Validator.isCandidate(t)).collect(Collectors.toList());
        System.out.println("Filtered valid primes "  +  p.size());

        // Preset some combinations:
        int[] primesContainingNine = this.getNumbersContainingDigit(p, 8);
        List<int[]> combs =  Combinations.combination(primesContainingNine, 8);
        PrimeCategorizer categorizer = new PrimeCategorizer(toIntArray(p));

        List<ValidatingPrimeSet> sets = new ArrayList<>();
        for (int[] preGeneratedValue : combs ) {
            ValidatingPrimeSet set = new ValidatingPrimeSet();
            boolean isValid = true;
            for (int prime : preGeneratedValue ) {
                if (!set.addEntry(prime)){
                    isValid = false;
                    break;
                }
            }
            if (isValid) { sets.add(set); }
        }
        this.printNumberCounts(p);

        ConcurrentPrimeCombinationFinder runner = new ConcurrentPrimeCombinationFinder(categorizer);
        runner.run(sets);

        System.out.println("runtime (ms)   : " + (System.currentTimeMillis() - runtimeStart));
    }


}
