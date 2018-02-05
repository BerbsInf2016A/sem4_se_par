package implementation;

import com.sun.deploy.util.ArrayUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public void run() {
        List<ValidatingPrimeSet> sets = this.runForOne();
        System.out.println(sets.size() + " sets after generating 1");
        this.printDebugDuplicates(sets);
        sets = this.runForTwo(sets);
        System.out.println(sets.size() + " sets after generating 2");
        this.printDebugDuplicates(sets);
/*
        int sliceSize = sets.size() / Configuration.instance.maximumNumberOfThreads;
        List<IntegerPartitionSizes> partitions = new ArrayList<>();
        for (int i = 0; i <= sets.size(); i += sliceSize) {
            final int from = i;
            int to = i + sliceSize;
            if (to > sets.size())
                to = sets.size();
            final int end = to;
            partitions.add(new IntegerPartitionSizes(from, end));
        }
        List<ValidatingPrimeSet> newSets = new ArrayList<>();
        for (IntegerPartitionSizes partition : partitions) {
            newSets.addAll(this.runForThree(sets.subList(partition.from, partition.to)));
            int g = 0;
        }

*/
        HashSet<ValidatingPrimeSet> replaceLaterSet = this.runForThree(sets);
        System.out.println(replaceLaterSet.size() + " sets after generating 3");
        this.printDebugDuplicates(new ArrayList<>(replaceLaterSet));
        
       // System.out.println(newSets.size() + " new sets after generating 3");
        replaceLaterSet = this.runForFour(new ArrayList<>(replaceLaterSet));
        System.out.println(replaceLaterSet.size() + " sets after generating 4");
        int g = 0;
    }
    private HashSet<ValidatingPrimeSet> runForThree(List<ValidatingPrimeSet> sourceSets) {
        HashSet<ValidatingPrimeSet> foundSets = new HashSet<>();
        try {

            final List<Callable<HashSet<ValidatingPrimeSet>>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);

            int sliceSize = sourceSets.size() / Configuration.instance.maximumNumberOfThreads;
            System.out.format("Using %d threads. SliceSize: %d \n", Configuration.instance.maximumNumberOfThreads, sliceSize);


            for (int i = 0; i <= sourceSets.size(); i += sliceSize) {
                final int from = i;
                int to = i + sliceSize;
                if (to > sourceSets.size())
                    to = sourceSets.size();
                final int end = to;
                ConcurrentLinkedQueue<ValidatingPrimeSet> sublist = new ConcurrentLinkedQueue<>();
                sublist.addAll(sourceSets.subList(from, end));
                partitions.add(() -> generateSetsForThree(sublist));;
            }

            final List<Future<HashSet<ValidatingPrimeSet>>> resultFromParts = executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);
            executorPool.shutdown();


            for (final Future<HashSet<ValidatingPrimeSet>> result : resultFromParts) {
                HashSet<ValidatingPrimeSet> sets = result.get();
                foundSets.addAll(sets);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return foundSets;
    }

    private HashSet<ValidatingPrimeSet> runForFour(List<ValidatingPrimeSet> sourceSets) {
        HashSet<ValidatingPrimeSet> foundSets = new HashSet<>();
        try {

            final List<Callable<HashSet<ValidatingPrimeSet>>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);

            int sliceSize = sourceSets.size() / Configuration.instance.maximumNumberOfThreads;
            System.out.format("Using %d threads. SliceSize: %d \n", Configuration.instance.maximumNumberOfThreads, sliceSize);


            for (int i = 0; i <= sourceSets.size(); i += sliceSize) {
                final int from = i;
                int to = i + sliceSize;
                if (to > sourceSets.size())
                    to = sourceSets.size();
                final int end = to;
                ConcurrentLinkedQueue<ValidatingPrimeSet> sublist = new ConcurrentLinkedQueue<>();
                sublist.addAll(sourceSets.subList(from, end));
                partitions.add(() -> generateSetsForFour(sublist));
            }

            final List<Future<HashSet<ValidatingPrimeSet>>> resultFromParts = executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);
            executorPool.shutdown();


            for (final Future<HashSet<ValidatingPrimeSet>> result : resultFromParts) {
                HashSet<ValidatingPrimeSet> sets = result.get();
                foundSets.addAll(sets);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return foundSets;
    }

    private HashSet<ValidatingPrimeSet> generateSetsForThree(ConcurrentLinkedQueue<ValidatingPrimeSet> sublist) {
        HashSet<ValidatingPrimeSet> validSets = new HashSet<>();

        int[] oneThreeList = this.categorizer.getBucketForCharacterAndCharacterCount(3,1);
        int[] twoThreeList = this.categorizer.getBucketForCharacterAndCharacterCount(3,2);
        List<int[]> oneThreeCombinations = Combinations.combination(oneThreeList,2);
        int[] singleThrees = this.categorizer.getBucketForCharacterAndCharacterCount(3,1);
        int[] doubleThrees = this.categorizer.getBucketForCharacterAndCharacterCount(3,2);
        // TODO Check the merge!
        int[] combined = merge(singleThrees, doubleThrees);
        List<int[]> singleThreeDoubleThreeCombination = Combinations.combination(combined,3);
        List<int[]> threeSingleThreeCombinations = Combinations.combination(singleThrees,3);

        while (!sublist.isEmpty()) {
            ValidatingPrimeSet set = sublist.poll();
            if (set.countReached(3)) {
                validSets.add(set);
                continue;
            }

            int missingThrees = set.countOfMissingDigit(3);
            switch (missingThrees) {
                case 1:
                    for (int three : oneThreeList ) {
                        ValidatingPrimeSet oneThreeSet = new ValidatingPrimeSet(set);
                        if (oneThreeSet.addEntry(three)){
                            validSets.add(oneThreeSet);
                        }
                    }
                    break;
                case 2:
                    for (int three : twoThreeList ) {
                        ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                        if (newSet.addEntry(three)){
                            validSets.add(newSet);
                        }
                    }
                    handleCombinations(validSets, oneThreeCombinations, set);
                    break;
                case 3:
                    this.handle3Missing3s(validSets, set, singleThreeDoubleThreeCombination, threeSingleThreeCombinations);
                    break;

            }
        }
        return validSets;
    }

    private HashSet<ValidatingPrimeSet> generateSetsForFour(ConcurrentLinkedQueue<ValidatingPrimeSet> sublist) {
        HashSet<ValidatingPrimeSet> validSets = new HashSet<>();

        int[] singleDigitFours = this.categorizer.getBucketForCharacterAndCharacterCount(4, 1);
        List<int[]> singleDigitFoursCombinations = Combinations.combination(singleDigitFours, 2);
        int[] doubleFours = this.categorizer.getBucketForCharacterAndCharacterCount(4,2);
        int[] tripleFour = this.categorizer.getBucketForCharacterAndCharacterCount(4, 3);


        int[] singleFoursList = this.categorizer.getBucketForCharacterAndCharacterCount(4,1);
        int[] combined = merge(doubleFours, singleFoursList);
        List<int[]> oneSingleDigitFourAndADoubleDigitFourCombinations = Combinations.combination(combined,2);

        int[] quadrupleFours = this.categorizer.getBucketForCharacterAndCharacterCount(4,4);
        List<int[]> doubleFoursCombinations = Combinations.combination(doubleFours, 2);
        List<int[]> singleFourCombinations = Combinations.combination(singleDigitFours,4);

        // One prime with three 4s and one prime with one 4.
        int[] tripleAndSingleCombined = merge(tripleFour, singleDigitFours);
        List<int[]> tripleAndSingleCombination = Combinations.combination(tripleAndSingleCombined,2);

        while (!sublist.isEmpty()) {
            ValidatingPrimeSet set = sublist.poll();
            if (set.countReached(4)){
                validSets.add(set);
                continue;
            }
            int missingFours = set.countOfMissingDigit(4);
            switch (missingFours){
                case 1:
                    this.handleOneMissingFour(validSets, set,singleDigitFours);
                    break;
                case 2:
                    this.handleTwoMissingFours(validSets, set, doubleFours, singleDigitFoursCombinations);
                    break;
                case 3:
                    this.handleThreeMissingFours(validSets, set, tripleFour, oneSingleDigitFourAndADoubleDigitFourCombinations);
                    break;
                case 4:
                    this.handleFourMissingFours(validSets, set, quadrupleFours, doubleFoursCombinations, singleFourCombinations, tripleAndSingleCombination);
                    break;
            }
            }
        return validSets;
    }

    private List<ValidatingPrimeSet> runForTwo(List<ValidatingPrimeSet> sourceSets) {
        List<ValidatingPrimeSet> foundSets = new ArrayList<>();
        try {

            final List<Callable<List<ValidatingPrimeSet>>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);

            int sliceSize = sourceSets.size() / Configuration.instance.maximumNumberOfThreads;
            System.out.format("Using %d threads. SliceSize: %d \n", Configuration.instance.maximumNumberOfThreads, sliceSize);


            for (int i = 0; i <= sourceSets.size(); i += sliceSize) {
                final int from = i;
                int to = i + sliceSize;
                if (to > sourceSets.size())
                    to = sourceSets.size();
                final int end = to;
                List<ValidatingPrimeSet> sublist = sourceSets.subList(from, end);
                partitions.add(() -> generateSetsForTwo(sublist));
            }

            final List<Future<List<ValidatingPrimeSet>>> resultFromParts = executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);
            executorPool.shutdown();


            for (final Future<List<ValidatingPrimeSet>> result : resultFromParts) {
                List<ValidatingPrimeSet> sets = result.get();
                foundSets.addAll(sets);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return foundSets;
    }

    private List<ValidatingPrimeSet> generateSetsForTwo(List<ValidatingPrimeSet> sublist) {
        List<ValidatingPrimeSet> validSets = new ArrayList<>();
        for (ValidatingPrimeSet set : sublist ) {
            validSets.addAll(handleTwo(set));
        }
        return validSets;
    }

    public List<ValidatingPrimeSet> runForOne(){
        List<ValidatingPrimeSet> foundSets = new ArrayList<>();
        try {
            // The ones are the starting point.
            int[] ones = this.categorizer.getBucketForCharacterAndCharacterCount(1,1);
            List<Integer> onesList = Arrays.stream(ones).boxed().collect(Collectors.toList());
            final List<Callable<List<ValidatingPrimeSet>>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);

            int sliceSize = onesList.size() / Configuration.instance.maximumNumberOfThreads;
            System.out.format("Using %d threads. SliceSize: %d \n", Configuration.instance.maximumNumberOfThreads, sliceSize);


            for (int i = 0; i <= onesList.size(); i += sliceSize) {
                final int from = i;
                int to = i + sliceSize;
                if (to > onesList.size())
                    to = onesList.size();
                final int end = to;
                List<Integer> sublist = onesList.subList(from, end);
                partitions.add(() -> generateSetsForOne(sublist));
            }

            final List<Future<List<ValidatingPrimeSet>>> resultFromParts = executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);
            executorPool.shutdown();


            for (final Future<List<ValidatingPrimeSet>> result : resultFromParts) {
                List<ValidatingPrimeSet> sets = result.get();
                foundSets.addAll(sets);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return foundSets;
    }


    private List<ValidatingPrimeSet> generateSetsForOne(List<Integer> primes) {
        List<ValidatingPrimeSet> sets = new ArrayList<>();
        for (int entry : primes) {
            ValidatingPrimeSet set = new ValidatingPrimeSet();
            if (set.addEntry(entry)) {
                sets.add(set);
            }
        }
        return sets;
    }

    private List<ValidatingPrimeSet> handleTwo(ValidatingPrimeSet set) {

        List<ValidatingPrimeSet> validSets = new ArrayList<>();

        if (set.countReached(2)){
            validSets.add(set);
            return validSets;
        }

        // Handling two one digit two combinations.
        List<int[]> oneTwoCombinations = Combinations.combination(this.categorizer.getBucketForCharacterAndCharacterCount(2,1),2);
        for (int[] combination : oneTwoCombinations) {
            ValidatingPrimeSet oneTwoSet = new ValidatingPrimeSet(set);
            boolean combinationInvalid = false;
            for (int two : combination) {
                if (!oneTwoSet.addEntry(two)) {
                    combinationInvalid = true;
                }
            }
            if (combinationInvalid) { continue; }
            validSets.add(oneTwoSet);
        }

        // Handling one prime with two twos.
        for (int twoTwos : this.categorizer.getBucketForCharacterAndCharacterCount(2,2)) {
            ValidatingPrimeSet oneDoubleTwoSet = new ValidatingPrimeSet(set);
            if (oneDoubleTwoSet.addEntry(twoTwos)) {
                validSets.add(oneDoubleTwoSet);
            } else {
                continue;
            }
        }

        return validSets;
    }



    private void handle3Missing3s(HashSet<ValidatingPrimeSet> validSets, ValidatingPrimeSet set, List<int[]> singleThreeDoubleThreeCombination, List<int[]> threeSingleThreeCombinations) {
        this.handleTrippleThree(validSets, set);
        this.handleMixedThrees(validSets, set,singleThreeDoubleThreeCombination);
        this.handleSingleThrees(validSets, set,threeSingleThreeCombinations);
    }


    private void handleCombinations(HashSet<ValidatingPrimeSet> validSets, List<int[]> combinations, ValidatingPrimeSet set) {
        for (int[] combination: combinations) {
            ValidatingPrimeSet combinationSet = new ValidatingPrimeSet(set);
            boolean combinationInvalid = false;
            for (int three : combination ) {
                if (!combinationSet.addEntry(three)) {
                    combinationInvalid = true;
                }
                if (combinationInvalid) { break; }
            }
            if (combinationInvalid) { continue; }
            validSets.add(combinationSet);
        }
    }

    private void handleSingleThrees(HashSet<ValidatingPrimeSet> validSets, ValidatingPrimeSet set, List<int[]> threeSingleThreeCombinations) {
        this.handleCombinations(validSets, threeSingleThreeCombinations, set);
    }

    private void handleMixedThrees(HashSet<ValidatingPrimeSet> validSets,ValidatingPrimeSet set, List<int[]> singleThreeDoubleThreeCombination) {
        this.handleCombinations(validSets, singleThreeDoubleThreeCombination, set);
    }

    private void handleTrippleThree(HashSet<ValidatingPrimeSet> validSets, ValidatingPrimeSet set) {
        // Add a single prime number with three 3s:
        for (int three : this.categorizer.getBucketForCharacterAndCharacterCount(3,3)) {
            ValidatingPrimeSet singleThreeSet = new ValidatingPrimeSet(set);
            if (singleThreeSet.addEntry(three)) {
                validSets.add(singleThreeSet);
            }
        }
    }


    private void handleFourMissingFours(HashSet<ValidatingPrimeSet> validSets,ValidatingPrimeSet set, int[] quadrupleFours
            , List<int[]> doubleFoursCombinations, List<int[]> singleFourCombinations, List<int[]> tripleAndSingleCombination) {

        // A prime containing four 4s.
        for (int four : quadrupleFours) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.addEntry(four)){
                validSets.add(newSet);
            }
        }

        // Two primes with two 4s.
        this.handleCombinations(validSets, doubleFoursCombinations, set);

        // Four primes with one 4.
        this.handleCombinations(validSets, singleFourCombinations, set);

        // One prime with three 4s and one prime with one 4.
        this.handleCombinations(validSets, tripleAndSingleCombination, set);
    }

    private void handleThreeMissingFours(HashSet<ValidatingPrimeSet> validSets ,ValidatingPrimeSet set, int[] tripleFours, List<int[]> onesingleDigitFourAndADoubleDigitFourCombinations) {
        // One prime with three fours.
        for (int four : tripleFours ) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.addEntry(four)) {
                validSets.add(newSet);
            }
        }

        // A prime with two 4 and a prime with one 4.
        this.handleCombinations(validSets, onesingleDigitFourAndADoubleDigitFourCombinations, set);
    }

    private void handleTwoMissingFours(HashSet<ValidatingPrimeSet> validSets, ValidatingPrimeSet set, int[] doubleFours, List<int[]> singleDigitFoursCombinations) {
        // Combination of two primes with one 4 each.
        this.handleCombinations(validSets ,singleDigitFoursCombinations, set);

        // A prime with two 4s.
        for (int doubleFour : doubleFours ) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.addEntry(doubleFour)) {
                validSets.add(newSet);
            }
        }
    }

    private void handleOneMissingFour(HashSet<ValidatingPrimeSet> validSets, ValidatingPrimeSet set, int[] singleDigitFours) {
        for (int four : singleDigitFours ) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.addEntry(four)){
                validSets.add(newSet);
            }
        }
    }

    private void handleFive(ValidatingPrimeSet set) {
        //int g = 0;
        //globalDebugCounter.incrementAndGet();
       // strings.add(set.getPrimes().stream().map(t -> t.toString()).collect(Collectors.joining(", ")));
        //Set <String> duplicates =this.findDuplicates(strings);
        //if (duplicates.size() > 0 ){
         //   int g5 = 0;
        //}

        //if (strings.stream().distinct().count() < strings.size()) {
          //  int h = 3;
        //}

        //System.out.println(set.getPrimes().stream().map(t -> t.toString()).collect(Collectors.joining(", ")));
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
}
