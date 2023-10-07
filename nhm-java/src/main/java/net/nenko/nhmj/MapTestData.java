package net.nenko.nhmj;

import java.util.UUID;

public class MapTestData {

    /**
     * getStringStringPairs returns 2D array with 2 columns of string data
     *
     * @param count number of pairs
     * @return 2d array: [0][i] - key of pair, [1][i] - value of pair, i = [0..count[
     */
    public static String[][] getStringStringPairs(int count) {
        String[][] data = new String[2][count];
        for(int i = 0; i < count; i++) {
            data[0][i] = UUID.randomUUID().toString();
            data[1][i] = "value-" + data[0][i] + "-value";
        }
        return data;
    }

}
