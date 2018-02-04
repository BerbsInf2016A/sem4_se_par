package implementation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PrimeCategorizer {
    private final List<Long> primes;
    private final ConcurrentHashMap<String, List<Long>> cache;


    public static boolean filterForCharacterAndCount(long number, char value, int count) {
        String stringValue = String.valueOf(number);
        return stringValue.chars().filter(t -> t == value).count() == count;
    }

    public PrimeCategorizer(List<Long> primes) {
        this.cache = new ConcurrentHashMap<>();
        this.primes = primes;
    }

    public List<Long> getBucketForCharacterAndCharacterCount(int digit, int count){
        char charValue = Character.forDigit(digit, 10);
        String key = digit + "_" + count;
        if (this.cache.containsKey(key)){
            return this.cache.get(key);
        } else {
            this.cache.put(key, this.primes.stream().filter(t -> filterForCharacterAndCount(t, charValue, count)).collect(Collectors.toList()));
        }
        return this.cache.get(key);
    }
}
