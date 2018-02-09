package implementation;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A handler for possible results.
 */
class ResultSetHandler {

    /**
     * The atomic variables are used to gather information on the current run.
     */
    public static final AtomicInteger globalValidSetsCounter = new AtomicInteger();
    public static final AtomicInteger globalMinimumSum = new AtomicInteger();
    public static final AtomicReference<String> globalMinimumSet = new AtomicReference<>();

    /**
     * Handles a possible result.
     *
     * @param set The set to analyze.
     * @return Null, this is a Void function, which is used as parameter.
     */
    public static Void handlePossibleResult(ValidatingPrimeSet set) {
        boolean isValid = Validator.validateFinalSet(set);
        if (isValid) {
            calculateResultAndPrintSet(set);
        }
        return null;
    }

    /**
     * Calculates the sum of a set and prints the information (dependent on the current configuration).
     *
     * @param set The set of primes.
     */
    private static void calculateResultAndPrintSet(ValidatingPrimeSet set) {
        int counter = globalValidSetsCounter.incrementAndGet();
        if (counter % Configuration.instance.printFoundSetsCounterInterval == 0) {
            System.out.println("Found valid sets: " + counter);
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
    }
}
