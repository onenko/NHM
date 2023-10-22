package net.nenko.lib.benchmark;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;

public class BenchmarkedExecution {

    public static BenchmarkResult benchmarkedRun(Runnable runnable) {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        BenchmarkResult result = new BenchmarkResult();
        result.durationNanos = - threadMXBean.getCurrentThreadCpuTime();
        result.heapIncreaseBytes = - memoryMXBean.getHeapMemoryUsage().getUsed();

        runnable.run();

        result.durationNanos += threadMXBean.getCurrentThreadCpuTime();
        result.heapIncreaseBytes += memoryMXBean.getHeapMemoryUsage().getUsed();
        return result;
    }

    public static class BenchmarkResult {
        public long durationNanos;
        public long heapIncreaseBytes;

        @Override
        public String toString() {
            return "duration " + (long)(0.001 * durationNanos + 0.5) + " microseconds," +
                    " heap increase " + heapIncreaseBytes + " bytes.";
        }
    }

}
