package implementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConcurrentPrimeCombinationFinder {
    private final PrimeCategorizer categorizer;

    public ConcurrentPrimeCombinationFinder(PrimeCategorizer categorizer) {
        this.categorizer = categorizer;
    }

    public boolean run(){
        boolean foundOne = false;
        try {


            // The ones are the starting point.
            List<Long> ones = this.categorizer.getBucketForCharacterAndCharacterCount(1,1);
            final List<Callable<Boolean>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);

            int sliceSize = ones.size() / Configuration.instance.maximumNumberOfThreads;
            System.out.format("Using %d threads. SliceSize: %d \n", Configuration.instance.maximumNumberOfThreads, sliceSize);


            for (int i = 0; i <= ones.size(); i += sliceSize) {
                final int from = i;
                int to = i + sliceSize;
                if (to > ones.size())
                    to = ones.size();
                final int end = to;
                List<Long> sublist = ones.subList(from, end);
                partitions.add(() -> findPrimesForStartPoint(sublist));
            }

            final List<Future<Boolean>> resultFromParts = executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);
            executorPool.shutdown();


            for (final Future<Boolean> result : resultFromParts)
                if (result.get()) {
                foundOne = true;
                }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return foundOne;
    }

    private Boolean findPrimesForStartPoint(List<Long> sublist) {
        //System.out.println("From: " + sublist.get(0) + " to: " + sublist.get(sublist.size() - 1));
        for (Long entry : sublist ) {
            this.handleOne(entry);
        }

        return false;
    }

    private void handleOne(Long entry) {
        ValidatingPrimeSet set = new ValidatingPrimeSet();
        set.addEntry(entry);

        this.handleTwo(set);
    }

    private void handleTwo(ValidatingPrimeSet set) {

        if (set.countReached(2)){
            this.handleThree(set);
        }

        // Handling two one digit two combinations.
        List<List<Long>> oneTwoCombinations = Combinations.combination(this.categorizer.getBucketForCharacterAndCharacterCount(2,1),2);
        for (List<Long> combination : oneTwoCombinations) {
            ValidatingPrimeSet oneTwoSet = new ValidatingPrimeSet(set);
            boolean combinationInvalid = false;
            for (Long two : combination) {
                if (!oneTwoSet.addEntry(two)) {
                    combinationInvalid = true;
                }
            }
            if (combinationInvalid) { continue; }
            this.handleThree(oneTwoSet);
        }

        // Handling one prime with two twos.
        for (Long twoTwos : this.categorizer.getBucketForCharacterAndCharacterCount(2,2)) {
            ValidatingPrimeSet oneDoubleTwoSet = new ValidatingPrimeSet(set);
            if (oneDoubleTwoSet.addEntry(twoTwos)) {
                this.handleThree(oneDoubleTwoSet);
            } else {
                continue;
            }
        }

    }

    private void handleThree(ValidatingPrimeSet set) {
        if (set.countReached(3)) {
          this.handleFour(set);
        }
        List<Long> oneThreeList = this.categorizer.getBucketForCharacterAndCharacterCount(3,1);

        int missingThrees = set.countOfMissingDigit(3);
        switch (missingThrees) {
            case 1:
                for (Long three : oneThreeList ) {
                    ValidatingPrimeSet oneThreeSet = new ValidatingPrimeSet(set);
                    if (oneThreeSet.addEntry(three)){
                        this.handleFour(oneThreeSet);
                    }
                }
                break;
            case 2:
                List<Long> twoThreeList = this.categorizer.getBucketForCharacterAndCharacterCount(3,2);
                for (Long three : twoThreeList ) {
                    ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                    if (newSet.addEntry(three)){
                        this.handleFour(newSet);
                    }
                }
                List<List<Long>> oneThreeCombinations = Combinations.combination(oneThreeList,2);
                this.handleThreeCombination(oneThreeCombinations, set);
                break;
            case 3:
                this.handle3Missing3s(set);
                break;

        }
    }

    private void handle3Missing3s(ValidatingPrimeSet set) {
        this.handleTrippleThree(set);
        this.handleMixedThrees(set);
        this.handleSingleThrees(set);
    }

    // TODO CombinationHandling could be one method, if the next method could be added as parameter.
    private void handleThreeCombination(List<List<Long>> combinations, ValidatingPrimeSet set) {
        for (List<Long> combination: combinations) {
            ValidatingPrimeSet combinationSet = new ValidatingPrimeSet(set);
            boolean combinationInvalid = false;
            for (Long three : combination ) {
                if (!combinationSet.addEntry(three)) {
                    combinationInvalid = true;
                }
                if (combinationInvalid) { break; }
            }
            if (combinationInvalid) { continue; }
            this.handleFour(combinationSet);
        }
    }
    private void handleFourCombination(List<List<Long>> combinations, ValidatingPrimeSet set) {
        for (List<Long> combination: combinations) {
            ValidatingPrimeSet combinationSet = new ValidatingPrimeSet(set);
            boolean combinationInvalid = false;
            for (Long four : combination ) {
                if (!combinationSet.addEntry(four)) {
                    combinationInvalid = true;
                }
                if (combinationInvalid) { break; }
            }
            if (combinationInvalid) { continue; }
            this.handleFive(combinationSet);
        }
    }

    private void handleSingleThrees(ValidatingPrimeSet set) {
        List<Long> singleThrees = this.categorizer.getBucketForCharacterAndCharacterCount(3,1);
        List<List<Long>> combinations = Combinations.combination(singleThrees,3);
        this.handleThreeCombination(combinations, set);
    }

    private void handleMixedThrees(ValidatingPrimeSet set) {
        List<Long> singleThrees = this.categorizer.getBucketForCharacterAndCharacterCount(3,1);
        List<Long> doubleThrees = this.categorizer.getBucketForCharacterAndCharacterCount(3,2);
        List<Long> combined = new ArrayList<>();
        combined.addAll(singleThrees);
        combined.addAll(doubleThrees);
        List<List<Long>> combinations = Combinations.combination(combined,3);
        this.handleThreeCombination(combinations, set);
    }

    private void handleTrippleThree(ValidatingPrimeSet set) {
        // Add a single prime number with three 3s:
        for (Long three : this.categorizer.getBucketForCharacterAndCharacterCount(3,3)) {
            ValidatingPrimeSet singleThreeSet = new ValidatingPrimeSet(set);
            if (singleThreeSet.addEntry(three)) {
                this.handleFour(singleThreeSet);
            }
        }
    }

    private void handleFour(ValidatingPrimeSet set) {

        if (set.countReached(4)){
            this.handleFive(set);
        }
        int missingFours = set.countOfMissingDigit(4);
        switch (missingFours){
            case 1:
                this.handleOneMissingFour(set);
                break;
            case 2:
                this.handleTwoMissingFours(set);
                break;
            case 3:
                this.handleThreeMissingFours(set);
                break;
            case 4:
                this.handleFourMissingFours(set);
                break;
        }

    }

    private void handleFourMissingFours(ValidatingPrimeSet set) {
        // A prime containing four 4s.
        List<Long> quadrupleFour = this.categorizer.getBucketForCharacterAndCharacterCount(4,4);
        for (Long four : quadrupleFour) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.addEntry(four)){
                this.handleFive(newSet);
            }
        }

        // Two primes with two 4s.
        List<Long> doubleFours = this.categorizer.getBucketForCharacterAndCharacterCount(4, 2);
        List<List<Long>> doubleFoursCombinations = Combinations.combination(doubleFours, 2);
        this.handleFourCombination(doubleFoursCombinations, set);

        // Four primes with one 4.
        List<Long> singleFours = this.categorizer.getBucketForCharacterAndCharacterCount(4, 1);
        singleFours = singleFours.stream().filter(t -> !set.getPrimes().contains(t)).collect(Collectors.toList());
        List<List<Long>> singleFourCombinations = Combinations.combination(singleFours,4);
        this.handleFourCombination(singleFourCombinations, set);

        // One prime with three 4s and one prime with one 4.
        List<Long> tripleFours = this.categorizer.getBucketForCharacterAndCharacterCount(4,3);
        List<Long> combined = new ArrayList<>();
        combined.addAll(tripleFours);
        combined.addAll(singleFours);
        List<List<Long>> combinations = Combinations.combination(combined,2);
        this.handleFourCombination(combinations, set);
    }

    private void handleThreeMissingFours(ValidatingPrimeSet set) {
        // One prime with three fours.
        List<Long> tripleFour = this.categorizer.getBucketForCharacterAndCharacterCount(4, 3);
        for (Long four : tripleFour ) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.addEntry(four)) {
                this.handleFive(newSet);
            }
        }

        // A prime with two 4 and a prime with one 4.
        List<Long> trippleFoursList = this.categorizer.getBucketForCharacterAndCharacterCount(4,2);
        List<Long> singleFoursList = this.categorizer.getBucketForCharacterAndCharacterCount(4,1);
        List<Long> combined = new ArrayList<>();
        combined.addAll(trippleFoursList);
        combined.addAll(singleFoursList);
        List<List<Long>> combinations = Combinations.combination(combined,2);
        this.handleFourCombination(combinations, set);
    }

    private void handleTwoMissingFours(ValidatingPrimeSet set) {
        // Combination of two primes with one 4 each.
        List<Long> singleFour = this.categorizer.getBucketForCharacterAndCharacterCount(4,1);
        List<List<Long>> combinations = Combinations.combination(singleFour, 2);
        this.handleFourCombination(combinations, set);

        // A prime with two 4s.
        List<Long> doubleFours = this.categorizer.getBucketForCharacterAndCharacterCount(4,2);
        for (Long doubleFour : doubleFours ) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.addEntry(doubleFour)) {
                this.handleFive(newSet);
            }
        }
    }



    private void handleOneMissingFour(ValidatingPrimeSet set) {
        List<Long> fours = this.categorizer.getBucketForCharacterAndCharacterCount(4, 1);
        for (Long four : fours ) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.addEntry(four)){
                this.handleFive(newSet);
            }
        }
    }

    private void handleFive(ValidatingPrimeSet set) {
        //int g = 0;
        //globalDebugCounter.incrementAndGet();
        strings.add(set.getPrimes().stream().map(t -> t.toString()).collect(Collectors.joining(", ")));
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
