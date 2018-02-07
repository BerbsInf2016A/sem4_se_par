package implementation;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static implementation.Combinations.merge;


public class ConcurrentPrimeCombinationFinder {
    private final PrimeCategorizer categorizer;

    public ConcurrentPrimeCombinationFinder(PrimeCategorizer categorizer) {
        this.categorizer = categorizer;
    }

    private void printDebugDuplicates (List<ValidatingPrimeSet> sets) {
        List<String> strings = new ArrayList<>();
        for (ValidatingPrimeSet set: sets) {
            List<Integer> list = Arrays.stream(set.getPrimes()).boxed().collect(Collectors.toList());
            Collections.sort(list);
            strings.add(list.stream().map(t -> t.toString()).collect(Collectors.joining(", ")));
        }

        System.out.println("Stringsize: " + strings.size() + " distinct count: " + strings.stream().distinct().count());
        Set<ValidatingPrimeSet> duplicates = findDuplicates(sets);
        System.out.println("duplicates: " + duplicates.size());

        List<String> firstEncounteredStrings = new ArrayList();
        for (String t : strings ) {
            if (! firstEncounteredStrings.contains(t)) {
                firstEncounteredStrings.add(t);
            } else {
                System.out.println(t);
            }
        }

    }

    public void run(List<ValidatingPrimeSet> sets) {
        // TODO Just get a chance to connect with visual vm
        try {
            Thread.sleep(10000);
            this.runConcurrent(sets);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }


    private void handleThree(ValidatingPrimeSet set){
        int valueOfDigit = 3;
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing){
            case 0:
                this.handleFour(set);
                return;
            case 1:
                PartitionCombinationResult oneMissingCombinations =  this.getCombinations(1, valueOfDigit,set.getPrimes());
                HandleThreeCombination(set, oneMissingCombinations);
                return;
            case 2:
                PartitionCombinationResult twoMissingCombination =  this.getCombinations(2, valueOfDigit,set.getPrimes());
                HandleThreeCombination(set, twoMissingCombination);
                return;
            case 3:
                PartitionCombinationResult threeMissingCombination =  this.getCombinations(3, valueOfDigit,set.getPrimes());
                HandleThreeCombination(set, threeMissingCombination);
                return;
        }
        }

