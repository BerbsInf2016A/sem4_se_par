package implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static implementation.Combinations.merge;


public class ConcurrentPrimeCombinationFinder {
    public static AtomicInteger globalDebugCounter = new AtomicInteger();
    public static AtomicInteger globalMinimumSum = new AtomicInteger();
    public static AtomicReference<String> globalMinimumSet = new AtomicReference<>();
    public static LinkedBlockingQueue<String> strings = new LinkedBlockingQueue<>();
    private final PrimeCategorizer categorizer;

    public ConcurrentPrimeCombinationFinder(PrimeCategorizer categorizer) {
        this.categorizer = categorizer;
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

    private void handleThree(ValidatingPrimeSet set) {
        int valueOfDigit = 3;
        if (!precheck(set, valueOfDigit)) return;
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing) {
            case 0:
                this.handleFour(set);
                return;
            case 1:
                PartitionCombinationResult oneMissingCombinations = this.getCombinations(1, valueOfDigit, set.getPrimes());
                HandleThreeCombination(set, oneMissingCombinations);
                return;
            case 2:
                PartitionCombinationResult twoMissingCombination = this.getCombinations(2, valueOfDigit, set.getPrimes());
                HandleThreeCombination(set, twoMissingCombination);
                return;
            case 3:
                PartitionCombinationResult threeMissingCombination = this.getCombinations(3, valueOfDigit, set.getPrimes());
                HandleThreeCombination(set, threeMissingCombination);
                return;
        }
    }

