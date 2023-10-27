package net.nenko.nhmj;

import org.apache.commons.cli.*;

/**
 * Cfg - responsible for the configuration of this application
 */
public class Cfg {
    private static final int BENCHMARK_DEFAULT_DATA_SIZE = 33000000;
    private static final int BENCHMARKED_MAP_DEFAULT_CAP = 45000000;

    private Options options = new Options();
    private int bds = BENCHMARK_DEFAULT_DATA_SIZE;
    private int bmc = BENCHMARKED_MAP_DEFAULT_CAP;
    private boolean isNanoHashMap = false;

    public Cfg() {
        options.addOption("nhm",
                "Benchmark HashMap (option is absent) or NanoHashMap (option is present) implementation");
        options.addOption("bds", true,
                "Benchmark Data size (Count of KVP to place into map), default " + BENCHMARK_DEFAULT_DATA_SIZE);
        options.addOption("bmc", true,
                "Benchmark Map Capacity (initial), default " + BENCHMARKED_MAP_DEFAULT_CAP);
    }

    /**
     * parse() parses command line arguments - validate and extract configuration
     *
     * @param args typical arguments of main()
     * @return null in case the arguments are OK, or Message that explains the problems
     */
    public String parse(String[] args) {
        try {
            CommandLine line = new DefaultParser().parse(options, args);
            isNanoHashMap = line.hasOption("nhm");

            String bdsValue = line.getOptionValue("bds");
            if(bdsValue != null) {
                bds = Integer.decode(bdsValue);
            }
            if (bds < 10) {
                return "Benchmark Data size option must be positive number >= 10";
            }

            String bmcValue = line.getOptionValue("bmc");
            if(bmcValue != null) {
                bmc = Integer.decode(bmcValue);
            }
            if (bmc < 10) {
                return "Benchmark Map Capacity (initial) option must be positive number >= 10";
            }
        } catch (ParseException exp) {
            return "Parsing failed.  Reason: " + exp.getMessage();
        }
        return null;
    }

    public boolean isNanoHashMap() {
        return isNanoHashMap;
    }

    public int benchmarkDataSize() {
        return bds;
    }

    public int benchmarkMapCapacity() {
        return bmc;
    }

}
