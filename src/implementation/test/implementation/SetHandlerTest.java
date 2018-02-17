package implementation;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class SetHandlerTest {
    @Test
    public void validFinishedSet() {
        int[] preSetValues = {89, 281, 283, 787, 853, 857, 859, 863, 467, 499, 449, 569, 659, 67, 769, 79};
        ValidatingPrimeSet validSet = new ValidatingPrimeSet();
        for (int value : preSetValues) {
            validSet.tryToAddEntry(value);
        }
        SetHandler.handleOne(validSet);

        Assert.assertEquals("Should be one valid set", 1, ResultSetHandler.globalValidSetsCounter.get());
    }

    @Test
    public void validUnfinishedSet() {
        ResultSetHandler.globalValidSetsCounter.set(0);

        int[] preSetValues = {89, 281, 283, 787, 853, 857, 859, 863, 467, 499, 449, 569, 659, 67, 769};

        ConcurrentPrimeFilter finder = new ConcurrentPrimeFilter();
        List<Integer> p = finder.filterPrimes(0, 1000);
        p = p.stream().filter(t -> Validator.isUsablePrimeCandidate(t)).collect(Collectors.toList());

        CachedPrimeCategories categories = new CachedPrimeCategories(Helpers.toIntArray(p));
        PartitionCombinationGenerator.setCategories(categories);

        ValidatingPrimeSet validSet = new ValidatingPrimeSet();
        for (int value : preSetValues) {
            validSet.tryToAddEntry(value);
        }
        SetHandler.handleOne(validSet);

        assertEquals("Should be one valid set", 2, ResultSetHandler.globalValidSetsCounter.get());
    }

}