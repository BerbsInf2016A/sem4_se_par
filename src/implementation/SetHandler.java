package implementation;

import java.util.concurrent.CancellationException;
import java.util.function.Function;

public class SetHandler {


    private static boolean preCheckConditions(ValidatingPrimeSet set, int i) {
        if (Thread.interrupted()) {
            // Executor has probably asked us to stop
            System.out.println(Thread.currentThread().toString() + " has been interrupted!");
            throw new CancellationException("Thread has been requested to stop");
        }
        return set.countReached(i - 1);
    }

    private static void handleCombinationsWithNextFunction(ValidatingPrimeSet set, PartitionCombinationResult combinations, Function<ValidatingPrimeSet, Void> nextFunction) {
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


    public static void handleOne(ValidatingPrimeSet set) {
        int valueOfDigit = 1;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                handleTwo(set);
                return;
            case 1:
                int[] oneMissingCombinations = PartitionCombinationGenerator.getCategories().getBucketForCharacterAndCharacterCount(1, 1);
                HandleMissingOne(set, oneMissingCombinations);
                return;
        }
    }

    private static void HandleMissingOne(ValidatingPrimeSet set, int[] oneMissingCombinations) {
        for (int entry : oneMissingCombinations) {
            ValidatingPrimeSet newSet = new ValidatingPrimeSet(set);
            if (newSet.tryToAddEntry(entry)) {
                handleTwo(newSet);
            }
        }
    }

    public static Void handleTwo(ValidatingPrimeSet set) {
        int valueOfDigit = 2;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = SetHandler::handleThree;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                handleThree(set);
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

    public static Void handleThree(ValidatingPrimeSet set) {
        int valueOfDigit = 3;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = SetHandler::handleFour;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                handleFour(set);
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

    public static Void handleFour(ValidatingPrimeSet set) {
        int valueOfDigit = 4;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = SetHandler::handleFive;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                handleFive(set);
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

    public static Void handleFive(ValidatingPrimeSet set) {
        int valueOfDigit = 5;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = SetHandler::handleSix;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                handleSix(set);
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

    public static Void handleSix(ValidatingPrimeSet set) {
        int valueOfDigit = 6;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = SetHandler::handleSeven;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                handleSeven(set);
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

    public static Void handleSeven(ValidatingPrimeSet set) {
        int valueOfDigit = 7;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = SetHandler::handleEight;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                handleEight(set);
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

    public static Void handleEight(ValidatingPrimeSet set) {
        int valueOfDigit = 8;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = SetHandler::handleNine;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                handleNine(set);
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

    public static Void handleNine(ValidatingPrimeSet set) {
        int valueOfDigit = 9;
        if (!preCheckConditions(set, valueOfDigit)) return null;
        Function<ValidatingPrimeSet, Void> nextFunction = ResultSetHandler::handlePossibleResult;
        int missing = set.countOfMissingOccurrences(valueOfDigit);
        switch (missing) {
            case 0:
                ResultSetHandler.handlePossibleResult(set);
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
