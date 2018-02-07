package implementation;

public class Main {

    public static void main(String ... args) {
        long runtimeStart = System.currentTimeMillis();
        Application app = new Application();
        app.run();
        System.out.println("Main runtime (ms)   : " + (System.currentTimeMillis() - runtimeStart));
    }
}
