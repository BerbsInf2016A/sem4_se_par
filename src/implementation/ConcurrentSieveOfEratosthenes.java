package implementation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ConcurrentSieveOfEratosthenes {
    public List<Long> sieve( Long minimunValue, Long maximumValue) {
        List<Long> primes = new LinkedList<>();

        try {
            final List<Callable<List<Long>>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);
            /*
            Long sliceSize = maximumValue / 4;
            if(sliceSize > Integer.MAX_VALUE) {
                sliceSize = new Long ((Integer.MAX_VALUE % 2 )  - 1);
            }
*/
            Long sliceSize = new Long(Integer.MAX_VALUE / 2);
            for (long i = minimunValue;i <= maximumValue;i+=sliceSize) {
                final Long from = i;
                Long to = i + sliceSize;
                if (to > maximumValue)
                    to = maximumValue;
                final Long end = to;
                partitions.add(new Callable<List<Long>>() {
                    public List<Long> call() {
                        return sieveInRange(from,end);
                    }
                });
            }

            final List<Future<List<Long>>> resultFromParts = executorPool.invokeAll(partitions,10000,TimeUnit.SECONDS);
            executorPool.shutdown();


            for (final Future<List<Long>> result : resultFromParts)
                primes.addAll(result.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return primes;
    }
    
    private List<Long> sieveInRange(Long minimumValue,Long maximumValue) {
        System.out.println("Min: " + minimumValue + " Max: " + maximumValue  );
        int size = (int) (maximumValue - minimumValue + 1) / 2;
        boolean[] isPrime = new boolean[size];


        for (int i = 0;i < size;i++)
            isPrime[i] = true;

        for (int i = 3;i * i <= maximumValue;i+= 2) {
            if (i >= 9 && i % 3 == 0)
                continue;
            if (i >= 25 && i % 5 == 0)
                continue;
            if (i >= 49 && i % 7 == 0)
                continue;

            long first = ((minimumValue + i -1) / i) * i;

            if (first < i * i)
                first = i * i;

            if (first % 2 == 0)
                first+= i;

            for (long j = first;j <= maximumValue;j+= i * 2){
                Long l = (j - minimumValue) / 2;
                if(i > size) {
                    int g = 0;
                }
                isPrime[l.intValue()] = false;
            }

        }

        int primeNumberOffset = 1;
        List<Long> primes = new ArrayList<>();

        for (int i = 0;i < size;i++)
            if (isPrime[i]) {
                Long value = new Long(minimumValue + primeNumberOffset + (i*2));
                if(Validator.isCandidate(value)) {
                    primes.add(value);
                }
            }

        if(minimumValue == 2) {
            primes.add(2L);
        }


        return primes;
    }
}