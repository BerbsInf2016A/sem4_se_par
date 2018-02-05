package implementation;

public enum Configuration {
    instance;
    int maximumNumberOfThreads = Runtime.getRuntime().availableProcessors();
    int maxNumberOfPrimesPerSet = 25;
}
