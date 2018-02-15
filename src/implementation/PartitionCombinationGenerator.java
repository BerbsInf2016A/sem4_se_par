package implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static implementation.Combinations.merge;

/**
 * Helper methods to generate combinations, based on a partitioned value.
 */
class PartitionCombinationGenerator {

    /**
     * The cached prime numbers, categorized by the different occurrences of digits.
     */
    private static CachedPrimeCategories categories;

    /**
     * Getter for the categories.
     *
     * @return The categories.
     */
    public static CachedPrimeCategories getCategories() {
        return categories;
    }

    /**
     * Setter for the categories.
     *
     * @param categories The categories to set.
     */
    public static void setCategories(CachedPrimeCategories categories) {
        PartitionCombinationGenerator.categories = categories;
    }

    /**
     * Get all combinations which fulfill the requirements, specified by count of missing and the value.
     * E.g. The six is missing three times -> countOfMissing = 3; value = 6 and filterPrimes is the current set.
     *
     * @param countOfMissing How often is the value missing.
     * @param value          The value of the digit which is wanted.
     * @param filterPrimes   The current set, to filter out combinations, which contain prime numbers, which are already
     *                       in the set.
     * @return A partitionCombinationResult, containing the different combinations to fulfill the missing numbers.
     */
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
                distinctOccurrence.addAll(partition);

                for (int occurrence : distinctOccurrence) {
                    partitionCombination = merge(partitionCombination, filteredValues.get(occurrence));
                }
                generatedCombinations.addAll((Combinations.combination(partitionCombination, partition.size())));
            }
        }

        // The following code should filter out all combinations, which can not be added to the existing set.
        // But filtering here, is slower than trying to add a combination and throw it away.
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
