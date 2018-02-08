package implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static implementation.Combinations.merge;


public class ConcurrentPrimeCombinationFinder {
    public static AtomicInteger globalDebugCounter = new AtomicInteger();
    public static AtomicInteger globalMinimumSum = new AtomicInteger();
    public static AtomicReference<String> globalMinimumSet = new AtomicReference<>();
    public static LinkedBlockingQueue<String> strings = new LinkedBlockingQueue<>();
    private static CachedPrimeCategories categories;

    public ConcurrentPrimeCombinationFinder(CachedPrimeCategories categories) {
        this.categories = categories;
    }

    public void run(List<ValidatingPrimeSet> sets) throws RuntimeException, InterruptedException {
        // TODO Just get a chance to connect with visual vm
        try {
            Thread.sleep(Configuration.instance.startDelay);
            this.runConcurrent(sets);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    private static Void handleThree(ValidatingPrimeSet set) {
        int valueOfDigit = 3;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = ConcurrentPrimeCombinationFinder::handleFour;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                handleFour(set);
                return null;
            case 1:
                PartitionCombinationResult oneMissingCombinations = getCombinations(1, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, oneMissingCombinations, nextFunction);
                return null;
            case 2:
                PartitionCombinationResult twoMissingCombination = getCombinations(2, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, twoMissingCombination, nextFunction);
                return null;
            case 3:
                PartitionCombinationResult threeMissingCombination = getCombinations(3, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, threeMissingCombination, nextFunction);
                return null;
        }
        return null;
    }

    private static void handleCombinationsWithNextFunction(ValidatingPrimeSet set, PartitionCombinationResult combinations, Function<ValidatingPrimeSet,Void> nextFunction){
        if (combinations.hasSingleResult()) {
            for (int singleResult : combinations.getSingle()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.tryToAddEntry(singleResult)) {
                    nextFunction.apply(newSet);
                }
            }
        }
        if (combinations.hasCombinationResult()) {
            for (int[] combination : combinations.getCombinations()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                boolean setIsValid = true;
                for (int prime : combination) {
                    if (!newSet.tryToAddEntry(prime)) {
                        setIsValid = false;
                        break;
                    }
                }
                if (setIsValid) {
                    nextFunction.apply(newSet);
                }
            }
        }
    }



