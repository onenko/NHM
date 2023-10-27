package net.nenko.nhmj;

import net.nenko.lib.benchmark.BenchmarkedExecution;
import net.nenko.util.NanoHashMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class App {
    private static String[][] testData;

    public static void main(String[] args) {

        System.out.println("HashMap/NanoHashMap Benchmark Application v1.0\n");

        Cfg cfg = new Cfg();
        String err = cfg.parse(args);
        if(err != null) {
            System.out.println("Error to parse command line: " + err);
            System.exit(-1);
        }

        testData = MapTestData.getStringStringPairs(cfg.benchmarkDataSize());

        Function<Void, Map<String, String>> testMapProducer = null;
        if(cfg.isNanoHashMap()) {
            testMapProducer = (_void) -> new NanoHashMap<String, String>(cfg.benchmarkMapCapacity());
            benchmarkRun(testMapProducer, "with net.nenko.util.NanoHashMap");
        } else {
            testMapProducer = (_void) -> new HashMap<String, String>(cfg.benchmarkMapCapacity());
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
