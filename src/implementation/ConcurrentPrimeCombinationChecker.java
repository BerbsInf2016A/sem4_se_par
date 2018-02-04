package implementation;

import jdk.nashorn.internal.ir.IfNode;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ConcurrentPrimeCombinationChecker
{
    public final LinkedBlockingQueue<Long[]> results = new LinkedBlockingQueue<Long[]>();
    public final LinkedBlockingQueue<ValidatingPrimeSet> resultSets = new LinkedBlockingQueue<ValidatingPrimeSet>();

    private List<PartitionSizes> getPartitions(BigInteger globalMinimum, BigInteger globalMaximum, int offset, int numberOfPartitions, BigInteger partitionSize ){
        List<PartitionSizes> partitions = new ArrayList<>();
        BigInteger sliceSize = globalMaximum.divide(BigInteger.valueOf(new Long(Configuration.instance.maximumNumberOfThreads)));
        if (sliceSize.compareTo(partitionSize) > 0){
            sliceSize = partitionSize;
        }
        int counter = 0;
        for( BigInteger bi = BigInteger.valueOf(0);
             bi.compareTo(globalMaximum) <= 0;
             bi = bi.add(sliceSize)) {
            BigInteger from = bi;
            BigInteger to = bi.add(sliceSize);
            if (to.compareTo(globalMaximum) == 1)
                to = globalMaximum;
            final BigInteger end = to;
            System.out.println("from " + from + " to " + end);
            if (counter >= offset) {
                partitions.add(new PartitionSizes(from, to));
            }
            counter++;
            if(counter == numberOfPartitions + offset){
                break;
            }
        }
        return partitions;
    }

    public List<Long> findValidCombinations(int minCombinationLength, int maxCombinationLength, Long[] primes, BigInteger maxValue) {
        List<Long>  foundValidCombinations =  new ArrayList<>();
        List<PartitionSizes> sizes = new ArrayList<>();


        try {
            final List<Callable<List<Long>>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);

            BigInteger sliceSize = maxValue.divide(BigInteger.valueOf(new Long(Configuration.instance.maximumNumberOfThreads)));
            //int sliceSize = Integer.MAX_VALUE;
            System.out.format("Using %d threads. SliceSize: %d \n", Configuration.instance.maximumNumberOfThreads, sliceSize);
            sizes = this.getPartitions(BigInteger.ZERO, maxValue, 0, Configuration.instance.maximumNumberOfThreads, new BigInteger("1000000000000000000000000000000000000000"));
            for (PartitionSizes size : sizes ) {
                partitions.add(() -> analyzeCombinationsEarlyLeave(size.from, size.to, minCombinationLength, maxCombinationLength, primes));
            }

/*
            //BigInteger increment = BigInteger.valueOf(sliceSize);
            for( BigInteger bi = BigInteger.valueOf(0);
                    bi.compareTo(maxValue) <= 0;
                    bi = bi.add(sliceSize)) {
                BigInteger from = bi;
                BigInteger to = bi.add(sliceSize);
                if (to.compareTo(maxValue) == 1)
                    to = maxValue;
                final BigInteger end = to;
                System.out.println("from " + from + " to " + end);
                partitions.add(() -> analyzeCombinations(from, end, minCombinationLength, maxCombinationLength, primes));
            }
*/
            final List<Future<List<Long>>> resultFromParts = executorPool.invokeAll(partitions, 900, TimeUnit.SECONDS);
            executorPool.shutdown();


            for (final Future<List<Long>> result : resultFromParts)
                foundValidCombinations.addAll(result.get());
        } catch (Exception e) {
            // TODO: Want a result.
            //throw new RuntimeException(e);
        }
        return foundValidCombinations;
    }

    public List<Long> analyzeCombinations(BigInteger from, BigInteger end, int minCombinationLength, int maxCombinationLength, Long[] primes) {
        // TODO Fix return value

        int counter = 0;
        List<Long> list = new ArrayList<>();
        BitSet bitSet;
        Long[] currentPrimes = new Long[maxCombinationLength];
        int currentPrimesIndex = 0;
        boolean isValid = false;
        StringJoiner sj = new StringJoiner("");
        for( BigInteger bi = from;
             bi.compareTo(end) <= 0;
             bi = bi.add(BigInteger.ONE)) {

            bitSet = BitSet.valueOf(bi.toByteArray());
            //System.out.println(bi);
            counter++;

            int countOfSetBits = bitSet.cardinality();
            //System.out.println(countOfSetBits);
            if (countOfSetBits < minCombinationLength || countOfSetBits > maxCombinationLength) continue;
            Arrays.fill(currentPrimes, new Long(0));
            isValid = false;
            currentPrimesIndex = 0;

            for (int i = bitSet.nextSetBit(0); i != -1; i = bitSet.nextSetBit(i + 1)) {
                currentPrimes[currentPrimesIndex] = primes[i];
                currentPrimesIndex++;
            }


            isValid = Validator.isValid(Arrays.stream(currentPrimes).map(i -> i.toString()).collect(Collectors.joining(",")));


            if (isValid) {
                results.add(currentPrimes);
            }
        }
        System.out.println("First: " + counter);
        return list;
    }


    public List<Long> analyzeCombinationsEarlyLeave(BigInteger from, BigInteger end, int minCombinationLength, int maxCombinationLength, Long[] primes) {
        // TODO Fix return value

        int debugCounter = -1;
        List<Long> list = new ArrayList<>();
        boolean isValid = true;
        BigInteger currentBigInt;
        if (!(from == BigInteger.ZERO)){
            currentBigInt = from.subtract(BigInteger.ONE);
        } else {
            currentBigInt = from;
        }

        ValidatingPrimeSet primeSet = new ValidatingPrimeSet(maxCombinationLength);
        while ( true ) {
            debugCounter++;

            currentBigInt = currentBigInt.add(BigInteger.ONE);
            if (debugCounter == 10000000){
                System.out.println(currentBigInt);
                debugCounter = 0;
            }
            if (currentBigInt.compareTo(end) >= 0) { break; }

            BitSet currentBitSet = BitSet.valueOf(currentBigInt.toByteArray());

            int countOfSetBits = currentBitSet.cardinality();
            if (  countOfSetBits > maxCombinationLength || countOfSetBits < minCombinationLength ) continue;

            primeSet.clear();

            for (int i = currentBitSet.nextSetBit(0); i != -1; i = currentBitSet.nextSetBit(i + 1)) {
                if (! primeSet.addEntry(primes[i])) {
                    isValid = false;
                    break;
                }

            }

            if (isValid) {
                resultSets.add(primeSet);
            }
        }
        return list;
    }
}
