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
        BigInteger maxValue = new BigInteger("2").pow(p.size());
        System.out.println("Filtered valid primes "  +  p.size());
        System.out.printf("There are %s^%s possible combinations: %s %n", 2, p.size(), maxValue);

        int[] primesContainingNine = this.getNumbersContainingDigit(p, 9);


        //List<int[]> combs =  Combinations.combination(primesContainingNine, 9);


        PrimeCategorizer categorizer = new PrimeCategorizer(toIntArray(p));
        ArrayList<ArrayList<Integer>> partitions =  Partition.partition(3);
        int[] filter = new int[2];
        filter[0] = 5;
        filter[1] = 59;
        PartitionCombinationResult test = getCombinationsForPartionsAndValue(partitions, 5, filter, categorizer);

        ArrayList<String> strings = new ArrayList<>();
        for (int[] comb : test.getCombination() ) {
            List<Integer> list = Arrays.stream(comb).boxed().collect(Collectors.toList());
            Collections.sort(list);
            strings.add(list.stream().map(t -> t.toString()).collect(Collectors.joining(", ")));
        }

        System.out.println("Stringsize: " + strings.size() + " distinct count: " + strings.stream().distinct().count());
        this.printNumberCounts(p);

        ConcurrentPrimeCombinationFinder runner = new ConcurrentPrimeCombinationFinder(categorizer);
        runner.run();
        System.out.println("Strings: " + ConcurrentPrimeCombinationFinder.strings);
        Set<String> duplicates = runner.findDuplicates(ConcurrentPrimeCombinationFinder.strings);
        System.out.println("Duplicate strings: " + duplicates.size());
        int z = 4;


        System.out.println("runtime (ms)   : " + (System.currentTimeMillis() - runtimeStart));
    }

    private PartitionCombinationResult getCombinationsForPartionsAndValue(ArrayList<ArrayList<Integer>> partitions, int value, int[] alreadyUsed, PrimeCategorizer categorizer) {

        Map<Integer, int[]> values = new HashMap<>();
        for (List<Integer> partition : partitions ) {
            for (Integer numberOfOccurence : partition ) {
                if (values.containsKey(numberOfOccurence)) continue;
                values.put(numberOfOccurence, categorizer.getBucketForCharacterAndCharacterCount(value, numberOfOccurence));
            }
        }

        List<Integer> filterValues = Arrays.stream(alreadyUsed).boxed().collect(Collectors.toList());
        Map<Integer, int[]> filteredValues = new HashMap<>();

        for (Map.Entry<Integer, int[]> entry : values.entrySet()) {
            ArrayList<Integer> validEntries = new ArrayList<>();
            for(int prime : entry.getValue()){
                if (!filterValues.contains(prime)) validEntries.add(prime);
            }
            filteredValues.put(entry.getKey(), toIntArray(validEntries));
        }

        PartitionCombinationResult result = new PartitionCombinationResult();
        for (List<Integer> partition : partitions ) {
            if(partition.size() == 1 ){
                int element = partition.get(0);
                if (filteredValues.containsKey(element)) result.setSingle(filteredValues.get(element));
            } else {
                HashSet<Integer> distinctOccurrence = new HashSet<>();
                int[] partitionCombination = new int[0];
                for (int occurence : partition ) {
                    if ( !filteredValues.containsKey(occurence)) continue;
                    distinctOccurrence.add(occurence);
                }

                for (int occurrence : distinctOccurrence ) {
                    partitionCombination = merge(partitionCombination, filteredValues.get(occurrence));
                }

                result.addCombination((Combinations.combination(partitionCombination, partition.size())));
            }

        }

        return result;
    }
}
