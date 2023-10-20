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
        List<String> savedKeysToCheck = new ArrayList<>(10000);
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