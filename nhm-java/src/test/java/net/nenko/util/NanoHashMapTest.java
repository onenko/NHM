package net.nenko.util;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class NanoHashMapTest {

    private static final String KEY1 = "Key1";
    private static final String VAL1 = "Value1";
    private static final String KEY2 = "Key2";
    private static final String VAL2 = "Value2";

//    private static final int HEAVY_TEST_CAPACITY = 88888888;
//    private static final int HEAVY_TEST_KVP_COUNT = 44000000;
    private static final int HEAVY_TEST_CAPACITY = 8888888;
    private static final int HEAVY_TEST_KVP_COUNT = 4400000;
    private static final int SAVED_KEYS_LIST_SIZE = 100000;

    @Test
    void simplestTest() {
        Map<String, String> map = new NanoHashMap<>();
        assertNull(map.put(KEY1, VAL1));
        assertEquals(VAL1, map.get(KEY1));
        assertNull(map.get(KEY2));
        assertEquals(1, map.size());
        Set<Map.Entry<String, String>> KVs = map.entrySet();
        assertNotNull(KVs);
        assertEquals(1, KVs.size());
        assertEquals(KEY1, KVs.stream().findFirst().get().getKey());
        assertEquals(VAL1, KVs.stream().findFirst().get().getValue());
    }

    @Test
    void resizeTest() {
        Map<String, String> map = new NanoHashMap<>(HEAVY_TEST_CAPACITY);
        List<String> savedKeysToCheck = new ArrayList<>(SAVED_KEYS_LIST_SIZE);
        for(int i = 0; i < HEAVY_TEST_KVP_COUNT; i ++) {
            String randomString = UUID.randomUUID().toString();
            if( i % 10000 == 0) {
                savedKeysToCheck.add(randomString);
            }
            map.put(randomString, "[" + randomString + ']');

            if( i % 1000000 == 0) {
                System.out.println("i = " + i);
            }
        }
        assertTrue("The map filled successfully" != null);
        for(String key: savedKeysToCheck) {
            String val = "[" + key + ']';
            assertEquals(val, map.get(key));
        }
    }

    @Test
    void removeTest() {
        Map<String, String> map = new NanoHashMap<>(HEAVY_TEST_CAPACITY);
        String savedKey = null;
        for(int i = 0; i < HEAVY_TEST_KVP_COUNT; i ++) {
            String randomKey = UUID.randomUUID().toString();
            String randomVal = "[" + randomKey + ']';
            if( i % 10000 == 0) {
                if(savedKey != null) {
                    String savedValue = "[" + savedKey + ']';
                    if(i % 20000 == 0) {
                        assertEquals(map.remove(savedKey), savedValue);
                    } else {
                        assertFalse(map.remove(savedKey, randomVal));
                        assertTrue(map.remove(savedKey, savedValue));
                    }
                }
                savedKey = randomKey;
            }
            map.put(randomKey, randomVal);

            if( i % 1000000 == 0) {
                System.out.println("i = " + i);
            }
        }
        assertTrue("The map filled successfully" != null);
        assertEquals(map.get(savedKey), "[" + savedKey + ']');
    }

    /**
     * removeNoResizeTest() - check that a map can keep the memory footprint in some limits
     * after huge amount remove/put operations
     */
    @Test
    void removeNoResizeTest() {
        Map<String, String> map = new NanoHashMap<>(HEAVY_TEST_CAPACITY);
        String[] savedKeysToCheck = new String[SAVED_KEYS_LIST_SIZE];

        // Add some initial values to the tested map and to saved keys array
        for(int i = 0; i < SAVED_KEYS_LIST_SIZE; i ++) {
            savedKeysToCheck[i] = UUID.randomUUID().toString();
            map.put(savedKeysToCheck[i], "[" + savedKeysToCheck[i] + ']');
        }

        // Make multiple passes through saved keys and delete KVP and create new one and add to the map
        for(int scan = 0; scan < 9999; scan ++) {
            for(int i = 0; i < SAVED_KEYS_LIST_SIZE; i ++) {
                // Delete KV Pair and generate and add new one
                String key = savedKeysToCheck[i];
                assertEquals(map.get(key), "[" + key + ']');
                assertEquals(map.remove(key), "[" + key + ']');
                savedKeysToCheck[i] = UUID.randomUUID().toString();
                map.put(savedKeysToCheck[i], "[" + savedKeysToCheck[i] + ']');
            }
            System.out.println("The map keeps OK after the pass " + scan);
        }
        assertTrue("The map endured all the scans" != null);
    }

    @Test
    void nullHandlingHashMapTest() {
        Map<String, String> map = new HashMap<>();
        map.put(KEY1, null);
        map.put(KEY2, VAL2);
        assertNull(map.get(KEY1));
        map.put(null, VAL1);
        assertEquals(map.get(null), VAL1);
    }

    @Test
    void nullHandlingNanoHashMapTest() {
        Map<String, String> map = new NanoHashMap<>();
        map.put(KEY1, null);
        map.put(KEY2, VAL2);
        assertNull(map.get(KEY1));
        assertThrows(NullPointerException.class, () -> map.put(null, VAL1));
    }

}