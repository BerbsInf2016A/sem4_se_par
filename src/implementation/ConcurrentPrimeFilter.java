package implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A concurrent prime finder.
 */
class ConcurrentPrimeFilter {
    /**
     * A method to check, if a long is a prime.
     *
     * @param n The value to check.
     * @return True, if a prime, false if not.
     */
    // Copied from https://www.mkyong.com/java/how-to-determine-a-prime-number-in-java/
    public static boolean isPrime(long n) {
        //check if n is a multiple of 2
        if (n % 2 == 0) return false;
        //if not, then just check the odds
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0)
                return false;
        }
        return true;
    }

    /**
     * Filter the prime integers in the range from min to max.
     *
     * @param min The lower limit.
     * @param max The upper limit.
     * @return The found primes.
     */
    public List<Integer> filterPrimes(int min, int max) {
        List<Integer> primes = new ArrayList<>();

        try {
            final List<Callable<List<Integer>>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);

            Integer sliceSize = max / Configuration.instance.maximumNumberOfThreads;
            System.out.format("Using %d threads for the prime filter. SliceSize: %d \n", Configuration.instance.maximumNumberOfThreads, sliceSize);


            for (int i = min; i <= max; i += sliceSize) {
                final Integer from = i;
                Integer to = i + sliceSize;
                if (to > max)
                    to = max;
                final Integer end = to;
                partitions.add(() -> findPrimesInRange(from, end));
            }

            final List<Future<List<Integer>>> resultFromParts = executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);
            executorPool.shutdown();


            for (final Future<List<Integer>> result : resultFromParts)
                primes.addAll(result.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return primes.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Find the primes in a given range.
     *
     * @param from The lower limit of the range.
     * @param end  The upper limit of the range.
     * @return A list of found primes.
     */
    private List<Integer> findPrimesInRange(Integer from, Integer end) {
        List<Integer> primes = new ArrayList<>();

        for (int i = from; i <= end; i++) {
            if (i == 2) primes.add(i);
            if (i == 1) continue;

            if (isPrime(i)) {
                primes.add(i);
            }
        }

        return primes;
    }
}