    private static Void handleFour(ValidatingPrimeSet set) {
        int valueOfDigit = 4;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = ConcurrentPrimeCombinationFinder::handleFive;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                handleFive(set);
                return null;
            case 1:
                PartitionCombinationResult oneMissingCombinations = getCombinations(1, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, oneMissingCombinations, nextFunction);
                return null;
            case 2:
                PartitionCombinationResult twoMissingCombination = getCombinations(2, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, twoMissingCombination, nextFunction);
                return null;
            case 3:
                PartitionCombinationResult threeMissingCombination = getCombinations(3, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, threeMissingCombination, nextFunction);
                return null;
            case 4:
                PartitionCombinationResult fourMissingCombination = getCombinations(4, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fourMissingCombination, nextFunction);
                return null;
        }
        return null;
    }



    public void runConcurrent(List<ValidatingPrimeSet> sets) {
        try {

            final List<Callable<Boolean>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);

            int sliceSize = sets.size() / Configuration.instance.maximumNumberOfThreads;
            System.out.format("Using %d threads. SliceSize: %d \n", Configuration.instance.maximumNumberOfThreads, sliceSize);


            for (int i = 0; i <= sets.size(); i += sliceSize) {
                final int from = i;
                int to = i + sliceSize;
                if (to > sets.size())
                    to = sets.size();
                final int end = to;
                List<ValidatingPrimeSet> sublist = sets.subList(from, end);
                partitions.add(() -> generateSets(sublist));
            }

            final List<Future<Boolean>> resultFromParts = executorPool.invokeAll(partitions, Configuration.instance.maxTimeOutInSeconds, TimeUnit.SECONDS);
            // Shutdown will not kill the spawned threads, but shutdownNow will set a flag which can be queried in the running
            // threads to end the execution.
            //executorPool.shutdown();
            executorPool.shutdownNow();

            for (final Future<Boolean> result : resultFromParts)
                result.get();

        } catch (CancellationException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean generateSets(List<ValidatingPrimeSet> sets) {
        for (ValidatingPrimeSet entry : sets) {
            ValidatingPrimeSet set = new ValidatingPrimeSet(entry);
            this.handleOne(set);
            if (Thread.interrupted()) return false;
        }
        return true;
    }

    private void handleOne(ValidatingPrimeSet set) {
        int valueOfDigit = 1;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                this.handleTwo(set);
                return;
            case 1:
                int[] oneMissingCombinations = this.categories.getBucketForCharacterAndCharacterCount(1, 1);
                HandleMissingOne(set, oneMissingCombinations);
                return;
        }
    }

    private void HandleMissingOne(ValidatingPrimeSet set, int[] oneMissingCombinations) {
        for (int entry : oneMissingCombinations) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.tryToAddEntry(entry)) {
                this.handleTwo(newSet);
            }
        }
    }

    private Void handleTwo(ValidatingPrimeSet set) {
        int valueOfDigit = 2;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = ConcurrentPrimeCombinationFinder::handleThree;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                this.handleThree(set);
                return null;
            case 1:
                PartitionCombinationResult oneMissingCombinations = this.getCombinations(1, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, oneMissingCombinations, nextFunction);
                return null;
            case 2:
                PartitionCombinationResult twoMissingCombination = this.getCombinations(2, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, twoMissingCombination, nextFunction);
                return null;
        }
        return null;
    }

    private static boolean preCheckConditions(ValidatingPrimeSet set, int i) {
        if (Thread.interrupted()) {
            // Executor has probably asked us to stop
            System.out.println(Thread.currentThread().toString() + " has been interrupted!");
            throw new CancellationException("Thread has been requested to stop");
        }
        return set.countReached(i - 1);
    }



    private static Void handleFive(ValidatingPrimeSet set) {
        int valueOfDigit = 5;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = ConcurrentPrimeCombinationFinder::handleSix;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                handleSix(set);
                return null;
            case 1:
                PartitionCombinationResult oneMissingCombinations = getCombinations(1, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, oneMissingCombinations, nextFunction);
                return null;
            case 2:
                PartitionCombinationResult twoMissingCombination = getCombinations(2, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, twoMissingCombination, nextFunction);
                return null;
            case 3:
                PartitionCombinationResult threeMissingCombination = getCombinations(3, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, threeMissingCombination, nextFunction);
                return null;
            case 4:
                PartitionCombinationResult fourMissingCombination = getCombinations(4, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fourMissingCombination, nextFunction);
                return null;
            case 5:
                PartitionCombinationResult fiveMissingCombination = getCombinations(5, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fiveMissingCombination, nextFunction);
                return null;
        }
        return null;
    }


    private static Void handleSix(ValidatingPrimeSet set) {
        int valueOfDigit = 6;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = ConcurrentPrimeCombinationFinder::handleSeven;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                handleSeven(set);
                return null;
            case 1:
                PartitionCombinationResult oneMissing = getCombinations(1, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, oneMissing, nextFunction);
                return null;
            case 2:
                PartitionCombinationResult twoMissing = getCombinations(2, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, twoMissing, nextFunction);
                return null;
            case 3:
                PartitionCombinationResult threeMissing = getCombinations(3, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, threeMissing, nextFunction);
                return null;
            case 4:
                PartitionCombinationResult fourMissing = getCombinations(4, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fourMissing, nextFunction);
                return null;
            case 5:
                PartitionCombinationResult fiveMissing = getCombinations(5, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fiveMissing, nextFunction);
                return null;
            case 6:
                PartitionCombinationResult sixMissing = getCombinations(6, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, sixMissing, nextFunction);
                return null;
        }
        return null;
    }



    private static Void handleSeven(ValidatingPrimeSet set) {
        int valueOfDigit = 7;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = ConcurrentPrimeCombinationFinder::handleEight;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                handleEight(set);
                return null;
            case 1:
                PartitionCombinationResult oneMissing = getCombinations(1, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, oneMissing, nextFunction);
                return null;
            case 2:
                PartitionCombinationResult twoMissing = getCombinations(2, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, twoMissing, nextFunction);
                return null;
            case 3:
                PartitionCombinationResult threeMissing = getCombinations(3, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, threeMissing, nextFunction);
                return null;
            case 4:
                PartitionCombinationResult fourMissing = getCombinations(4, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fourMissing, nextFunction);
                return null;
            case 5:
                PartitionCombinationResult fiveMissing = getCombinations(5, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fiveMissing, nextFunction);
                return null;
            case 6:
                PartitionCombinationResult sixMissing = getCombinations(6, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, sixMissing, nextFunction);
                return null;
            case 7:
                PartitionCombinationResult sevenMissing = getCombinations(7, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, sevenMissing, nextFunction);
                return null;
        }
        return null;
    }


    private static Void handleEight(ValidatingPrimeSet set) {
        int valueOfDigit = 8;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = ConcurrentPrimeCombinationFinder::handleNine;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                handleNine(set);
                return null;
            case 1:
                PartitionCombinationResult oneMissing = getCombinations(1, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, oneMissing, nextFunction);
                return null;
            case 2:
                PartitionCombinationResult twoMissing = getCombinations(2, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, twoMissing, nextFunction);
                return null;
            case 3:
                PartitionCombinationResult threeMissing = getCombinations(3, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, threeMissing, nextFunction);
                return null;
            case 4:
                PartitionCombinationResult fourMissing = getCombinations(4, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fourMissing, nextFunction);
                return null;
            case 5:
                PartitionCombinationResult fiveMissing = getCombinations(5, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fiveMissing, nextFunction);
                return null;
            case 6:
                PartitionCombinationResult sixMissing = getCombinations(6, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, sixMissing, nextFunction);
                return null;
            case 7:
                PartitionCombinationResult sevenMissing = getCombinations(7, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, sevenMissing, nextFunction);
                return null;
            case 8:
                PartitionCombinationResult eightMissing = getCombinations(8, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, eightMissing, nextFunction);
                return null;
        }
        return null;
    }


    private static Void handleNine(ValidatingPrimeSet set) {
        int valueOfDigit = 9;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = ConcurrentPrimeCombinationFinder::handlePossibleResult;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                handlePossibleResult(set);
                return null;
            case 1:
                PartitionCombinationResult oneMissing = getCombinations(1, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, oneMissing, nextFunction);
                return null;
            case 2:
                PartitionCombinationResult twoMissing = getCombinations(2, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, twoMissing, nextFunction);
                return null;
            case 3:
                PartitionCombinationResult threeMissing = getCombinations(3, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, threeMissing, nextFunction);
                return null;
            case 4:
                PartitionCombinationResult fourMissing = getCombinations(4, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fourMissing, nextFunction);
                return null;
            case 5:
                PartitionCombinationResult fiveMissing = getCombinations(5, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fiveMissing, nextFunction);
                return null;
            case 6:
                PartitionCombinationResult sixMissing = getCombinations(6, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, sixMissing, nextFunction);
                return null;
            case 7:
                PartitionCombinationResult sevenMissing = getCombinations(7, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, sevenMissing, nextFunction);
                return null;
            case 8:
                PartitionCombinationResult eightMissing = getCombinations(8, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, eightMissing, nextFunction);
                return null;
            case 9:
                PartitionCombinationResult nineMissing = getCombinations(9, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, nineMissing, nextFunction);
                return null;
        }
        return null;
    }



    private static Void handlePossibleResult(ValidatingPrimeSet set) {
        boolean isValid = Validator.validateFinalSet(set);
        if (isValid) {
            calculateResultAndPrintSet(set);
        }
        return null;
    }

    /**
     * Calculates the sum of a set and prints the information (dependend on the current configuration).
     *
     * @param set The set of primes.
     */
    private static void calculateResultAndPrintSet(ValidatingPrimeSet set) {
        int counter = globalDebugCounter.incrementAndGet();
        if (counter % Configuration.instance.printFoundSetsCounterInterval == 0) {
            System.out.println("Found valid sets: " + counter);
            System.out.println("Stringsize: " + strings.size() + " distinct count: " + strings.stream().distinct().count());
        }

        int[] primes = Arrays.stream(set.getPrimes()).filter(t -> t != 0).toArray();
        int sum = Arrays.stream(primes).sum();

        int globalMinimum = globalMinimumSum.get();
        if (sum < globalMinimum || globalMinimum == 0) {
            globalMinimumSum.set(sum);
            String primesString = Arrays.toString(primes);
            globalMinimumSet.set(primesString);
            System.out.println("---- New minimum found!: sum: " + sum + " " + primesString);
        } else {
            if (Configuration.instance.printFoundSetsDuringRuntime) {
                primes = Arrays.stream(primes).sorted().toArray();
                System.out.println("Found: Sum: " + sum + " " + Arrays.toString(primes));
            }
        }


        // TODO Remove global string array
        strings.add(Arrays.toString(primes));

    }

    private void printDebugSetData(ValidatingPrimeSet set) {
        globalDebugCounter.incrementAndGet();
        List<Integer> list = Arrays.stream(set.getPrimes()).boxed().collect(Collectors.toList());
        System.out.println(list.stream().map(t -> t.toString()).collect(Collectors.joining(", ")));
    }

    private static PartitionCombinationResult getCombinations(int countOfMissing, int value, int[] filterPrimes) {
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
