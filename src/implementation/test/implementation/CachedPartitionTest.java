package implementation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CachedPartitionTest {
    @Test
    public void getPartition() {
        ArrayList<ArrayList<Integer>> partitions = CachedPartition.partition(4);

        assertEquals("Should contain four partitions", 5, partitions.size());

        // Partition 4:
        List<Integer> firstPartition = Arrays.asList(4);
        // Partition 3 + 1:
        List<Integer> secondPartition = Arrays.asList(3, 1);
        // Partition 2 + 2:
        List<Integer> thirdPartition = Arrays.asList(2, 2);
        // Partition 2 + 1 + 1:
        List<Integer> fourthPartition = Arrays.asList(2, 1, 1);
        // Partition 1 + 1 + 1 + 1:
        List<Integer> fifthPartition = Arrays.asList(1, 1, 1, 1);
        List<List<Integer>> expectedPartitions = new ArrayList<>();
        expectedPartitions.add(firstPartition);
        expectedPartitions.add(secondPartition);
        expectedPartitions.add(thirdPartition);
        expectedPartitions.add(fourthPartition);
        expectedPartitions.add(fifthPartition);

        for (List<Integer> expected : expectedPartitions) {
            assertTrue("Should contain all partitions", partitions.contains(expected));
        }
    }

    @Test
    public void getPartition_MultipleTimes() {
        ArrayList<ArrayList<Integer>> partitions = CachedPartition.partition(4);

        assertEquals("Should contain four partitions", 5, partitions.size());

        // Partition 4:
        List<Integer> firstPartition = Arrays.asList(4);
        // Partition 3 + 1:
        List<Integer> secondPartition = Arrays.asList(3, 1);
        // Partition 2 + 2:
        List<Integer> thirdPartition = Arrays.asList(2, 2);
        // Partition 2 + 1 + 1:
        List<Integer> fourthPartition = Arrays.asList(2, 1, 1);
        // Partition 1 + 1 + 1 + 1:
        List<Integer> fifthPartition = Arrays.asList(1, 1, 1, 1);
        List<List<Integer>> expectedPartitions = new ArrayList<>();
        expectedPartitions.add(firstPartition);
        expectedPartitions.add(secondPartition);
        expectedPartitions.add(thirdPartition);
        expectedPartitions.add(fourthPartition);
        expectedPartitions.add(fifthPartition);

        for (List<Integer> expected : expectedPartitions) {
            assertTrue("Should contain all partitions", partitions.contains(expected));
        }

        ArrayList<ArrayList<Integer>> secondPartitions = CachedPartition.partition(4);
        for (List<Integer> expected : expectedPartitions) {
            assertTrue("Should contain all partitions", secondPartitions.contains(expected));
        }
    }

}