package implementation;

public enum Configuration {
    instance;
    public long maxTimeOutInSeconds = 900;
    public boolean printFoundSetsDuringRuntime = false;
    public int printFoundSetsCounterInterval = 100;
    public int startDelay = 10000;
    int maximumNumberOfThreads = Runtime.getRuntime().availableProcessors();
    int maxNumberOfPrimesPerSet = 25;
}
