package implementation;


import java.util.List;

public class Application {
    public void run() {

        long runtimeStart = System.currentTimeMillis();

        // Generate
        ConcurrentSieveOfEratosthenes sieve = new ConcurrentSieveOfEratosthenes();

        List<Long> primes  = sieve.sieve(9000000000L, 9999999999L);
        System.out.println("elements found primes "  +  primes.size());


        System.out.println("runtime (ms)   : " + (System.currentTimeMillis() - runtimeStart));


        // Parallel: Filter invalid PrimeNumbers

        // Build Combination

        // Parallel: Check and Sum
    }
}
