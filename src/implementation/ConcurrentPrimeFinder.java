package implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ConcurrentPrimeFinder {
    public List<Long> findPrimes(long min, long max) {
        List<Long> primes = new ArrayList<>();

        try {
            final List<Callable<List<Long>>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);

            Long sliceSize = max / Configuration.instance.maximumNumberOfThreads;
            System.out.format("Using %d threads. SliceSize: %d \n", Configuration.instance.maximumNumberOfThreads, sliceSize);


            for (long i = min; i <= max; i += sliceSize) {
                final Long from = i;
                Long to = i + sliceSize;
                if (to > max)
                    to = max;
                final Long end = to;
                partitions.add(() -> findPrimesInRange(from, end));
            }

            final List<Future<List<Long>>> resultFromParts = executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);
            executorPool.shutdown();


            for (final Future<List<Long>> result : resultFromParts)
                primes.addAll(result.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return primes;
    }

    private List<Long> findPrimesInRange(Long from, Long end) {
        List<Long> primes = new ArrayList<>();

        for (long i = from; i <= end; i++){
            if(i == 2) primes.add(i);
            if(i == 1) continue;

            if(isPrime(i)) {
                primes.add(i);
            }
        }

        return primes;
    }

    // Copied from https://www.mkyong.com/java/how-to-determine-a-prime-number-in-java/
    boolean isPrime(long n) {
        //check if n is a multiple of 2
        if (n%2==0) return false;
        //if not, then just check the odds
        for(int i=3;i*i<=n;i+=2) {
            if(n%i==0)
                return false;
        }
        return true;
    }
}
