package net.nenko.nhmj;

import net.nenko.lib.benchmark.BenchmarkedExecution;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MapTest /*implements MeasuredExecutionStep*/ {

    private String[][] testData;
    private Map<String, String> testMap;

    public MapTest(Map<String, String> testMap, String[][] testData) {
        this.testMap = testMap;
        this.testData = testData;
    }

    public BenchmarkedExecution.BenchmarkResult benchmarkFillTheMap() {
//        return MeasuredExecutionStep.execute(() -> _fillTheMap());
        return BenchmarkedExecution.benchmarkedRun(() -> _fillTheMap());
    }

    public BenchmarkedExecution.BenchmarkResult benchmarkLookInTheMap() {
        return BenchmarkedExecution.benchmarkedRun(() -> _lookInTheMap());
    }

    private void _lookInTheMap() {
        for (int i = 0; i < testData[0].length; i++) {
            String value = testMap.get(testData[0][i]);
            if(value == null) {
                throw new IllegalStateException("Unexpected failure: get(testData[0][" + i + "]) == null");
            };
            if( ! value.equals(testData[1][i])) {
                throw new IllegalStateException("Unexpected value returned by get(testData[0][" + i + "])");
            };
        }
    }

    private void _fillTheMap() {
        for (int i = 0; i < testData[0].length; i++) {
            testMap.put(testData[0][i], testData[1][i]);
        }
    }

}
