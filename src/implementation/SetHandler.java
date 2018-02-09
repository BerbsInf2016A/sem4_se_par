package implementation;

import java.util.concurrent.CancellationException;
import java.util.function.Function;

/**
 * The set handler analyzes a set and tries to add the different combinations to find a valid set
 * of prime numbers.
 */
public class SetHandler {

    /**
     * This is used to filter out invalid sets and to abort the execution, if the Thread was interrupted.
     * Throwing away a set with a invalid combinations is faster, then to filter out the combinations.
     * See comment in the PartitionCombinationGenerator.
     *
     * @param set The set to analyze.
     * @param i   The count of the number, which should already be fulfilled by the set.
     * @return True if valid, false if not.
     */
    private static boolean preCheckConditions(ValidatingPrimeSet set, int i) {
        if (Thread.interrupted()) {
            // Executor has probably asked us to stop
            System.out.println(Thread.currentThread().toString() + " has been interrupted!");
            throw new CancellationException("Thread has been requested to stop");
        }
        return set.countReached(i - 1);
    }

    /**
     * This handles the different combinations of a set.
     *
     * @param set          The source set as base for new sets.
     * @param combinations The combinations, which have to be tried.
     * @param nextFunction The function, which should be called if a set is valid.
     */
    private static void handleCombinationsWithNextFunction(ValidatingPrimeSet set, PartitionCombinationResult combinations, Function<ValidatingPrimeSet, Void> nextFunction) {
        if (combinations.hasSingleResult()) { // Handle all results for primes, which fulfill the need of missing counts with only one prime number.
            for (int singleResult : combinations.getSingle()) {
                ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                if (newSet.tryToAddEntry(singleResult)) {
                    nextFunction.apply(newSet);
                }
            }
        }
        if (combinations.hasCombinationResult()) { // Handle all results for combinations of prime numbers.
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

    /**
     * Generates the sets which fulfill the requirements for the one.
     *
     * @param set The source set.
     */
    public static void handleOne(ValidatingPrimeSet set) {
        int valueOfDigit = 1;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                handleTwo(set);
                return;
            case 1:
                int[] oneMissingCombinations = PartitionCombinationGenerator.getCategories().getBucketForCharacterAndCharacterCount(1, 1);
                for (int entry : oneMissingCombinations) {
                    ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
                    if (newSet.tryToAddEntry(entry)) {
                        handleTwo(newSet);
                    }
                }
                return;
        }
    }

    /**
     * Generates the sets which fulfill the requirements for the two.
     *
     * @param set The source set.
     * @return Null, this is a Void function, which is used as parameter.
     */
    public static Void handleTwo(ValidatingPrimeSet set) {
        int valueOfDigit = 2;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = SetHandler::handleThree;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                nextFunction.apply(set);
                return null;
            case 1:
                PartitionCombinationResult oneMissingCombinations = PartitionCombinationGenerator.getCombinations(1, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, oneMissingCombinations, nextFunction);
                return null;
            case 2:
                PartitionCombinationResult twoMissingCombination = PartitionCombinationGenerator.getCombinations(2, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, twoMissingCombination, nextFunction);
                return null;
        }
        return null;
    }

