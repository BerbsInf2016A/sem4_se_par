package implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class ConcurrentPrimeCombinationFinder {


    public void run(List<ValidatingPrimeSet> sets) throws RuntimeException, InterruptedException {
        // Just get a chance to connect with visual vm
        try {
            Thread.sleep(Configuration.instance.startDelay);
            this.runConcurrent(sets);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public void runConcurrent(List<ValidatingPrimeSet> sets) {
        try {

            final List<Callable<Boolean>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);

            int sliceSize = sets.size() / Configuration.instance.maximumNumberOfThreads;
            System.out.format("Using %d threads. SliceSize: %d \n", Configuration.instance.maximumNumberOfThreads, sliceSize);


            for (int i = 0; i <= sets.size(); i += sliceSize) {
                final int from = i;
                int to = i + sliceSize;
                if (to > sets.size())
                    to = sets.size();
                final int end = to;
                List<ValidatingPrimeSet> sublist = sets.subList(from, end);
                partitions.add(() -> generateSets(sublist));
            }

            final List<Future<Boolean>> resultFromParts = executorPool.invokeAll(partitions, Configuration.instance.maxTimeOutInSeconds, TimeUnit.SECONDS);
            // Shutdown will not kill the spawned threads, but shutdownNow will set a flag which can be queried in the running
            // threads to end the execution.
            //executorPool.shutdown();
            executorPool.shutdownNow();

            for (final Future<Boolean> result : resultFromParts)
                result.get();

        } catch (CancellationException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean generateSets(List<ValidatingPrimeSet> sets) {
        for (ValidatingPrimeSet entry : sets) {
            ValidatingPrimeSet set = new ValidatingPrimeSet(entry);
            SetHandler.handleOne(set);
            if (Thread.interrupted()) return false;
        }
        return true;
    }


}
