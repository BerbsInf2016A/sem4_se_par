package implementation;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Application {
    public static int countDigits(long number) {
        number = Math.abs(number);

        int count = 0;

        if(number<10) {
            count++;
        }
        else {
            count += countDigits(number/10) + 1;
        }

        return count;
    }

    public void run() {

        long runtimeStart = System.currentTimeMillis();

        // Generate
        ConcurrentPrimeFinder finder = new ConcurrentPrimeFinder();
        List<Long> p = finder.findPrimes(0, 1000);
        System.out.println("elements found primes "  +  p.size());
        p = p.stream().filter(t -> Validator.isCandidate(t)).collect(Collectors.toList());
        System.out.println("elements found primes "  +  p.size());
        System.out.println("runtime (ms)   : " + (System.currentTimeMillis() - runtimeStart));

        p = p.stream().filter(t -> Validator.isCandidate(t)).collect(Collectors.toList());

        List<Long> p1 = p.stream().filter(t -> countDigits(t) == 3).collect(Collectors.toList());
        List<Long> p2 = p.stream().filter(t -> countDigits(t) == 3).collect(Collectors.toList());
        List<Long> p3 = p.stream().filter(t -> countDigits(t) == 3).collect(Collectors.toList());

        System.out.println("elements found primes3 "  +  p3.size());

        long runtimeStart3 = System.currentTimeMillis();
        List<List<Long>> comb3 =  Combinations.combination(p3, 5);
        System.out.println("runtime (ms)   : " + (System.currentTimeMillis() - runtimeStart) + " and found " + comb3.size() + " combinations");

/*
        List<List<Long>> comb =  new ArrayList<>();
        for (int i = 2; i <=24; i++ ){
            System.out.println(i);
            comb.addAll(Combinations.combination(p, i));
            System.out.println(comb.size());
        }

*/




        System.out.println("elements found reduced primes "  +  p.size());
        //System.out.println("combinations found primes "  +  comb.size());


        System.out.println("runtime (ms)   : " + (System.currentTimeMillis() - runtimeStart));


        // Parallel: Filter invalid PrimeNumbers

        // Build Combination

        // Parallel: Check and Sum
    }
}
