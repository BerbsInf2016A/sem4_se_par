package implementation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PrimeCategorizer {
    private final int[] primes;
    private final ConcurrentHashMap<String, int[]> cache;


    public static boolean filterForCharacterAndCount(long number, char value, int count) {
        String stringValue = String.valueOf(number);
        return stringValue.chars().filter(t -> t == value).count() == count;
    }

    public PrimeCategorizer(int[] primes) {
        this.cache = new ConcurrentHashMap<>();
        this.primes = primes;
    }

    public int[] getBucketForCharacterAndCharacterCount(int digit, int count){
        System.out.println("Bucket called: " + digit + " " + count);
        char charValue = Character.forDigit(digit, 10);
        String key = digit + "_" + count;
        if (this.cache.containsKey(key)){
            return this.cache.get(key);
        } else {
            this.cache.put(key, Arrays.stream(this.primes).filter(t -> filterForCharacterAndCount(t, charValue, count)).toArray());
        }
        return this.cache.get(key);
    }
}
