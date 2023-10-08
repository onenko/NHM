package net.nenko.util;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NanoHashMapTest {

    private static final String KEY1 = "Key1";
    private static final String VAL1 = "Value1";
    private static final String KEY2 = "Key2";
    private static final String VAL2 = "Value2";

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
}