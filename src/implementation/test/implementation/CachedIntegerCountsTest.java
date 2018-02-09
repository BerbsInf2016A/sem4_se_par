package implementation;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class CachedIntegerCountsTest {
    @Test
    public void countCharsInInteger()  {
        Map<Integer, Integer> countsMap = CachedIntegerCounts.countCharsInInteger(756912266);
        Map<Integer, Integer> expected = new HashMap<>();
        expected.put(7,1);
        expected.put(5,1);
        expected.put(6,3);
        expected.put(9,1);
        expected.put(1,1);
        expected.put(2,2);

        assertThat("The correct count should be returned", countsMap, is(expected));

        Map<Integer, Integer> secondCallMap = CachedIntegerCounts.countCharsInInteger(756912266);
        assertThat("The correct count should be returned", secondCallMap, is(expected));
    }

}