    private void HandleThreeCombination(ValidatingPrimeSet set, PartitionCombinationResult result) {
        if (result.hasSingleResult()) {
            for (int singleResult : result.getSingle() ) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.addEntry(singleResult)) { this.handleFour(newSet); }
            }
        }
        if (result.hasCombinationResult()) {
            for (int [] combination : result.getCombination() ) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                boolean setIsValid = true;
                for (int prime : combination) {
                    if ( !newSet.addEntry(prime)) {
                        setIsValid = false;
                        break;
                    }
                }
                if(setIsValid) { this.handleFour(newSet); }
            }
        }
    }

    private void handleFour(ValidatingPrimeSet set) {
        int valueOfDigit = 4;
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing){
            case 0:
                this.handleFive(set);
                return;
            case 1:
                PartitionCombinationResult oneMissingCombinations =  this.getCombinations(1, valueOfDigit,set.getPrimes());
                HandleFourCombination(set, oneMissingCombinations);
                return;
            case 2:
                PartitionCombinationResult twoMissingCombination =  this.getCombinations(2, valueOfDigit,set.getPrimes());
                HandleFourCombination(set, twoMissingCombination);
                return;
            case 3:
                PartitionCombinationResult threeMissingCombination =  this.getCombinations(3, valueOfDigit,set.getPrimes());
                HandleFourCombination(set, threeMissingCombination);
                return;
            case 4:
                PartitionCombinationResult fourMissingCombination =  this.getCombinations(4, valueOfDigit,set.getPrimes());
                HandleFourCombination(set, fourMissingCombination);
                return;
        }
        }

    private void HandleFourCombination(ValidatingPrimeSet set, PartitionCombinationResult result) {
        if (result.hasSingleResult()) {
            for (int singleResult : result.getSingle() ) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.addEntry(singleResult)) { this.handleFive(newSet); }
            }
        }
        if (result.hasCombinationResult()) {
            for (int [] combination : result.getCombination() ) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                boolean setIsValid = true;
                for (int prime : combination) {
                    if ( !newSet.addEntry(prime)) {
                        setIsValid = false;
                        break;
                    }
                }
                if(setIsValid) { this.handleFive(newSet); }
            }
        }
    }


    public void runConcurrent(List<ValidatingPrimeSet> sets){
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

            final List<Future<Boolean>> resultFromParts = executorPool.invokeAll(partitions, 10, TimeUnit.SECONDS);
            executorPool.shutdown();


        } catch (Exception e) {
            System.out.println("Concurrent run aborted.");
            throw new RuntimeException(e);
        }
    }


    private boolean generateSets(List<ValidatingPrimeSet> sets) {
        for (ValidatingPrimeSet entry : sets) {
            ValidatingPrimeSet set = new ValidatingPrimeSet(entry);
                this.handleOne(set);
        }
        return true;
    }

    private void handleOne(ValidatingPrimeSet set) {
        int valueOfDigit = 1;
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing){
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
        for (int entry : oneMissingCombinations ) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.addEntry(entry)) {
                this.handleTwo(newSet);
            }
        }
    }

    private void handleTwo(ValidatingPrimeSet set) {
        int valueOfDigit = 2;
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing){
            case 0:
                this.handleThree(set);
                return;
            case 1:
                PartitionCombinationResult oneMissingCombinations =  this.getCombinations(1, valueOfDigit,set.getPrimes());
                HandleTwoCombination(set, oneMissingCombinations);
                return;
            case 2:
                PartitionCombinationResult twoMissingCombination =  this.getCombinations(2, valueOfDigit,set.getPrimes());
                HandleTwoCombination(set, twoMissingCombination);
                return;
        }
    }

    private void HandleTwoCombination(ValidatingPrimeSet set, PartitionCombinationResult result) {
        if (result.hasSingleResult()) {
            for (int singleResult : result.getSingle() ) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.addEntry(singleResult)) { this.handleThree(newSet); }
            }
        }
        if (result.hasCombinationResult()) {
            for (int [] combination : result.getCombination() ) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                boolean setIsValid = true;
                for (int prime : combination) {
                    if ( !newSet.addEntry(prime)) {
                        setIsValid = false;
                        break;
                    }
                }
                if(setIsValid) { this.handleThree(newSet); }
            }
        }
    }


    private void handleFive(ValidatingPrimeSet set) {
        int valueOfDigit = 5;
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing){
            case 0:
                this.handleSix(set);
                break;
            case 1:
                PartitionCombinationResult oneMissingCombinations =  this.getCombinations(1, valueOfDigit,set.getPrimes());
                HandleFiveCombination(set, oneMissingCombinations);
                break;
            case 2:
                PartitionCombinationResult twoMissingCombination =  this.getCombinations(2, valueOfDigit,set.getPrimes());
                HandleFiveCombination(set, twoMissingCombination);
                break;
            case 3:
                PartitionCombinationResult threeMissingCombination =  this.getCombinations(3, valueOfDigit,set.getPrimes());
                HandleFiveCombination(set, threeMissingCombination);
                break;
            case 4:
                PartitionCombinationResult fourMissingCombination =  this.getCombinations(4, valueOfDigit,set.getPrimes());
                HandleFiveCombination(set, fourMissingCombination);
                break;
            case 5:
                PartitionCombinationResult fiveMissingCombination =  this.getCombinations(5, valueOfDigit,set.getPrimes());
                HandleFiveCombination(set, fiveMissingCombination);
                break;
        }
    }

    private void HandleFiveCombination(ValidatingPrimeSet set, PartitionCombinationResult result) {
        if (result.hasSingleResult()) {
            for (int singleResult : result.getSingle() ) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.addEntry(singleResult)) { this.handleSix(newSet); }
            }
        }
        if (result.hasCombinationResult()) {
            for (int [] combination : result.getCombination() ) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                boolean setIsValid = true;
                for (int prime : combination) {
                    if ( !newSet.addEntry(prime)) {
                        setIsValid = false;
                        break;
                    }
                }
                if(setIsValid) { this.handleSix(newSet); }
            }
        }
    }

    private void handleSix(ValidatingPrimeSet set) {
        int valueOfDigit = 6;
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing){
            case 0:
                this.handleSeven(set);
                break;
            case 1:
                PartitionCombinationResult oneMissing =  this.getCombinations(1, valueOfDigit,set.getPrimes());
                HandleSixCombination(set, oneMissing);
                break;
            case 2:
                PartitionCombinationResult twoMissing =  this.getCombinations(2, valueOfDigit,set.getPrimes());
                HandleSixCombination(set, twoMissing);
                break;
            case 3:
                PartitionCombinationResult threeMissing =  this.getCombinations(3, valueOfDigit,set.getPrimes());
                HandleSixCombination(set, threeMissing);
                break;
            case 4:
                PartitionCombinationResult fourMissing =  this.getCombinations(4, valueOfDigit,set.getPrimes());
                HandleSixCombination(set, fourMissing);
                break;
            case 5:
                PartitionCombinationResult fiveMissing =  this.getCombinations(5, valueOfDigit,set.getPrimes());
                HandleSixCombination(set, fiveMissing);
                break;
            case 6:
                PartitionCombinationResult sixMissing =  this.getCombinations(6, valueOfDigit,set.getPrimes());
                HandleSixCombination(set, sixMissing);
                break;
        }
    }

    private void HandleSixCombination(ValidatingPrimeSet set, PartitionCombinationResult result) {
        if (result.hasSingleResult()) {
            for (int singleResult : result.getSingle() ) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.addEntry(singleResult)) { this.handleSeven(newSet); }
            }
        }
        if (result.hasCombinationResult()) {
            for (int [] combination : result.getCombination() ) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);

                boolean setIsValid = true;
                for (int prime : combination) {
                    if ( !newSet.addEntry(prime)) {
                        setIsValid = false;
                        break;
                    }
                }

                if(setIsValid) { this.handleSeven(newSet); }
            }
        }
    }

    private void handleSeven(ValidatingPrimeSet set) {
        int valueOfDigit = 7;
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing){
            case 0:
                this.handleEight(set);
                break;
            case 1:
                PartitionCombinationResult oneMissing =  this.getCombinations(1, valueOfDigit,set.getPrimes());
                HandleSevenCombination(set, oneMissing);
                break;
            case 2:
                PartitionCombinationResult twoMissing =  this.getCombinations(2, valueOfDigit,set.getPrimes());
                HandleSevenCombination(set, twoMissing);
                break;
            case 3:
                PartitionCombinationResult threeMissing =  this.getCombinations(3, valueOfDigit,set.getPrimes());
                HandleSevenCombination(set, threeMissing);
                break;
            case 4:
                PartitionCombinationResult fourMissing =  this.getCombinations(4, valueOfDigit,set.getPrimes());
                HandleSevenCombination(set, fourMissing);
                break;
            case 5:
                PartitionCombinationResult fiveMissing =  this.getCombinations(5, valueOfDigit,set.getPrimes());
                HandleSevenCombination(set, fiveMissing);
                break;
            case 6:
                PartitionCombinationResult sixMissing =  this.getCombinations(6, valueOfDigit,set.getPrimes());
                HandleSevenCombination(set, sixMissing);
                break;
            case 7:
                PartitionCombinationResult sevenMissing =  this.getCombinations(7, valueOfDigit,set.getPrimes());
                HandleSevenCombination(set, sevenMissing);
                break;
        }
    }

    private void HandleSevenCombination(ValidatingPrimeSet set, PartitionCombinationResult result) {
        if (result.hasSingleResult()) {
            for (int singleResult : result.getSingle() ) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.addEntry(singleResult)) { this.handleEight(newSet); }
            }
        }
        if (result.hasCombinationResult()) {
            for (int [] combination : result.getCombination() ) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                boolean setIsValid = true;
                for (int prime : combination) {
                    if ( !newSet.addEntry(prime)) {
                        setIsValid = false;
                        break;
                    }
                }
                if(setIsValid) { this.handleEight(newSet); }
            }
        }
    }

    private void handleEight(ValidatingPrimeSet set) {
        int valueOfDigit = 8;
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing){
            case 0:
                this.handleNine(set);
                break;
            case 1:
                PartitionCombinationResult oneMissing =  this.getCombinations(1, valueOfDigit,set.getPrimes());
                HandleEightCombination(set, oneMissing);
                break;
            case 2:
                PartitionCombinationResult twoMissing =  this.getCombinations(2, valueOfDigit,set.getPrimes());
                HandleEightCombination(set, twoMissing);
                break;
            case 3:
                PartitionCombinationResult threeMissing =  this.getCombinations(3, valueOfDigit,set.getPrimes());
                HandleEightCombination(set, threeMissing);
                break;
            case 4:
                PartitionCombinationResult fourMissing =  this.getCombinations(4, valueOfDigit,set.getPrimes());
                HandleEightCombination(set, fourMissing);
                break;
            case 5:
                PartitionCombinationResult fiveMissing =  this.getCombinations(5, valueOfDigit,set.getPrimes());
                HandleEightCombination(set, fiveMissing);
                break;
            case 6:
                PartitionCombinationResult sixMissing =  this.getCombinations(6, valueOfDigit,set.getPrimes());
                HandleEightCombination(set, sixMissing);
                break;
            case 7:
                PartitionCombinationResult sevenMissing =  this.getCombinations(7, valueOfDigit,set.getPrimes());
                HandleEightCombination(set, sevenMissing);
                break;
            case 8:
                PartitionCombinationResult eightMissing =  this.getCombinations(8, valueOfDigit,set.getPrimes());
                HandleEightCombination(set, eightMissing);
                break;
        }
    }

    private void HandleEightCombination(ValidatingPrimeSet set, PartitionCombinationResult result) {
        if (result.hasSingleResult()) {
            for (int singleResult : result.getSingle() ) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.addEntry(singleResult)) { this.handleNine(newSet); }
            }
        }
        if (result.hasCombinationResult()) {
            for (int [] combination : result.getCombination() ) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                boolean setIsValid = true;
                for (int prime : combination) {
                    if ( !newSet.addEntry(prime)) {
                        setIsValid = false;
                        break;
                    }
                }
                if(setIsValid) { this.handleNine(newSet); }
            }
        }
    }

    private void handleNine(ValidatingPrimeSet set) {
        int valueOfDigit = 9;
        int missing = set.countOfMissingDigit(valueOfDigit);
        switch (missing){
            case 0:
                this.handlePossibleResult(set);
                break;
            case 1:
                PartitionCombinationResult oneMissing =  this.getCombinations(1, valueOfDigit,set.getPrimes());
                HandleNineCombination(set, oneMissing);
                break;
            case 2:
                PartitionCombinationResult twoMissing =  this.getCombinations(2, valueOfDigit,set.getPrimes());
                HandleNineCombination(set, twoMissing);
                break;
            case 3:
                PartitionCombinationResult threeMissing =  this.getCombinations(3, valueOfDigit,set.getPrimes());
                HandleNineCombination(set, threeMissing);
                break;
            case 4:
                PartitionCombinationResult fourMissing =  this.getCombinations(4, valueOfDigit,set.getPrimes());
                HandleNineCombination(set, fourMissing);
                break;
            case 5:
                PartitionCombinationResult fiveMissing =  this.getCombinations(5, valueOfDigit,set.getPrimes());
                HandleNineCombination(set, fiveMissing);
                break;
            case 6:
                PartitionCombinationResult sixMissing =  this.getCombinations(6, valueOfDigit,set.getPrimes());
                HandleNineCombination(set, sixMissing);
                break;
            case 7:
                PartitionCombinationResult sevenMissing =  this.getCombinations(7, valueOfDigit,set.getPrimes());
                HandleNineCombination(set, sevenMissing);
                break;
            case 8:
                PartitionCombinationResult eightMissing =  this.getCombinations(8, valueOfDigit,set.getPrimes());
                HandleNineCombination(set, eightMissing);
                break;
            case 9:
                PartitionCombinationResult nineMissing =  this.getCombinations(9, valueOfDigit,set.getPrimes());
                HandleNineCombination(set, nineMissing);
                break;
        }
    }

    private void HandleNineCombination(ValidatingPrimeSet set, PartitionCombinationResult result) {
        if (result.hasSingleResult()) {
            for (int singleResult : result.getSingle() ) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.addEntry(singleResult)) { this.handlePossibleResult(newSet); }
            }
        }
        if (result.hasCombinationResult()) {
            for (int [] combination : result.getCombination() ) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                boolean setIsValid = true;
                for (int prime : combination) {
                    if ( !newSet.addEntry(prime)) {
                        setIsValid = false;
                        break;
                    }
                }
                if(setIsValid) { this.handlePossibleResult(newSet); }
            }
        }
    }

    private void handlePossibleResult(ValidatingPrimeSet set) {
        boolean isValid = Validator.validateFinalSet(set);
        if (isValid) {
            calculateResultAndPrintSet(set);
        }
    }

    private void calculateResultAndPrintSet(ValidatingPrimeSet set) {
        int[] primes = Arrays.stream(set.getPrimes()).filter(t -> t != 0).toArray();
        primes = Arrays.stream(primes).sorted().toArray();
        int sum = Arrays.stream(primes).sum();
        System.out.println("Found: Sum: " + sum + " " + Arrays.toString(primes));
    }

    private void printDebugSetData(ValidatingPrimeSet set) {
        globalDebugCounter.incrementAndGet();
        List<Integer> list = Arrays.stream(set.getPrimes()).boxed().collect(Collectors.toList());
        System.out.println(list.stream().map(t -> t.toString()).collect(Collectors.joining(", ")));
    }

    public static AtomicInteger globalDebugCounter = new AtomicInteger();
    public static LinkedBlockingQueue<String> strings = new LinkedBlockingQueue<>();

    public <T> Set<T> findDuplicates(Collection<T> list) {

        Set<T> duplicates = new LinkedHashSet<T>();
        Set<T> uniques = new HashSet<T>();

        for(T t : list) {
            if(!uniques.add(t)) {
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
            if (validEntries.size() > 0 ){
                filteredValues.put(entry.getKey(), toIntArray(validEntries));
            }
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

    int[] toIntArray(List<Integer> list){
        int[] ret = new int[list.size()];
        for(int i = 0;i < ret.length;i++)
            ret[i] = list.get(i);
        return ret;
    }
}
