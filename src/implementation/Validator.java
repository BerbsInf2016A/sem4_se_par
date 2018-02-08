package implementation;

import java.util.Arrays;

public class Validator {

    public static boolean isCandidate(Integer value) {
        String str = value.toString();
        int[] array = new int[10];
        for (int i = 0; i < str.length(); i++) {
            Character c = str.charAt(i);
            switch (c) {
                case '0':
                    // System.out.println("Validated: False Value: " + value);
                    return false;
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
        for (int i = 0; i < 10; i++) {
            int digitCount = array[i];
            if (digitCount > i) {
                //System.out.println("Validated: False Value: " + value);
                return false;
            }
        }
        // System.out.println("Validated: True Value: " + value);
        return true;
    }

    public static boolean isValid(String value) {

        String adjustedValue = value.replace("0", "");
        int[] array = new int[10];
        for (int i = 0; i < adjustedValue.length(); i++) {
            Character c = adjustedValue.charAt(i);
            switch (c) {
                case ',':
                    continue;
                case '0':
                    // System.out.println("Validated: False Value: " + value);
                    return false;
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
        for (int i = 0; i < 10; i++) {
            int digitCount = array[i];
            if (digitCount > i) {
                //System.out.println("Validated: False Value: " + adjustedValue);
                return false;
            }
        }
        System.out.println("Validated: True Value: " + adjustedValue);
        return true;
    }

    public static boolean validateFinalSet(ValidatingPrimeSet set) {
        int[] counts = new int[10];
        int[] primes = Arrays.stream(set.getPrimes()).filter(t -> t != 0).toArray();
        for (int prime : primes) {
            String primeString = String.valueOf(prime);
            for (int i = 0; i < String.valueOf(prime).length(); i++) {
                Character c = primeString.charAt(i);
                switch (c) {
                    case ',':
                        continue;
                    case '0':
                        return false;
                    case '1':
                        counts[1] = counts[1] + 1;
                        break;
                    case '2':
                        counts[2] = counts[2] + 1;
                        break;
                    case '3':
                        counts[3] = counts[3] + 1;
                        break;
                    case '4':
                        counts[4] = counts[4] + 1;
                        break;
                    case '5':
                        counts[5] = counts[5] + 1;
                        break;
                    case '6':
                        counts[6] = counts[6] + 1;
                        break;
                    case '7':
                        counts[7] = counts[7] + 1;
                        break;
                    case '8':
                        counts[8] = counts[8] + 1;
                        break;
                    case '9':
                        counts[9] = counts[9] + 1;
                        break;
                }
            }
        }
        for (int i = 1; i <= 9; i++) {
            int count = counts[i];
            if (count != i) {
                return false;
            }
        }
        return true;
    }
}
