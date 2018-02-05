package implementation;

import java.util.ArrayList;
import java.util.Collection;
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
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConcurrentPrimeCombinationFinder {
    private final PrimeCategorizer categorizer;

    public ConcurrentPrimeCombinationFinder(PrimeCategorizer categorizer) {
        this.categorizer = categorizer;
    }

    public void run() {
        List<ValidatingPrimeSet> sets = this.runForOne();
        System.out.println(sets.size() + " sets after generating 1");
        sets = this.runForTwo(sets);
        System.out.println(sets.size() + " sets after generating 2");
        sets = this.runForThree(sets);
        System.out.println(sets.size() + " sets after generating 3");
        sets = this.runForFour(sets);
        int g = 0;
    }
    private List<ValidatingPrimeSet> runForThree(List<ValidatingPrimeSet> sourceSets) {
        List<ValidatingPrimeSet> foundSets = new ArrayList<>();
        try {

            final List<Callable<List<ValidatingPrimeSet>>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);

            int sliceSize = sourceSets.size() / Configuration.instance.maximumNumberOfThreads;
            System.out.format("Two: Using %d threads. SliceSize: %d \n", Configuration.instance.maximumNumberOfThreads, sliceSize);


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

    private List<ValidatingPrimeSet> runForFour(List<ValidatingPrimeSet> sourceSets) {
        List<ValidatingPrimeSet> foundSets = new ArrayList<>();
        try {

            final List<Callable<List<ValidatingPrimeSet>>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);

            int sliceSize = sourceSets.size() / Configuration.instance.maximumNumberOfThreads;
            System.out.format("Two: Using %d threads. SliceSize: %d \n", Configuration.instance.maximumNumberOfThreads, sliceSize);


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

    private List<ValidatingPrimeSet> generateSetsForThree(ConcurrentLinkedQueue<ValidatingPrimeSet> sublist) {
        List<ValidatingPrimeSet> validSets = new ArrayList<>();
        List<Long> oneThreeList = this.categorizer.getBucketForCharacterAndCharacterCount(3,1);
        List<Long> twoThreeList = this.categorizer.getBucketForCharacterAndCharacterCount(3,2);
        List<List<Long>> oneThreeCombinations = Combinations.combination(oneThreeList,2);
        List<Long> singleThrees = this.categorizer.getBucketForCharacterAndCharacterCount(3,1);
        List<Long> doubleThrees = this.categorizer.getBucketForCharacterAndCharacterCount(3,2);
        List<Long> combined = new ArrayList<>();
        combined.addAll(singleThrees);
        combined.addAll(doubleThrees);
        List<List<Long>> singleThreeDoubleThreeCombination = Combinations.combination(combined,3);
        List<List<Long>> threeSingleThreeCombinations = Combinations.combination(singleThrees,3);

        while (!sublist.isEmpty()) {
            ValidatingPrimeSet set = sublist.poll();
            if (set.countReached(3)) {
                validSets.add(set);
                continue;
            }

            int missingThrees = set.countOfMissingDigit(3);
            switch (missingThrees) {
                case 1:
                    for (Long three : oneThreeList ) {
                        ValidatingPrimeSet oneThreeSet = new ValidatingPrimeSet(set);
                        if (oneThreeSet.addEntry(three)){
                            validSets.add(oneThreeSet);
                        }
                    }
                    break;
                case 2:
                    for (Long three : twoThreeList ) {
                        ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                        if (newSet.addEntry(three)){
                            validSets.add(newSet);
                        }
                    }
                    validSets.addAll(handleCombinations(oneThreeCombinations, set));
                    break;
                case 3:
                    validSets.addAll(this.handle3Missing3s(set, singleThreeDoubleThreeCombination, threeSingleThreeCombinations));
                    break;

            }
        }
        return validSets;
    }

    private List<ValidatingPrimeSet> generateSetsForFour(ConcurrentLinkedQueue<ValidatingPrimeSet> sublist) {
        List<ValidatingPrimeSet> validSets = new ArrayList<>();

        List<Long> singleDigitFours = this.categorizer.getBucketForCharacterAndCharacterCount(4, 1);
        List<List<Long>> singleDigitFoursCombinations = Combinations.combination(singleDigitFours, 2);
        List<Long> doubleFours = this.categorizer.getBucketForCharacterAndCharacterCount(4,2);
        List<Long> tripleFour = this.categorizer.getBucketForCharacterAndCharacterCount(4, 3);


        List<Long> singleFoursList = this.categorizer.getBucketForCharacterAndCharacterCount(4,1);
        List<Long> combined = new ArrayList<>();
        combined.addAll(doubleFours);
        combined.addAll(singleFoursList);
        List<List<Long>> oneSingleDigitFourAndADoubleDigitFourCombinations = Combinations.combination(combined,2);

        List<Long> quadrupleFours = this.categorizer.getBucketForCharacterAndCharacterCount(4,4);
        List<List<Long>> doubleFoursCombinations = Combinations.combination(doubleFours, 2);
        List<List<Long>> singleFourCombinations = Combinations.combination(singleDigitFours,4);

        // One prime with three 4s and one prime with one 4.
        List<Long> tripleAndSingleCombined = new ArrayList<>();
        tripleAndSingleCombined.addAll(tripleFour);
        tripleAndSingleCombined.addAll(singleDigitFours);
        List<List<Long>> tripleAndSingleCombination = Combinations.combination(tripleAndSingleCombined,2);

        while (!sublist.isEmpty()) {
            ValidatingPrimeSet set = sublist.poll();
            if (set.countReached(4)){
                validSets.add(set);
                continue;
            }
            int missingFours = set.countOfMissingDigit(4);
            switch (missingFours){
                case 1:
                    validSets.addAll(this.handleOneMissingFour(set,singleDigitFours));
                    break;
                case 2:
                    validSets.addAll(this.handleTwoMissingFours(set, doubleFours, singleDigitFoursCombinations));
                    break;
                case 3:
                    validSets.addAll(this.handleThreeMissingFours(set, tripleFour, oneSingleDigitFourAndADoubleDigitFourCombinations));
                    break;
                case 4:
                    validSets.addAll(this.handleFourMissingFours(set, quadrupleFours, doubleFoursCombinations, singleFourCombinations, tripleAndSingleCombination));
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
            System.out.format("Two: Using %d threads. SliceSize: %d \n", Configuration.instance.maximumNumberOfThreads, sliceSize);


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
            List<Long> ones = this.categorizer.getBucketForCharacterAndCharacterCount(1,1);
            final List<Callable<List<ValidatingPrimeSet>>> partitions = new ArrayList<>();
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


    private List<ValidatingPrimeSet> generateSetsForOne(List<Long> primes) {
        List<ValidatingPrimeSet> sets = new ArrayList<>();
        for (Long entry : primes) {
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
            validSets.add(oneTwoSet);
        }

        // Handling one prime with two twos.
        for (Long twoTwos : this.categorizer.getBucketForCharacterAndCharacterCount(2,2)) {
            ValidatingPrimeSet oneDoubleTwoSet = new ValidatingPrimeSet(set);
            if (oneDoubleTwoSet.addEntry(twoTwos)) {
                validSets.add(oneDoubleTwoSet);
            } else {
                continue;
            }
        }

        return validSets;
    }



    private List<ValidatingPrimeSet> handle3Missing3s(ValidatingPrimeSet set, List<List<Long>> singleThreeDoubleThreeCombination, List<List<Long>> threeSingleThreeCombinations) {
        List<ValidatingPrimeSet> validSets = new ArrayList<>();
        validSets.addAll(this.handleTrippleThree(set));
        validSets.addAll(this.handleMixedThrees(set,singleThreeDoubleThreeCombination));
        validSets.addAll(this.handleSingleThrees(set,threeSingleThreeCombinations));
        return validSets;
    }


    private List<ValidatingPrimeSet> handleCombinations(List<List<Long>> combinations, ValidatingPrimeSet set) {
        List<ValidatingPrimeSet> validSets = new ArrayList<>();

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
            validSets.add(combinationSet);
        }
        return validSets;
    }

    private List<ValidatingPrimeSet> handleSingleThrees(ValidatingPrimeSet set, List<List<Long>> threeSingleThreeCombinations) {
        return this.handleCombinations(threeSingleThreeCombinations, set);
    }

    private List<ValidatingPrimeSet> handleMixedThrees(ValidatingPrimeSet set, List<List<Long>> singleThreeDoubleThreeCombination) {

        return this.handleCombinations(singleThreeDoubleThreeCombination, set);
    }

    private List<ValidatingPrimeSet> handleTrippleThree(ValidatingPrimeSet set) {
        List<ValidatingPrimeSet> validSets = new ArrayList<>();
        // Add a single prime number with three 3s:
        for (Long three : this.categorizer.getBucketForCharacterAndCharacterCount(3,3)) {
            ValidatingPrimeSet singleThreeSet = new ValidatingPrimeSet(set);
            if (singleThreeSet.addEntry(three)) {
                validSets.add(singleThreeSet);
            }
        }
        return validSets;
    }


    private List<ValidatingPrimeSet> handleFourMissingFours(ValidatingPrimeSet set, List<Long> quadrupleFours
            , List<List<Long>> doubleFoursCombinations, List<List<Long>> singleFourCombinations, List<List<Long>> tripleAndSingleCombination) {
        List<ValidatingPrimeSet> validSets = new ArrayList<>();

        // A prime containing four 4s.
        for (Long four : quadrupleFours) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.addEntry(four)){
                validSets.add(newSet);
            }
        }

        // Two primes with two 4s.
        validSets.addAll(this.handleCombinations(doubleFoursCombinations, set));

        // Four primes with one 4.
        validSets.addAll(this.handleCombinations(singleFourCombinations, set));

        // One prime with three 4s and one prime with one 4.
        validSets.addAll(this.handleCombinations(tripleAndSingleCombination, set));

        return validSets;
    }

    private List<ValidatingPrimeSet>  handleThreeMissingFours(ValidatingPrimeSet set, List<Long> tripleFours, List<List<Long>> onesingleDigitFourAndADoubleDigitFourCombinations) {
        List<ValidatingPrimeSet> validSets = new ArrayList<>();
        // One prime with three fours.
        for (Long four : tripleFours ) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.addEntry(four)) {
                validSets.add(newSet);
            }
        }

        // A prime with two 4 and a prime with one 4.
        validSets.addAll(this.handleCombinations(onesingleDigitFourAndADoubleDigitFourCombinations, set));
        return validSets;
    }

    private List<ValidatingPrimeSet> handleTwoMissingFours(ValidatingPrimeSet set, List<Long> doubleFours, List<List<Long>> singleDigitFoursCombinations) {
       List<ValidatingPrimeSet> validSets = new ArrayList<>();
        // Combination of two primes with one 4 each.
        validSets.addAll(this.handleCombinations(singleDigitFoursCombinations, set));

        // A prime with two 4s.
        for (Long doubleFour : doubleFours ) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.addEntry(doubleFour)) {
                validSets.add(newSet);
            }
        }
        return validSets;
    }



    private List<ValidatingPrimeSet> handleOneMissingFour(ValidatingPrimeSet set, List<Long> singleDigitFours) {
        List<ValidatingPrimeSet> validSets = new ArrayList<>();
        for (Long four : singleDigitFours ) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.addEntry(four)){
                validSets.add(newSet);
            }
        }
        return validSets;
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
