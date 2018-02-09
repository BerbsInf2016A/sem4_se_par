package implementation;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Application {


    /**
     * Run the application.
     */
    public void run() {

        long runtimeStart = System.currentTimeMillis();

        // Generate the prim numbers.
        ConcurrentPrimeFilter finder = new ConcurrentPrimeFilter();
        List<Integer> p = finder.filterPrimes(0, 1000);
        System.out.println("Found primes " + p.size());

        // Filter out all invalid primes e.g. primes containing a zero.
        p = p.stream().filter(t -> Validator.isUsablePrimeCandidate(t)).collect(Collectors.toList());
        System.out.println("Filtered valid primes " + p.size());

        // See comment below.
        //Helpers.printNumberCounts(p);

        // Preset some combinations:
        // The method printNumberCounts was used to get the counts of prime numbers for each number from 1 to 9.
        // The result of this small analysis showed, that the numbers eights is relative rare. But the eight must
        // be the second-most number in a valid set. So the combinations of eights get generated before the other
        // numbers are added. This increases the chance to find a valid set.
        int[] primesContainingEight = Helpers.getNumbersContainingDigit(p, 8);
        List<int[]> combs = Combinations.combination(primesContainingEight, 8);
        CachedPrimeCategories categories = new CachedPrimeCategories(Helpers.toIntArray(p));

        // Generate the sets and filter out invalid combinations.
        List<ValidatingPrimeSet> sets = new ArrayList<>();
        for (int[] preGeneratedValue : combs) {
            ValidatingPrimeSet set = new ValidatingPrimeSet();
            boolean isValid = true;
            for (int prime : preGeneratedValue) {
                if (!set.tryToAddEntry(prime)) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                sets.add(set);
            }
        }

        PartitionCombinationGenerator.setCategories(categories);
        ConcurrentPrimeCombinationFinder runner = new ConcurrentPrimeCombinationFinder();
        try {
            runner.run(sets);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Wait 3 seconds after the execution and the probable interruption of threads, before the result is
        // printed to the console. This trick is used, to enhance the chance, that the final result message will be
        // the last printed message on the console..
        try {

            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("Found minimum: " + ResultSetHandler.globalMinimumSum.get() + " Set: "
                + ResultSetHandler.globalMinimumSet.get());

        System.out.println("Total runtime (ms)   : " + (System.currentTimeMillis() - runtimeStart));
    }


}
