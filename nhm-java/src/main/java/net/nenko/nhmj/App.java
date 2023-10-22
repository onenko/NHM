package net.nenko.nhmj;

import net.nenko.lib.benchmark.BenchmarkedExecution;
import net.nenko.util.NanoHashMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class App {
    private static final int TEST_DATA_SIZE = 33000000;
    private static final int MAP_DATA_SIZE = 45000000;
    private static String[][] testData;

    public static void main(String[] args) {

        System.out.println("HashMap/NanoHashMap Benchmark Application v1.0\n");

        Boolean isNanoHashMap = null;
        if(args.length == 1) {
            if("-hm".equals(args[0]))   isNanoHashMap = false;
            if("-nhm".equals(args[0]))  isNanoHashMap = true;
        }
        if(isNanoHashMap == null) {
            System.out.println("You should provide argument -hm for HashMap or -nhm for NanoHashMap to benchmark");
            System.exit(-1);
        }

        testData = MapTestData.getStringStringPairs(TEST_DATA_SIZE);

        Function<Void, Map<String, String>> testMapProducer = null;
        if(isNanoHashMap) {
            testMapProducer = (_void) -> new NanoHashMap<String, String>(MAP_DATA_SIZE);
            benchmarkRun(testMapProducer, "with net.nenko.util.NanoHashMap");
        } else {
            testMapProducer = (_void) -> new HashMap<String, String>(MAP_DATA_SIZE);
            benchmarkRun(testMapProducer, "with java.util.HashMap");
        }
    }

    private static void benchmarkRun(Function<Void, Map<String, String>> testMapProducer, String title) {
        System.out.println("*** BEGIN stepRun " + title);

        MapTest mapTest = new MapTest(testMapProducer, testData);

        BenchmarkedExecution.BenchmarkResult benchmarkResult = mapTest.benchmarkCreateTheMap();
        System.out.println("Step 'Create the TestMap' done in " + benchmarkResult);

        benchmarkResult = mapTest.benchmarkFillTheMap();
        System.out.println("Step 'Fill the TestMap' done in " + benchmarkResult);

        benchmarkResult = mapTest.benchmarkLookInTheMap();
        System.out.println("Step 'Lookup in the Map' done in " + benchmarkResult);

        System.out.println("*** END OF stepRun " + title);
    }

}
