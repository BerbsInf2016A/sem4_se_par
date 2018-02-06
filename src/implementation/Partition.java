package implementation;

import java.util.ArrayList;

public class Partition {
    public static ArrayList<ArrayList<Integer>> partition(int n) {
        ArrayList<String> result = new ArrayList<>();
        partition(n, n, "", result);
        ArrayList<ArrayList<Integer>> intResult = new ArrayList<>();
        for (String entry : result ) {
            String[] numberStrings =  entry.split(" ");
            ArrayList<Integer> numbers = new ArrayList<>();
            for (String numberString : numberStrings ) {
                if(numberString.length() == 0) continue;
                numbers.add(Integer.parseInt(numberString));
            }
            intResult.add(numbers);
        }
        return intResult;
    }
    public static void partition(int n, int max, String prefix, ArrayList<String> result) {

        if (n == 0) {
            result.add(prefix);
            System.out.println(prefix);
            return;
        }

        for (int i = Math.min(max, n); i >= 1; i--) {
            partition(n-i, i, prefix + " " + i, result);
        }
    }
}
