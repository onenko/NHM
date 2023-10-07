package net.nenko.nhmj;

public interface MeasuredExecutionStep {

    /**
     * execute() executes some benchmarked actions and returns the duration in ms
     */
    long execute();

    /**
     * execute() executes lambda and returns the duration in ms
     */
    static long execute(Runnable runnable) {
        long startMs = System.currentTimeMillis();
        runnable.run();
        return System.currentTimeMillis() - startMs;
    }

}
