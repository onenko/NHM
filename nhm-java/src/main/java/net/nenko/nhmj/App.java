package net.nenko.nhmj;

import net.nenko.lib.benchmark.BenchmarkedExecution;
import net.nenko.util.NanoHashMap;

import java.util.HashMap;
import java.util.Map;

public class App {

    private static String[][] testData = MapTestData.getStringStringPairs(10000000);

    public static void main(String[] args) {
        stepRun(new HashMap<>(), "with java.util.HashMap");
        stepRun(new NanoHashMap<>(), "with nenko.net.lib.util.NanoHashMap");
        stepRun(new HashMap<>(), "with java.util.HashMap");
        stepRun(new NanoHashMap<>(), "with nenko.net.lib.util.NanoHashMap");
        stepRun(new HashMap<>(), "with java.util.HashMap");
        stepRun(new NanoHashMap<>(), "with nenko.net.lib.util.NanoHashMap");
    }

    private static void stepRun(Map<String, String> testMap, String title) {
        System.out.println("*** BEGIN stepRun " + title);

        MapTest mapTest = new MapTest(testMap, testData);

        BenchmarkedExecution.BenchmarkResult benchmarkResult = mapTest.benchmarkFillTheMap();
        System.out.println("Step 'Fill the TestMap' done in " + benchmarkResult);

        benchmarkResult = mapTest.benchmarkLookInTheMap();
        System.out.println("Step 'Lookup in the Map' done in " + benchmarkResult);

        System.out.println("*** END OF stepRun " + title);
    }

}