    /**
     * Generates the sets which fulfill the requirements for the three.
     *
     * @param set The source set.
     * @return Null, this is a Void function, which is used as parameter.
     */
    public static Void handleThree(ValidatingPrimeSet set) {
        int valueOfDigit = 3;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = SetHandler::handleFour;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                nextFunction.apply(set);
                return null;
            case 1:
                PartitionCombinationResult oneMissingCombinations = PartitionCombinationGenerator.getCombinations(1, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, oneMissingCombinations, nextFunction);
                return null;
            case 2:
                PartitionCombinationResult twoMissingCombination = PartitionCombinationGenerator.getCombinations(2, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, twoMissingCombination, nextFunction);
                return null;
            case 3:
                PartitionCombinationResult threeMissingCombination = PartitionCombinationGenerator.getCombinations(3, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, threeMissingCombination, nextFunction);
                return null;
        }
        return null;
    }

    /**
     * Generates the sets which fulfill the requirements for the four.
     *
     * @param set The source set.
     * @return Null, this is a Void function, which is used as parameter.
     */
    public static Void handleFour(ValidatingPrimeSet set) {
        int valueOfDigit = 4;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = SetHandler::handleFive;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                nextFunction.apply(set);
                return null;
            case 1:
                PartitionCombinationResult oneMissingCombinations = PartitionCombinationGenerator.getCombinations(1, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, oneMissingCombinations, nextFunction);
                return null;
            case 2:
                PartitionCombinationResult twoMissingCombination = PartitionCombinationGenerator.getCombinations(2, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, twoMissingCombination, nextFunction);
                return null;
            case 3:
                PartitionCombinationResult threeMissingCombination = PartitionCombinationGenerator.getCombinations(3, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, threeMissingCombination, nextFunction);
                return null;
            case 4:
                PartitionCombinationResult fourMissingCombination = PartitionCombinationGenerator.getCombinations(4, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fourMissingCombination, nextFunction);
                return null;
        }
        return null;
    }

    /**
     * Generates the sets which fulfill the requirements for the five.
     *
     * @param set The source set.
     * @return Null, this is a Void function, which is used as parameter.
     */
    public static Void handleFive(ValidatingPrimeSet set) {
        int valueOfDigit = 5;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = SetHandler::handleSix;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                nextFunction.apply(set);
                return null;
            case 1:
                PartitionCombinationResult oneMissingCombinations = PartitionCombinationGenerator.getCombinations(1, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, oneMissingCombinations, nextFunction);
                return null;
            case 2:
                PartitionCombinationResult twoMissingCombination = PartitionCombinationGenerator.getCombinations(2, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, twoMissingCombination, nextFunction);
                return null;
            case 3:
                PartitionCombinationResult threeMissingCombination = PartitionCombinationGenerator.getCombinations(3, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, threeMissingCombination, nextFunction);
                return null;
            case 4:
                PartitionCombinationResult fourMissingCombination = PartitionCombinationGenerator.getCombinations(4, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fourMissingCombination, nextFunction);
                return null;
            case 5:
                PartitionCombinationResult fiveMissingCombination = PartitionCombinationGenerator.getCombinations(5, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fiveMissingCombination, nextFunction);
                return null;
        }
        return null;
    }

    /**
     * Generates the sets which fulfill the requirements for the six.
     *
     * @param set The source set.
     * @return Null, this is a Void function, which is used as parameter.
     */
    public static Void handleSix(ValidatingPrimeSet set) {
        int valueOfDigit = 6;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = SetHandler::handleSeven;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                nextFunction.apply(set);
                return null;
            case 1:
                PartitionCombinationResult oneMissing = PartitionCombinationGenerator.getCombinations(1, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, oneMissing, nextFunction);
                return null;
            case 2:
                PartitionCombinationResult twoMissing = PartitionCombinationGenerator.getCombinations(2, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, twoMissing, nextFunction);
                return null;
            case 3:
                PartitionCombinationResult threeMissing = PartitionCombinationGenerator.getCombinations(3, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, threeMissing, nextFunction);
                return null;
            case 4:
                PartitionCombinationResult fourMissing = PartitionCombinationGenerator.getCombinations(4, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fourMissing, nextFunction);
                return null;
            case 5:
                PartitionCombinationResult fiveMissing = PartitionCombinationGenerator.getCombinations(5, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fiveMissing, nextFunction);
                return null;
            case 6:
                PartitionCombinationResult sixMissing = PartitionCombinationGenerator.getCombinations(6, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, sixMissing, nextFunction);
                return null;
        }
        return null;
    }

    /**
     * Generates the sets which fulfill the requirements for the seven.
     *
     * @param set The source set.
     * @return Null, this is a Void function, which is used as parameter.
     */
    public static Void handleSeven(ValidatingPrimeSet set) {
        int valueOfDigit = 7;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = SetHandler::handleEight;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                nextFunction.apply(set);
                return null;
            case 1:
                PartitionCombinationResult oneMissing = PartitionCombinationGenerator.getCombinations(1, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, oneMissing, nextFunction);
                return null;
            case 2:
                PartitionCombinationResult twoMissing = PartitionCombinationGenerator.getCombinations(2, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, twoMissing, nextFunction);
                return null;
            case 3:
                PartitionCombinationResult threeMissing = PartitionCombinationGenerator.getCombinations(3, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, threeMissing, nextFunction);
                return null;
            case 4:
                PartitionCombinationResult fourMissing = PartitionCombinationGenerator.getCombinations(4, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fourMissing, nextFunction);
                return null;
            case 5:
                PartitionCombinationResult fiveMissing = PartitionCombinationGenerator.getCombinations(5, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fiveMissing, nextFunction);
                return null;
            case 6:
                PartitionCombinationResult sixMissing = PartitionCombinationGenerator.getCombinations(6, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, sixMissing, nextFunction);
                return null;
            case 7:
                PartitionCombinationResult sevenMissing = PartitionCombinationGenerator.getCombinations(7, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, sevenMissing, nextFunction);
                return null;
        }
        return null;
    }

    /**
     * Generates the sets which fulfill the requirements for the eight.
     *
     * @param set The source set.
     * @return Null, this is a Void function, which is used as parameter.
     */
    public static Void handleEight(ValidatingPrimeSet set) {
        int valueOfDigit = 8;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = SetHandler::handleNine;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                nextFunction.apply(set);
                return null;
            case 1:
                PartitionCombinationResult oneMissing = PartitionCombinationGenerator.getCombinations(1, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, oneMissing, nextFunction);
                return null;
            case 2:
                PartitionCombinationResult twoMissing = PartitionCombinationGenerator.getCombinations(2, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, twoMissing, nextFunction);
                return null;
            case 3:
                PartitionCombinationResult threeMissing = PartitionCombinationGenerator.getCombinations(3, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, threeMissing, nextFunction);
                return null;
            case 4:
                PartitionCombinationResult fourMissing = PartitionCombinationGenerator.getCombinations(4, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fourMissing, nextFunction);
                return null;
            case 5:
                PartitionCombinationResult fiveMissing = PartitionCombinationGenerator.getCombinations(5, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fiveMissing, nextFunction);
                return null;
            case 6:
                PartitionCombinationResult sixMissing = PartitionCombinationGenerator.getCombinations(6, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, sixMissing, nextFunction);
                return null;
            case 7:
                PartitionCombinationResult sevenMissing = PartitionCombinationGenerator.getCombinations(7, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, sevenMissing, nextFunction);
                return null;
            case 8:
                PartitionCombinationResult eightMissing = PartitionCombinationGenerator.getCombinations(8, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, eightMissing, nextFunction);
                return null;
        }
        return null;
    }

    /**
     * Generates the sets which fulfill the requirements for the nine.
     *
     * @param set The source set.
     * @return Null, this is a Void function, which is used as parameter.
     */
    public static Void handleNine(ValidatingPrimeSet set) {
        int valueOfDigit = 9;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = ResultSetHandler::handlePossibleResult;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                nextFunction.apply(set);
                return null;
            case 1:
                PartitionCombinationResult oneMissing = PartitionCombinationGenerator.getCombinations(1, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, oneMissing, nextFunction);
                return null;
            case 2:
                PartitionCombinationResult twoMissing = PartitionCombinationGenerator.getCombinations(2, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, twoMissing, nextFunction);
                return null;
            case 3:
                PartitionCombinationResult threeMissing = PartitionCombinationGenerator.getCombinations(3, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, threeMissing, nextFunction);
                return null;
            case 4:
                PartitionCombinationResult fourMissing = PartitionCombinationGenerator.getCombinations(4, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fourMissing, nextFunction);
                return null;
            case 5:
                PartitionCombinationResult fiveMissing = PartitionCombinationGenerator.getCombinations(5, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, fiveMissing, nextFunction);
                return null;
            case 6:
                PartitionCombinationResult sixMissing = PartitionCombinationGenerator.getCombinations(6, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, sixMissing, nextFunction);
                return null;
            case 7:
                PartitionCombinationResult sevenMissing = PartitionCombinationGenerator.getCombinations(7, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, sevenMissing, nextFunction);
                return null;
            case 8:
                PartitionCombinationResult eightMissing = PartitionCombinationGenerator.getCombinations(8, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, eightMissing, nextFunction);
                return null;
            case 9:
                PartitionCombinationResult nineMissing = PartitionCombinationGenerator.getCombinations(9, valueOfDigit, set.getPrimes());
                handleCombinationsWithNextFunction(set, nineMissing, nextFunction);
                return null;
        }
        return null;
    }
}
