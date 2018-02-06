package implementation;

import com.sun.deploy.util.ArrayUtil;

import java.lang.reflect.Array;
import java.util.*;
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
        // TODO Just get a chance to connect with visual vm
        this.initCachedData();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Boolean> sets = this.runForOne();
        System.out.println(sets.size() + " sets after generating 1");
        System.out.println("Atomic global counter: " + globalDebugCounter.get());
        //this.printDebugDuplicates(sets);
        //sets = this.runForTwo(sets);
        //System.out.println(sets.size() + " sets after generating 2");
        //this.printDebugDuplicates(sets);



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
       // HashSet<ValidatingPrimeSet> replaceLaterSet = this.runForThree(sets);
        //System.out.println(replaceLaterSet.size() + " sets after generating 3");
        //this.printDebugDuplicates(new ArrayList<>(replaceLaterSet));

       // System.out.println(newSets.size() + " new sets after generating 3");
        //replaceLaterSet = this.runForFour(new ArrayList<>(replaceLaterSet));
        //System.out.println(replaceLaterSet.size() + " sets after generating 4");
        int g = 0;
    }

    private List<int[]> oneThreeCombinations;
    private int[] singleThrees;
    private int[] doubleThrees;
    private List<int[]> singleThreeDoubleThreeCombination;
    private  List<int[]> threeSingleThreeCombinations;
    private int[] singleDigitFours;
    private List<int[]> singleDigitFoursCombinations;
    private int[] doubleFours;
    private int[] tripleFour;
    int[] singleFoursList;
    private List<int[]> oneSingleDigitFourAndADoubleDigitFourCombinations;
    int[] quadrupleFours;
    List<int[]> doubleFoursCombinations;
    List<int[]> singleFourCombinations;
    List<int[]> tripleAndSingleCombination;
    List<int[]> oneTwoCombinations;
    int[] doubleTwos;
    int[] tripleThrees;

    private void initCachedData(){
        this.tripleThrees = this.categorizer.getBucketForCharacterAndCharacterCount(3,3);
        this.doubleTwos = this.categorizer.getBucketForCharacterAndCharacterCount(2,2);
        this.oneTwoCombinations = Combinations.combination(this.categorizer.getBucketForCharacterAndCharacterCount(2,1),2);
        this.singleThrees = this.categorizer.getBucketForCharacterAndCharacterCount(3,1);
        this.doubleThrees = this.categorizer.getBucketForCharacterAndCharacterCount(3,2);
        this.oneThreeCombinations = Combinations.combination(this.singleThrees,2);
        // TODO Check the merge!
        int[] combined = merge(singleThrees, doubleThrees);
        this.singleThreeDoubleThreeCombination = Combinations.combination(combined,3);
        this.threeSingleThreeCombinations = Combinations.combination(singleThrees,3);
        this.singleDigitFours = this.categorizer.getBucketForCharacterAndCharacterCount(4, 1);
        this.singleDigitFoursCombinations =  Combinations.combination(singleDigitFours, 2);
        this.doubleFours = this.categorizer.getBucketForCharacterAndCharacterCount(4,2);
        this.tripleFour = this.categorizer.getBucketForCharacterAndCharacterCount(4, 3);
        this.singleFoursList = this.categorizer.getBucketForCharacterAndCharacterCount(4,1);
        int[] combinedFours = merge(doubleFours, singleFoursList);
        oneSingleDigitFourAndADoubleDigitFourCombinations = Combinations.combination(combinedFours,2);
        this.quadrupleFours = this.categorizer.getBucketForCharacterAndCharacterCount(4,4);
        this.doubleFoursCombinations = Combinations.combination(doubleFours, 2);
        this.singleFourCombinations = Combinations.combination(singleDigitFours,4);
        int[] tripleAndSingleCombined = merge(tripleFour, singleDigitFours);
        this.tripleAndSingleCombination = Combinations.combination(tripleAndSingleCombined,2);
    }

    private void handleThree(ValidatingPrimeSet set){
            if (set.countReached(3)) {
                this.handleFour(set);
                return;
            }

            int missingThrees = set.countOfMissingDigit(3);
            switch (missingThrees) {
                case 1:
                    for (int three : this.singleThrees ) {
                        ValidatingPrimeSet oneThreeSet = new ValidatingPrimeSet(set);
                        if (oneThreeSet.addEntry(three)){
                            this.handleFour(oneThreeSet);
                        }
                    }
                    break;
                case 2:
                    for (int three : this.doubleThrees ) {
                        ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                        if (newSet.addEntry(three)){
                            this.handleFour(newSet);
                        }
                    }
                    ArrayList<ValidatingPrimeSet> combinationSetCase2 = new ArrayList<>();
                    handleCombinations(combinationSetCase2, oneThreeCombinations, set);
                    for (int i = 0, combinationSetCase2Size = combinationSetCase2.size(); i < combinationSetCase2Size; i++) {
                        ValidatingPrimeSet validatingPrimeSet = combinationSetCase2.get(i);
                        this.handleFour(validatingPrimeSet);
                    }
                    break;
                case 3:
                    ArrayList<ValidatingPrimeSet> combinationSetCase3 = new ArrayList<>();
                    this.handle3Missing3s(combinationSetCase3, set, singleThreeDoubleThreeCombination, threeSingleThreeCombinations);
                    for (int i = 0, combinationSetCase3Size = combinationSetCase3.size(); i < combinationSetCase3Size; i++) {
                        ValidatingPrimeSet t = combinationSetCase3.get(i);
                        this.handleFour(t);
                    }
                    break;

            }
        }

    private void handleFour(ValidatingPrimeSet set) {

        // One prime with three 4s and one prime with one 4.
        if (set.countReached(4)){
            this.handleFive(set);
            return;
        }
        int missingFours = set.countOfMissingDigit(4);
        switch (missingFours){
            case 1:
                ArrayList<ValidatingPrimeSet> combinationCase1 = new ArrayList<>();
                this.handleOneMissingFour(combinationCase1, set,singleDigitFours);
                for (int i = 0, combinationCase1Size = combinationCase1.size(); i < combinationCase1Size; i++) {
                    ValidatingPrimeSet t1 = combinationCase1.get(i);
                    this.handleFive(t1);
                }
                break;
            case 2:
                ArrayList<ValidatingPrimeSet> combinationCase2 = new ArrayList<>();
                this.handleTwoMissingFours(combinationCase2, set, doubleFours, singleDigitFoursCombinations);
                for (int i = 0, combinationCase2Size = combinationCase2.size(); i < combinationCase2Size; i++) {
                    ValidatingPrimeSet primeSet = combinationCase2.get(i);
                    this.handleFive(primeSet);
                }
                break;
            case 3:
                ArrayList<ValidatingPrimeSet> combinationCase3 = new ArrayList<>();
                this.handleThreeMissingFours(combinationCase3, set, tripleFour, oneSingleDigitFourAndADoubleDigitFourCombinations);
                for (int i = 0, combinationCase3Size = combinationCase3.size(); i < combinationCase3Size; i++) {
                    ValidatingPrimeSet validatingPrimeSet = combinationCase3.get(i);
                    this.handleFive(validatingPrimeSet);
                }
                break;
            case 4:
                ArrayList<ValidatingPrimeSet> combinationCase4 = new ArrayList<>();
                this.handleFourMissingFours(combinationCase4, set, quadrupleFours, doubleFoursCombinations, singleFourCombinations, tripleAndSingleCombination);
                for (int i = 0, combinationCase4Size = combinationCase4.size(); i < combinationCase4Size; i++) {
                    ValidatingPrimeSet t = combinationCase4.get(i);
                    this.handleFive(t);
                }
                break;
        }
        }





    public List<Boolean> runForOne(){
        List<Boolean> foundSets = new ArrayList<>();
        try {
            // The ones are the starting point.
            int[] ones = this.categorizer.getBucketForCharacterAndCharacterCount(1,1);
            List<Integer> onesList = Arrays.stream(ones).boxed().collect(Collectors.toList());
            final List<Callable<Boolean>> partitions = new ArrayList<>();
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

            final List<Future<Boolean>> resultFromParts = executorPool.invokeAll(partitions, 900, TimeUnit.SECONDS);
            executorPool.shutdown();


            for (final Future<Boolean> result : resultFromParts) {
                Boolean sets = result.get();
                foundSets.add(sets);
            }

        } catch (Exception e) {
            System.out.println("Global counter: " + globalDebugCounter.get());
            throw new RuntimeException(e);
        }
        return foundSets;
    }


    private Boolean generateSetsForOne(List<Integer> primes) {
        for (int entry : primes) {
            ValidatingPrimeSet set = new ValidatingPrimeSet();
            if (set.addEntry(entry)) {
                this.handleTwo(set);
            }
        }
        return true;
    }

    private void handleTwo(ValidatingPrimeSet set) {


        if (set.countReached(2)){
            this.handleThree(set);
            return;
        }

        // Handling two one digit two combinations.

        for (int[] combination : oneTwoCombinations) {
            ValidatingPrimeSet oneTwoSet = new ValidatingPrimeSet(set);
            boolean combinationInvalid = false;
            for (int two : combination) {
                if (!oneTwoSet.addEntry(two)) {
                    combinationInvalid = true;
                }
            }
            if (combinationInvalid) { continue; }
            this.handleThree(oneTwoSet);
        }

        // Handling one prime with two twos.
        for (int twoTwos : this.doubleTwos) {
            ValidatingPrimeSet oneDoubleTwoSet = new ValidatingPrimeSet(set);
            if (oneDoubleTwoSet.addEntry(twoTwos)) {
                this.handleThree(oneDoubleTwoSet);
            } else {
                continue;
            }
        }
    }



    private void handle3Missing3s(ArrayList<ValidatingPrimeSet> validSets, ValidatingPrimeSet set, List<int[]> singleThreeDoubleThreeCombination, List<int[]> threeSingleThreeCombinations) {
        this.handleTrippleThree(validSets, set);
        this.handleMixedThrees(validSets, set,singleThreeDoubleThreeCombination);
        this.handleSingleThrees(validSets, set,threeSingleThreeCombinations);
    }


    private void handleCombinations(ArrayList<ValidatingPrimeSet> validSets, List<int[]> combinations, ValidatingPrimeSet set) {
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

    private void handleSingleThrees(ArrayList<ValidatingPrimeSet> validSets, ValidatingPrimeSet set, List<int[]> threeSingleThreeCombinations) {
        this.handleCombinations(validSets, threeSingleThreeCombinations, set);
    }

    private void handleMixedThrees(ArrayList<ValidatingPrimeSet> validSets,ValidatingPrimeSet set, List<int[]> singleThreeDoubleThreeCombination) {
        this.handleCombinations(validSets, singleThreeDoubleThreeCombination, set);
    }

    private void handleTrippleThree(ArrayList<ValidatingPrimeSet> validSets, ValidatingPrimeSet set) {
        // Add a single prime number with three 3s:
        for (int three : this.tripleThrees) {
            ValidatingPrimeSet singleThreeSet = new ValidatingPrimeSet(set);
            if (singleThreeSet.addEntry(three)) {
                validSets.add(singleThreeSet);
            }
        }
    }


    private void handleFourMissingFours(ArrayList<ValidatingPrimeSet> validSets,ValidatingPrimeSet set, int[] quadrupleFours
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

    private void handleThreeMissingFours(ArrayList<ValidatingPrimeSet> validSets ,ValidatingPrimeSet set, int[] tripleFours, List<int[]> onesingleDigitFourAndADoubleDigitFourCombinations) {
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

    private void handleTwoMissingFours(ArrayList<ValidatingPrimeSet> validSets, ValidatingPrimeSet set, int[] doubleFours, List<int[]> singleDigitFoursCombinations) {
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

    private void handleOneMissingFour(ArrayList<ValidatingPrimeSet> validSets, ValidatingPrimeSet set, int[] singleDigitFours) {
        for (int four : singleDigitFours ) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.addEntry(four)){
                validSets.add(newSet);
            }
        }
    }

    private void handleFive(ValidatingPrimeSet set) {
        //this.printDebugSetData(set);


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
}
