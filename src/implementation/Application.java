package implementation;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Application {


    private void printNumberCounts(List<Integer> numbers) {

        int[] array = new int[10];
        for (Integer number : numbers) {
            String adjustedValue = number.toString().replace("0", "");
            for (int i = 0; i < adjustedValue.length(); i++) {
                Character c = adjustedValue.charAt(i);
                switch (c) {
                    case ',':
                        continue;
                    case '0':
                        // System.out.println("Validated: False Value: " + value);
                    case '1':
                        array[1] = array[1] + 1;
                        break;
                    case '2':
                        array[2] = array[2] + 1;
                        break;
                    case '3':
                        array[3] = array[3] + 1;
                        break;
                    case '4':
                        array[4] = array[4] + 1;
                        break;
                    case '5':
                        array[5] = array[5] + 1;
                        break;
                    case '6':
                        array[6] = array[6] + 1;
                        break;
                    case '7':
                        array[7] = array[7] + 1;
                        break;
                    case '8':
                        array[8] = array[8] + 1;
                        break;
                    case '9':
                        array[9] = array[9] + 1;
                        break;
                }
            }
        }

        for (int i = 0; i < 10; i++) {
            int digitCount = array[i];
            System.out.println(i + " " + digitCount);
        }

    }


    private int[] getNumbersContainingDigit(List<Integer> source, int digit) {
        ArrayList<Integer> hits = new ArrayList<>();

        for (int entry : source) {
            String value = String.valueOf(entry);
            for (Character c : value.toCharArray()) {
                if (Character.getNumericValue(c) == digit) {
                    hits.add(entry);
                }
            }
        }
        return Helpers.toIntArray(hits.stream().distinct().collect(Collectors.toList()));
    }

    public void run() {

        long runtimeStart = System.currentTimeMillis();

        // Generate the prim numbers.
        ConcurrentPrimeFinder finder = new ConcurrentPrimeFinder();
        List<Integer> p = finder.findPrimes(0, 1000);
        System.out.println("Found primes " + p.size());

        // Filter out all invalid primes e.g. primes containing a zero.
        p = p.stream().filter(t -> Validator.isCandidate(t)).collect(Collectors.toList());
        System.out.println("Filtered valid primes " + p.size());

        // See comment below.
        //this.printNumberCounts(p);

        // Preset some combinations:
        // The method printNumberCounts was used to get the counts of prime numbers for each number from 1 to 9.
        // The result of this small analysis showed, that the numbers eights is relative rare. But the eight must
        // be the second-most number in a valid set. So the combinations of eights get generated before the other
        // numbers are added. This increases the chance to find a valid set.
        int[] primesContainingNine = this.getNumbersContainingDigit(p, 8);
        List<int[]> combs = Combinations.combination(primesContainingNine, 8);
        PrimeCategorizer categorizer = new PrimeCategorizer(Helpers.toIntArray(p));

        // Generate the sets and filter out invalid combinations.
        List<ValidatingPrimeSet> sets = new ArrayList<>();
        for (int[] preGeneratedValue : combs) {
            ValidatingPrimeSet set = new ValidatingPrimeSet();
            boolean isValid = Arrays.stream(preGeneratedValue).allMatch(prime -> set.addEntry(prime));
            if (isValid) {
                sets.add(set);
            }
        }

        ConcurrentPrimeCombinationFinder runner = new ConcurrentPrimeCombinationFinder(categorizer);
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


        System.out.println("Found minimum: " + ConcurrentPrimeCombinationFinder.globalMinimumSum.get() + " Set: "
                + ConcurrentPrimeCombinationFinder.globalMinimumSet.get());

        System.out.println("Total runtime (ms)   : " + (System.currentTimeMillis() - runtimeStart));
    }


}
