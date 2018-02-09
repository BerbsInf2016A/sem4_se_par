package implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static implementation.Combinations.merge;


public class PartitionCombinationGenerator {

    private static CachedPrimeCategories categories;

    public static CachedPrimeCategories getCategories() {
        return categories;
    }

    public static void setCategories(CachedPrimeCategories categories) {
        PartitionCombinationGenerator.categories = categories;
    }

    public static PartitionCombinationResult getCombinations(int countOfMissing, int value, int[] filterPrimes) {
        ArrayList<ArrayList<Integer>> partitions = CachedPartition.partition(countOfMissing);
        return getCombinationsForPartitionsAndValue(partitions, value, filterPrimes);
    }


    private static PartitionCombinationResult getCombinationsForPartitionsAndValue(ArrayList<ArrayList<Integer>> partitions, int value, int[] alreadyUsed) {

        // Combine the possible candidates with the partitions.
        Map<Integer, int[]> values = new HashMap<>();
        for (List<Integer> partition : partitions) {
            for (Integer numberOfOccurrence : partition) {
                if (values.containsKey(numberOfOccurrence)) continue;
                values.put(numberOfOccurrence, categories.getBucketForCharacterAndCharacterCount(value, numberOfOccurrence));
            }
        }

        // Filter out the primes, which are already in the set --> This will reduce the created combinations.
        List<Integer> filterValues = Arrays.stream(alreadyUsed).boxed().collect(Collectors.toList());
        Map<Integer, int[]> filteredValues = new HashMap<>();

        for (Map.Entry<Integer, int[]> entry : values.entrySet()) {
            ArrayList<Integer> validEntries = new ArrayList<>();
            for (int prime : entry.getValue()) {
                if (!filterValues.contains(prime)) validEntries.add(prime);
            }
            if (validEntries.size() > 0) {
                filteredValues.put(entry.getKey(), Helpers.toIntArray(validEntries));
            }
        }

        // Generate the combinations.
        PartitionCombinationResult result = new PartitionCombinationResult();
        List<int[]> generatedCombinations = new ArrayList<>();
        for (List<Integer> partition : partitions) {
            if (partition.size() == 1) {
                int element = partition.get(0);
                if (filteredValues.containsKey(element)) result.setSingle(filteredValues.get(element));
            } else {
                HashSet<Integer> distinctOccurrence = new HashSet<>();
                // Check if all parts for this partition are available.
                boolean necessaryDataAvailable = true;
                for (int numberOfValueDigits : partition) {
                    if (!filteredValues.containsKey(numberOfValueDigits)) necessaryDataAvailable = false;
                }
                if (!necessaryDataAvailable) continue;

                int[] partitionCombination = new int[0];
                for (int occurence : partition) {
                    distinctOccurrence.add(occurence);
                }

                for (int occurrence : distinctOccurrence) {
                    partitionCombination = merge(partitionCombination, filteredValues.get(occurrence));
                }
                generatedCombinations.addAll((Combinations.combination(partitionCombination, partition.size())));
            }
        }

        // TODO This slowed the generation..
        /*
        // Filter invalid combinations:
        List<int[]> validCombinations = new ArrayList<>();
        int[] existingSetCounters = ValidatingPrimeSet.countOfDigits(alreadyUsed);
        if (result.hasCombinationResult()) {
            List<int[]> combinations = generatedCombinations;
            for (int[] combination : combinations) {
                int[] newCounters = ValidatingPrimeSet.countOfDigits(combination);
                boolean suitableCombination = true;
                for (int i = 0; i<9; i++) {
                    if (newCounters[i] + existingSetCounters[i] > (i+1)){
                        suitableCombination = false;
                        break;
                    }
                }
                if (suitableCombination) {
                    validCombinations.add(combination);
                }
            }
        }
        // result.setCombination(validCombinations);

        */
        result.setCombination(generatedCombinations);
        return result;
    }
}
