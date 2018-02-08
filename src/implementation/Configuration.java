package implementation;

public enum Configuration {
    instance;
    /**
     * The maximum timeout setting for ThreadExecutor.
     */
    public long maxTimeOutInSeconds = 900;
    /**
     * If set to true, every found set will be printed out on the console.
     */
    public boolean printFoundSetsDuringRuntime = false;
    /**
     * An interval to control the counter message.
     * E.g. If set to 100, a message containing the counter value will be printed
     * after every hundredth found set.
     */
    public int printFoundSetsCounterInterval = 100;
    /**
     * Set a delay, before the search is started. Can be used to give visual vm
     * a little time to connect.
     */
    public int startDelay = 10000;
    /**
     * The number of available processors.
     */
    int maximumNumberOfThreads = Runtime.getRuntime().availableProcessors();
    /**
     * This value is used in the ValidatingPrimeSet as size for the array, which
     * holds the primes in the set. This is configurable for searches with bigger
     * sets of primes.
     */
    int maxNumberOfPrimesPerSet = 25;
}