    private void HandleThreeCombination(ValidatingPrimeSet set, PartitionCombinationResult result) {
        if (result.hasSingleResult()) {
            for (int singleResult : result.getSingle()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.addEntry(singleResult)) {
                    this.handleFour(newSet);
                }
            }
        }
        if (result.hasCombinationResult()) {
            for (int[] combination : result.getCombination()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                boolean setIsValid = true;
                for (int prime : combination) {
                    if (!newSet.addEntry(prime)) {
                        setIsValid = false;
                        break;
                    }
                }
                if (setIsValid) {
                    this.handleFour(newSet);
                }
            }
        }
    }

    private void handleFour(ValidatingPrimeSet set) {
        int valueOfDigit = 4;
        if (!precheck(set, valueOfDigit)) return;
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing) {
            case 0:
                this.handleFive(set);
                return;
            case 1:
                PartitionCombinationResult oneMissingCombinations = this.getCombinations(1, valueOfDigit, set.getPrimes());
                HandleFourCombination(set, oneMissingCombinations);
                return;
            case 2:
                PartitionCombinationResult twoMissingCombination = this.getCombinations(2, valueOfDigit, set.getPrimes());
                HandleFourCombination(set, twoMissingCombination);
                return;
            case 3:
                PartitionCombinationResult threeMissingCombination = this.getCombinations(3, valueOfDigit, set.getPrimes());
                HandleFourCombination(set, threeMissingCombination);
                return;
            case 4:
                PartitionCombinationResult fourMissingCombination = this.getCombinations(4, valueOfDigit, set.getPrimes());
                HandleFourCombination(set, fourMissingCombination);
                return;
        }
    }

    private void HandleFourCombination(ValidatingPrimeSet set, PartitionCombinationResult result) {
        if (result.hasSingleResult()) {
            for (int singleResult : result.getSingle()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.addEntry(singleResult)) {
                    this.handleFive(newSet);
                }
            }
        }
        if (result.hasCombinationResult()) {
            for (int[] combination : result.getCombination()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                boolean setIsValid = true;
                for (int prime : combination) {
                    if (!newSet.addEntry(prime)) {
                        setIsValid = false;
                        break;
                    }
                }
                if (setIsValid) {
                    this.handleFive(newSet);
                }
            }
        }
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
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing) {
            case 0:
                this.handleTwo(set);
                return;
            case 1:
                int[] oneMissingCombinations = this.categorizer.getBucketForCharacterAndCharacterCount(1, 1);
                HandleMissingOne(set, oneMissingCombinations);
                return;
        }
    }

    private void HandleMissingOne(ValidatingPrimeSet set, int[] oneMissingCombinations) {
        for (int entry : oneMissingCombinations) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.addEntry(entry)) {
                this.handleTwo(newSet);
            }
        }
    }

    private void handleTwo(ValidatingPrimeSet set) {
        int valueOfDigit = 2;
        if (!precheck(set, valueOfDigit)) return;
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing) {
            case 0:
                this.handleThree(set);
                return;
            case 1:
                PartitionCombinationResult oneMissingCombinations = this.getCombinations(1, valueOfDigit, set.getPrimes());
                HandleTwoCombination(set, oneMissingCombinations);
                return;
            case 2:
                PartitionCombinationResult twoMissingCombination = this.getCombinations(2, valueOfDigit, set.getPrimes());
                HandleTwoCombination(set, twoMissingCombination);
                return;
        }
    }

    private boolean precheck(ValidatingPrimeSet set, int i) {
        if (Thread.interrupted()) {
            // Executor has probably asked us to stop
            System.out.println(Thread.currentThread().toString() + " has been interrupted!");
            throw new CancellationException("Thread has been requested to stop");
        }
        return set.countReached(i - 1);
    }

    private void HandleTwoCombination(ValidatingPrimeSet set, PartitionCombinationResult result) {
        if (result.hasSingleResult()) {
            for (int singleResult : result.getSingle()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.addEntry(singleResult)) {
                    this.handleThree(newSet);
                }
            }
        }
        if (result.hasCombinationResult()) {
            for (int[] combination : result.getCombination()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                boolean setIsValid = true;
                for (int prime : combination) {
                    if (!newSet.addEntry(prime)) {
                        setIsValid = false;
                        break;
                    }
                }
                if (setIsValid) {
                    this.handleThree(newSet);
                }
            }
        }
    }

    private void handleFive(ValidatingPrimeSet set) {
        int valueOfDigit = 5;
        if (!precheck(set, valueOfDigit)) return;
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing) {
            case 0:
                this.handleSix(set);
                return;
            case 1:
                PartitionCombinationResult oneMissingCombinations = this.getCombinations(1, valueOfDigit, set.getPrimes());
                HandleFiveCombination(set, oneMissingCombinations);
                return;
            case 2:
                PartitionCombinationResult twoMissingCombination = this.getCombinations(2, valueOfDigit, set.getPrimes());
                HandleFiveCombination(set, twoMissingCombination);
                return;
            case 3:
                PartitionCombinationResult threeMissingCombination = this.getCombinations(3, valueOfDigit, set.getPrimes());
                HandleFiveCombination(set, threeMissingCombination);
                return;
            case 4:
                PartitionCombinationResult fourMissingCombination = this.getCombinations(4, valueOfDigit, set.getPrimes());
                HandleFiveCombination(set, fourMissingCombination);
                return;
            case 5:
                PartitionCombinationResult fiveMissingCombination = this.getCombinations(5, valueOfDigit, set.getPrimes());
                HandleFiveCombination(set, fiveMissingCombination);
                return;
        }
    }

    private void HandleFiveCombination(ValidatingPrimeSet set, PartitionCombinationResult result) {
        if (result.hasSingleResult()) {
            for (int singleResult : result.getSingle()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.addEntry(singleResult)) {
                    this.handleSix(newSet);
                }
            }
        }
        if (result.hasCombinationResult()) {
            for (int[] combination : result.getCombination()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                boolean setIsValid = true;
                for (int prime : combination) {
                    if (!newSet.addEntry(prime)) {
                        setIsValid = false;
                        break;
                    }
                }
                if (setIsValid) {
                    this.handleSix(newSet);
                }
            }
        }
    }

    private void handleSix(ValidatingPrimeSet set) {
        int valueOfDigit = 6;
        if (!precheck(set, valueOfDigit)) return;
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing) {
            case 0:
                this.handleSeven(set);
                return;
            case 1:
                PartitionCombinationResult oneMissing = this.getCombinations(1, valueOfDigit, set.getPrimes());
                HandleSixCombination(set, oneMissing);
                return;
            case 2:
                PartitionCombinationResult twoMissing = this.getCombinations(2, valueOfDigit, set.getPrimes());
                HandleSixCombination(set, twoMissing);
                return;
            case 3:
                PartitionCombinationResult threeMissing = this.getCombinations(3, valueOfDigit, set.getPrimes());
                HandleSixCombination(set, threeMissing);
                return;
            case 4:
                PartitionCombinationResult fourMissing = this.getCombinations(4, valueOfDigit, set.getPrimes());
                HandleSixCombination(set, fourMissing);
                return;
            case 5:
                PartitionCombinationResult fiveMissing = this.getCombinations(5, valueOfDigit, set.getPrimes());
                HandleSixCombination(set, fiveMissing);
                return;
            case 6:
                PartitionCombinationResult sixMissing = this.getCombinations(6, valueOfDigit, set.getPrimes());
                HandleSixCombination(set, sixMissing);
                return;
        }
    }

    private void HandleSixCombination(ValidatingPrimeSet set, PartitionCombinationResult result) {
        if (result.hasSingleResult()) {
            for (int singleResult : result.getSingle()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.addEntry(singleResult)) {
                    this.handleSeven(newSet);
                }
            }
        }
        if (result.hasCombinationResult()) {
            for (int[] combination : result.getCombination()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);

                boolean setIsValid = true;
                for (int prime : combination) {
                    if (!newSet.addEntry(prime)) {
                        setIsValid = false;
                        break;
                    }
                }

                if (setIsValid) {
                    this.handleSeven(newSet);
                }
            }
        }
    }

    private void handleSeven(ValidatingPrimeSet set) {
        int valueOfDigit = 7;
        if (!precheck(set, valueOfDigit)) return;
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing) {
            case 0:
                this.handleEight(set);
                return;
            case 1:
                PartitionCombinationResult oneMissing = this.getCombinations(1, valueOfDigit, set.getPrimes());
                HandleSevenCombination(set, oneMissing);
                return;
            case 2:
                PartitionCombinationResult twoMissing = this.getCombinations(2, valueOfDigit, set.getPrimes());
                HandleSevenCombination(set, twoMissing);
                return;
            case 3:
                PartitionCombinationResult threeMissing = this.getCombinations(3, valueOfDigit, set.getPrimes());
                HandleSevenCombination(set, threeMissing);
                return;
            case 4:
                PartitionCombinationResult fourMissing = this.getCombinations(4, valueOfDigit, set.getPrimes());
                HandleSevenCombination(set, fourMissing);
                return;
            case 5:
                PartitionCombinationResult fiveMissing = this.getCombinations(5, valueOfDigit, set.getPrimes());
                HandleSevenCombination(set, fiveMissing);
                return;
            case 6:
                PartitionCombinationResult sixMissing = this.getCombinations(6, valueOfDigit, set.getPrimes());
                HandleSevenCombination(set, sixMissing);
                return;
            case 7:
                PartitionCombinationResult sevenMissing = this.getCombinations(7, valueOfDigit, set.getPrimes());
                HandleSevenCombination(set, sevenMissing);
                return;
        }
    }

    private void HandleSevenCombination(ValidatingPrimeSet set, PartitionCombinationResult result) {
        if (result.hasSingleResult()) {
            for (int singleResult : result.getSingle()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.addEntry(singleResult)) {
                    this.handleEight(newSet);
                }
            }
        }
        if (result.hasCombinationResult()) {
            for (int[] combination : result.getCombination()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                boolean setIsValid = true;
                for (int prime : combination) {
                    if (!newSet.addEntry(prime)) {
                        setIsValid = false;
                        break;
                    }
                }
                if (setIsValid) {
                    this.handleEight(newSet);
                }
            }
        }
    }

    private void handleEight(ValidatingPrimeSet set) {
        int valueOfDigit = 8;
        if (!precheck(set, valueOfDigit)) return;
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing) {
            case 0:
                this.handleNine(set);
                return;
            case 1:
                PartitionCombinationResult oneMissing = this.getCombinations(1, valueOfDigit, set.getPrimes());
                HandleEightCombination(set, oneMissing);
                return;
            case 2:
                PartitionCombinationResult twoMissing = this.getCombinations(2, valueOfDigit, set.getPrimes());
                HandleEightCombination(set, twoMissing);
                return;
            case 3:
                PartitionCombinationResult threeMissing = this.getCombinations(3, valueOfDigit, set.getPrimes());
                HandleEightCombination(set, threeMissing);
                return;
            case 4:
                PartitionCombinationResult fourMissing = this.getCombinations(4, valueOfDigit, set.getPrimes());
                HandleEightCombination(set, fourMissing);
                return;
            case 5:
                PartitionCombinationResult fiveMissing = this.getCombinations(5, valueOfDigit, set.getPrimes());
                HandleEightCombination(set, fiveMissing);
                return;
            case 6:
                PartitionCombinationResult sixMissing = this.getCombinations(6, valueOfDigit, set.getPrimes());
                HandleEightCombination(set, sixMissing);
                return;
            case 7:
                PartitionCombinationResult sevenMissing = this.getCombinations(7, valueOfDigit, set.getPrimes());
                HandleEightCombination(set, sevenMissing);
                return;
            case 8:
                PartitionCombinationResult eightMissing = this.getCombinations(8, valueOfDigit, set.getPrimes());
                HandleEightCombination(set, eightMissing);
                return;
        }
    }

    private void HandleEightCombination(ValidatingPrimeSet set, PartitionCombinationResult result) {
        if (result.hasSingleResult()) {
            for (int singleResult : result.getSingle()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.addEntry(singleResult)) {
                    this.handleNine(newSet);
                }
            }
        }
        if (result.hasCombinationResult()) {
            for (int[] combination : result.getCombination()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                boolean setIsValid = true;
                for (int prime : combination) {
                    if (!newSet.addEntry(prime)) {
                        setIsValid = false;
                        break;
                    }
                }
                if (setIsValid) {
                    this.handleNine(newSet);
                }
            }
        }
    }

    private void handleNine(ValidatingPrimeSet set) {
        int valueOfDigit = 9;
        if (!precheck(set, valueOfDigit)) return;
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing) {
            case 0:
                this.handlePossibleResult(set);
                return;
            case 1:
                PartitionCombinationResult oneMissing = this.getCombinations(1, valueOfDigit, set.getPrimes());
                HandleNineCombination(set, oneMissing);
                return;
            case 2:
                PartitionCombinationResult twoMissing = this.getCombinations(2, valueOfDigit, set.getPrimes());
                HandleNineCombination(set, twoMissing);
                return;
            case 3:
                PartitionCombinationResult threeMissing = this.getCombinations(3, valueOfDigit, set.getPrimes());
                HandleNineCombination(set, threeMissing);
                return;
            case 4:
                PartitionCombinationResult fourMissing = this.getCombinations(4, valueOfDigit, set.getPrimes());
                HandleNineCombination(set, fourMissing);
                return;
            case 5:
                PartitionCombinationResult fiveMissing = this.getCombinations(5, valueOfDigit, set.getPrimes());
                HandleNineCombination(set, fiveMissing);
                return;
            case 6:
                PartitionCombinationResult sixMissing = this.getCombinations(6, valueOfDigit, set.getPrimes());
                HandleNineCombination(set, sixMissing);
                return;
            case 7:
                PartitionCombinationResult sevenMissing = this.getCombinations(7, valueOfDigit, set.getPrimes());
                HandleNineCombination(set, sevenMissing);
                return;
            case 8:
                PartitionCombinationResult eightMissing = this.getCombinations(8, valueOfDigit, set.getPrimes());
                HandleNineCombination(set, eightMissing);
                return;
            case 9:
                PartitionCombinationResult nineMissing = this.getCombinations(9, valueOfDigit, set.getPrimes());
                HandleNineCombination(set, nineMissing);
                return;
        }
    }

    private void HandleNineCombination(ValidatingPrimeSet set, PartitionCombinationResult result) {
        if (result.hasSingleResult()) {
            for (int singleResult : result.getSingle()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.addEntry(singleResult)) {
                    this.handlePossibleResult(newSet);
                }
            }
        }
        if (result.hasCombinationResult()) {
            for (int[] combination : result.getCombination()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                boolean setIsValid = true;
                for (int prime : combination) {
                    if (!newSet.addEntry(prime)) {
                        setIsValid = false;
                        break;
                    }
                }
                if (setIsValid) {
                    this.handlePossibleResult(newSet);
                }
            }
        }
    }

    private void handlePossibleResult(ValidatingPrimeSet set) {
        boolean isValid = Validator.validateFinalSet(set);
        if (isValid) {
            calculateResultAndPrintSet(set);
        }
    }

    /**
     * Calculates the sum of a set and prints the information (dependend on the current configuration).
     *
     * @param set The set of primes.
     */
    private void calculateResultAndPrintSet(ValidatingPrimeSet set) {
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

    public <T> Set<T> findDuplicates(Collection<T> list) {

        Set<T> duplicates = new LinkedHashSet<T>();
        Set<T> uniques = new HashSet<T>();

        for (T t : list) {
            if (!uniques.add(t)) {
                duplicates.add(t);
            }
        }

        return duplicates;
    }

    private PartitionCombinationResult getCombinations(int countOfMissing, int value, int[] filterPrimes) {
        ArrayList<ArrayList<Integer>> partitions = Partition.partition(countOfMissing);
        return this.getCombinationsForPartionsAndValue(partitions, value, filterPrimes);
    }


    private PartitionCombinationResult getCombinationsForPartionsAndValue(ArrayList<ArrayList<Integer>> partitions, int value, int[] alreadyUsed) {

        // Combine the possible candidates with the partitions.
        Map<Integer, int[]> values = new HashMap<>();
        for (List<Integer> partition : partitions) {
            for (Integer numberOfOccurence : partition) {
                if (values.containsKey(numberOfOccurence)) continue;
                values.put(numberOfOccurence, categorizer.getBucketForCharacterAndCharacterCount(value, numberOfOccurence));
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
