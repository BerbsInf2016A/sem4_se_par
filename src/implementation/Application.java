package implementation;


import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        System.out.println("Found primes "  +  p.size());
        p = p.stream().filter(t -> Validator.isCandidate(t)).collect(Collectors.toList());
        BigInteger maxValue = new BigInteger("2").pow(p.size());
        System.out.println("Filtered valid primes "  +  p.size());
        System.out.printf("There are %s^%s possible combinations: %s %n", 2, p.size(), maxValue);

        Long[] primes = p.toArray(new Long[p.size()]);

        PrimeCategorizer categorizer = new PrimeCategorizer(p);

        ConcurrentPrimeCombinationFinder runner = new ConcurrentPrimeCombinationFinder(categorizer);
        runner.run();
        System.out.println("Strings: " + ConcurrentPrimeCombinationFinder.strings);
        Set<String> duplicates = runner.findDuplicates(ConcurrentPrimeCombinationFinder.strings);
        System.out.println("Duplicate strings: " + duplicates.size());
        int z = 4;


/*


        int oneDigitLongPrimesCount = (int) p.stream().filter(t -> countDigits(t) == 1).count();
        int twoDigitLongPrimesCount = (int) p.stream().filter(t -> countDigits(t) == 2).count();
        int threeDigitLongPrimesCount = (int) p.stream().filter(t -> countDigits(t) == 3).count();

        int numberOnNeededDigits = 45;
        // TODO: Fix maxPrimeLength
        Long maxPrimeLength = p.stream().max(Comparator.comparingInt(value -> value.toString().length())).get();
        int minCombinationLength = numberOnNeededDigits / 3;
        int maxCombinationLength = numberOnNeededDigits - oneDigitLongPrimesCount - twoDigitLongPrimesCount;


        ConcurrentPrimeCombinationChecker checker = new ConcurrentPrimeCombinationChecker();
        //checker.analyzeCombinations(BigInteger.ZERO,maxValue, minCombinationLength, maxCombinationLength, primes);
        checker.findValidCombinations(minCombinationLength, maxCombinationLength,primes, maxValue);

/*
        BigInteger maxTestValue = new BigInteger("2").pow(25);

        // Checking the two versions:
        long runtimeStartFirst = System.currentTimeMillis();
        checker.analyzeCombinations(BigInteger.ONE,maxTestValue, minCombinationLength, maxCombinationLength, primes);
        System.out.println("First runtime (ms)   : " + (System.currentTimeMillis() - runtimeStartFirst));
        long runtimeStartSecond = System.currentTimeMillis();
        checker.analyzeCombinations2(BigInteger.ONE,maxTestValue, minCombinationLength, maxCombinationLength, primes);
        System.out.println("Second runtime (ms)   : " + (System.currentTimeMillis() - runtimeStartSecond));

        int index_5 = Arrays.asList(primes).indexOf(new Long(5));
        int index_7 = Arrays.asList(primes).indexOf(new Long(7));
        int index_29 = Arrays.asList(primes).indexOf(new Long(29));
        int index_47 = Arrays.asList(primes).indexOf(new Long(47));
        int index_59 = Arrays.asList(primes).indexOf(new Long(59));
        int index_61 = Arrays.asList(primes).indexOf(new Long(61));
        int index_67 = Arrays.asList(primes).indexOf(new Long(67));
        int index_79 = Arrays.asList(primes).indexOf(new Long(79));
        int index_83 = Arrays.asList(primes).indexOf(new Long(83));
        int index_89 = Arrays.asList(primes).indexOf(new Long(89));
        int index_269 = Arrays.asList(primes).indexOf(new Long(269));
        int index_463 = Arrays.asList(primes).indexOf(new Long(463));
        int index_467 = Arrays.asList(primes).indexOf(new Long(467));
        int index_487 = Arrays.asList(primes).indexOf(new Long(487));
        int index_569 = Arrays.asList(primes).indexOf(new Long(569));
        int index_599 = Arrays.asList(primes).indexOf(new Long(599));
        int index_859 = Arrays.asList(primes).indexOf(new Long(859));
        int index_883 = Arrays.asList(primes).indexOf(new Long(883));
        int index_887 = Arrays.asList(primes).indexOf(new Long(887));
        BitSet set = new BitSet(p.size());

        List<Integer> indexList = Arrays.asList(index_5, index_7, index_29, index_47, index_59, index_61, index_67, index_79, index_83, index_89,
                index_269, index_463, index_467, index_487, index_569, index_599, index_859, index_883, index_887);

        for (Integer index : indexList ) {
            set.set(index);
        }
        BigInteger bi = new BigInteger(set.toByteArray());
*/

        System.out.println("runtime (ms)   : " + (System.currentTimeMillis() - runtimeStart));
    }
}
