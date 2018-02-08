package implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Helpers {
    /**
     * Converting a list of Integers to an int array.
     *
     * @param list The list of Integers, which should be converted.
     * @return The int array.
     */
    public static int[] toIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        for (int i = 0; i < ret.length; i++)
            ret[i] = list.get(i);
        return ret;
    }

    /**
     * Get all integers in a list, which contain the digit.
     *
     * @param source The source list containing the integers to scan.
     * @param digit  The digit to search for.
     * @return An int array containing all integers, containing the digit.
     */
    public static int[] getNumbersContainingDigit(List<Integer> source, int digit) {
        ArrayList<Integer> hits = new ArrayList<>();

        for (int entry : source) {
            String value = String.valueOf(entry);
            for (Character c : value.toCharArray()) {
                if (Character.getNumericValue(c) == digit) {
                    hits.add(entry);
                    break;
                }
            }
        }
        return Helpers.toIntArray(hits.stream().distinct().collect(Collectors.toList()));
    }

    /**
     * Prints the count of numbers.
     *
     * @param numbers The integers to analyze.
     */
    public static void printNumberCounts(List<Integer> numbers) {

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
}
