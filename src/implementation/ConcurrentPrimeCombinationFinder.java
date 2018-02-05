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
/*
        List<String> strings = new ArrayList<>();
        for (ValidatingPrimeSet set: sets) {
            strings.add(set.getPrimes().stream().map(t -> t.toString()).collect(Collectors.joining(", ")));
        }

        System.out.println("Stringsize: " + strings.size() + " distinct count: " + strings.stream().distinct().count());
        Set<ValidatingPrimeSet> duplicates = findDuplicates(sets);
        System.out.println("duplicates: " + duplicates.size());
*/
        //sets = this.runForThree(sets);
        System.out.println(replaceLaterSet.size() + " sets after generating 3");

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

        List<Integer> oneThreeList = this.categorizer.getBucketForCharacterAndCharacterCount(3,1);
        List<Integer> twoThreeList = this.categorizer.getBucketForCharacterAndCharacterCount(3,2);
        List<List<Integer>> oneThreeCombinations = Combinations.combination(oneThreeList,2);
        List<Integer> singleThrees = this.categorizer.getBucketForCharacterAndCharacterCount(3,1);
        List<Integer> doubleThrees = this.categorizer.getBucketForCharacterAndCharacterCount(3,2);
        List<Integer> combined = new ArrayList<>();
        combined.addAll(singleThrees);
        combined.addAll(doubleThrees);
        List<List<Integer>> singleThreeDoubleThreeCombination = Combinations.combination(combined,3);
        List<List<Integer>> threeSingleThreeCombinations = Combinations.combination(singleThrees,3);

        while (!sublist.isEmpty()) {
            ValidatingPrimeSet set = sublist.poll();
            if (set.countReached(3)) {
                validSets.add(set);
                continue;
            }

            int missingThrees = set.countOfMissingDigit(3);
            switch (missingThrees) {
                case 1:
                    for (Integer three : oneThreeList ) {
                        ValidatingPrimeSet oneThreeSet = new ValidatingPrimeSet(set);
                        if (oneThreeSet.addEntry(three)){
                            validSets.add(oneThreeSet);
                        }
                    }
                    break;
                case 2:
                    for (Integer three : twoThreeList ) {
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

        List<Integer> singleDigitFours = this.categorizer.getBucketForCharacterAndCharacterCount(4, 1);
        List<List<Integer>> singleDigitFoursCombinations = Combinations.combination(singleDigitFours, 2);
        List<Integer> doubleFours = this.categorizer.getBucketForCharacterAndCharacterCount(4,2);
        List<Integer> tripleFour = this.categorizer.getBucketForCharacterAndCharacterCount(4, 3);


        List<Integer> singleFoursList = this.categorizer.getBucketForCharacterAndCharacterCount(4,1);
        List<Integer> combined = new ArrayList<>();
        combined.addAll(doubleFours);
        combined.addAll(singleFoursList);
        List<List<Integer>> oneSingleDigitFourAndADoubleDigitFourCombinations = Combinations.combination(combined,2);

        List<Integer> quadrupleFours = this.categorizer.getBucketForCharacterAndCharacterCount(4,4);
        List<List<Integer>> doubleFoursCombinations = Combinations.combination(doubleFours, 2);
        List<List<Integer>> singleFourCombinations = Combinations.combination(singleDigitFours,4);

        // One prime with three 4s and one prime with one 4.
        List<Integer> tripleAndSingleCombined = new ArrayList<>();
        tripleAndSingleCombined.addAll(tripleFour);
        tripleAndSingleCombined.addAll(singleDigitFours);
        List<List<Integer>> tripleAndSingleCombination = Combinations.combination(tripleAndSingleCombined,2);

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
            List<Integer> ones = this.categorizer.getBucketForCharacterAndCharacterCount(1,1);
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
                List<Integer> sublist = ones.subList(from, end);
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
        for (Integer entry : primes) {
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
        List<List<Integer>> oneTwoCombinations = Combinations.combination(this.categorizer.getBucketForCharacterAndCharacterCount(2,1),2);
        for (List<Integer> combination : oneTwoCombinations) {
            ValidatingPrimeSet oneTwoSet = new ValidatingPrimeSet(set);
            boolean combinationInvalid = false;
            for (Integer two : combination) {
                if (!oneTwoSet.addEntry(two)) {
                    combinationInvalid = true;
                }
            }
            if (combinationInvalid) { continue; }
            validSets.add(oneTwoSet);
        }

        // Handling one prime with two twos.
        for (Integer twoTwos : this.categorizer.getBucketForCharacterAndCharacterCount(2,2)) {
            ValidatingPrimeSet oneDoubleTwoSet = new ValidatingPrimeSet(set);
            if (oneDoubleTwoSet.addEntry(twoTwos)) {
                validSets.add(oneDoubleTwoSet);
            } else {
                continue;
            }
        }

        return validSets;
    }



    private void handle3Missing3s(HashSet<ValidatingPrimeSet> validSets, ValidatingPrimeSet set, List<List<Integer>> singleThreeDoubleThreeCombination, List<List<Integer>> threeSingleThreeCombinations) {
        this.handleTrippleThree(validSets, set);
        this.handleMixedThrees(validSets, set,singleThreeDoubleThreeCombination);
        this.handleSingleThrees(validSets, set,threeSingleThreeCombinations);
    }


    private void handleCombinations(HashSet<ValidatingPrimeSet> validSets, List<List<Integer>> combinations, ValidatingPrimeSet set) {

        for (List<Integer> combination: combinations) {
            ValidatingPrimeSet combinationSet = new ValidatingPrimeSet(set);
            boolean combinationInvalid = false;
            for (Integer three : combination ) {
                if (!combinationSet.addEntry(three)) {
                    combinationInvalid = true;
                }
                if (combinationInvalid) { break; }
            }
            if (combinationInvalid) { continue; }
            validSets.add(combinationSet);
        }
    }

    private void handleSingleThrees(HashSet<ValidatingPrimeSet> validSets, ValidatingPrimeSet set, List<List<Integer>> threeSingleThreeCombinations) {
        this.handleCombinations(validSets, threeSingleThreeCombinations, set);
    }

    private void handleMixedThrees(HashSet<ValidatingPrimeSet> validSets,ValidatingPrimeSet set, List<List<Integer>> singleThreeDoubleThreeCombination) {
        this.handleCombinations(validSets, singleThreeDoubleThreeCombination, set);
    }

    private void handleTrippleThree(HashSet<ValidatingPrimeSet> validSets, ValidatingPrimeSet set) {
        // Add a single prime number with three 3s:
        for (Integer three : this.categorizer.getBucketForCharacterAndCharacterCount(3,3)) {
            ValidatingPrimeSet singleThreeSet = new ValidatingPrimeSet(set);
            if (singleThreeSet.addEntry(three)) {
                validSets.add(singleThreeSet);
            }
        }
    }


    private void handleFourMissingFours(HashSet<ValidatingPrimeSet> validSets,ValidatingPrimeSet set, List<Integer> quadrupleFours
            , List<List<Integer>> doubleFoursCombinations, List<List<Integer>> singleFourCombinations, List<List<Integer>> tripleAndSingleCombination) {

        // A prime containing four 4s.
        for (Integer four : quadrupleFours) {
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

    private void handleThreeMissingFours(HashSet<ValidatingPrimeSet> validSets ,ValidatingPrimeSet set, List<Integer> tripleFours, List<List<Integer>> onesingleDigitFourAndADoubleDigitFourCombinations) {
        // One prime with three fours.
        for (Integer four : tripleFours ) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.addEntry(four)) {
                validSets.add(newSet);
            }
        }

        // A prime with two 4 and a prime with one 4.
        this.handleCombinations(validSets, onesingleDigitFourAndADoubleDigitFourCombinations, set);
    }

    private void handleTwoMissingFours(HashSet<ValidatingPrimeSet> validSets, ValidatingPrimeSet set, List<Integer> doubleFours, List<List<Integer>> singleDigitFoursCombinations) {
        // Combination of two primes with one 4 each.
        this.handleCombinations(validSets ,singleDigitFoursCombinations, set);

        // A prime with two 4s.
        for (Integer doubleFour : doubleFours ) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.addEntry(doubleFour)) {
                validSets.add(newSet);
            }
        }
    }



    private List<ValidatingPrimeSet> handleOneMissingFour(ValidatingPrimeSet set, List<Integer> singleDigitFours) {
        List<ValidatingPrimeSet> validSets = new ArrayList<>();
        for (Integer four : singleDigitFours ) {
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
