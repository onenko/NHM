package net.nenko.util;

import java.util.*;

/**
 * NanoHashMap - simplest implementation of hash map with minimum memory (heap) requirements
 *
 * NOTE1: Definitely this implementation can't compete in speed with classic java.util.HashMap,
 *      but having less memory requirements in some circumstances it can be a good choice
 */
public class NanoHashMap<K, V> extends AbstractMap<K,V> implements Map<K,V> {

    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
    static final int MAXIMUM_CAPACITY = 1 << 30;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final float MIN_LOAD_FACTOR = 0.1f;
    static final float MAX_LOAD_FACTOR = 0.9f;
    private static final String ABOUT_LOAD_FACTOR = ", must be [0.1 .. 0.9]";
    private static final String NO_FREE_SLOTS_MSG = "No free slots left in the map, this should not happen";

    private int size;           // number of KV pairs in the map
    private int deletedSlots;   // used/inactive slots count
    private int threshold;      // when (size+deletedSlots) > threshold, we must resize
    private float loadFactor;

    // Data structures to store keys and values, 2 arrays
    // A pair (keys[i], values[i]) forms a slot with index 'i'
    // We use 2 arrays of references instead of one array of pairs of references
    //  to minimize the amount of used heap objects
    // NOTE2: Some slots should be marked as deleted, see slotIsDeleted()
    // One more rule: let's the value of deleted slot will be null (to avoid memory leak).

    private K[] keys;
    private V[] values;

    /**
     * Constructs an empty HashMap with the specified initial capacity and load factor.
     * @param initialCapacity – the initial capacity
     * @param loadFactor – the load factor
     * @throw IllegalArgumentException – if the initial capacity is negative or the load factor is nonpositive
     */
    public NanoHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }
        if (loadFactor < MIN_LOAD_FACTOR || loadFactor > MAX_LOAD_FACTOR) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor + ABOUT_LOAD_FACTOR);
        }
        this.loadFactor = loadFactor;
        createInternalDataStructures(initialCapacity);
    }

    /**
     * Constructs an empty {@code HashMap} with the specified initial
     * capacity and the default load factor (0.75).
     *
     * @param  initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is negative.
     */
    public NanoHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public NanoHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    @Override
    public V put(K key, V value) {
        if(size + deletedSlots > threshold) {
            resize();
        }
        int index = slotIndex(key);
        for(int i = 0; i < keys.length; i++) {
            int slot = (index + i) % keys.length;
            if(slotIsDeleted(slot)) {
                continue;
            }
            if(keys[slot] == null) {    // add KV Pair
                keys[slot] = key;
                values[slot] = value;
                size++;
                return null;
            }
            if(key.equals(keys[slot])) {    // update KV Pair
                V old = values[slot];
                values[slot] = value;
                return old;
            }
        }
        throw new IllegalStateException(NO_FREE_SLOTS_MSG);
    }

    @Override
    public V get(Object key) {
        int slot = locateSlot(key);
        return slot == -1 ? null : values[slot];
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> set = new HashSet<>(keys.length);
        for(int i = 0; i < keys.length; i++) {
            if (keys[i] != null && ! slotIsDeleted(i)) {
                set.add(new AbstractMap.SimpleEntry<>(keys[i], values[i]));
            }
        }
        return set;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean containsKey(Object key) {
        return locateSlot(key) != -1;
    }

    @Override
    public V remove(Object key) {
        int slot = locateSlot(key);
        V oldValue = null;
        if(slot >= 0) {
            oldValue = values[slot];
            deleteSlot(slot);
        }
        return oldValue;
    }

    @Override
    public boolean remove(Object key, Object value) {
        int slot = locateSlot(key);
        if(slot >= 0 && Objects.equals(values[slot], value)) {
            deleteSlot(slot);
            return true;
        }
        return false;
    }

    private static final Object REFERENCE_OF_DELETED_SLOT = "REFERENCE_OF_DELETED_SLOT";

    private void deleteSlot(int slot) {
        keys[slot] = (K) REFERENCE_OF_DELETED_SLOT;
        values[slot] = null;
        size--;
        deletedSlots++;
    }

    /**
     * slotIsDeleted() - checks if the slot contains deleted map entry and can't be reused
     *
     * NOTE: to mark the slot as deleted, we use tricky approach: assign the key to
     * predefined reference REFERENCE_OF_DELETED_SLOT
     */
    private boolean slotIsDeleted(int i) {
        return keys[i] == REFERENCE_OF_DELETED_SLOT;
    }

    private int slotIndex(Object key) {
        if( key == null) {
            throw new NullPointerException("NanoHashMap implementation of Map does not support 'null' key");
        }
        return (key.hashCode() & 0x7FFFFFFF) % keys.length;
    }

    /**
     * locateSlot() - searches data array for the key 'key'
     * @return index of a slot with key 'key' in data array or -1 if the key not found
     */
    private int locateSlot(Object key) {
        int index = slotIndex(key);
        for(int i = 0; i < keys.length; i++) {
            int slot = (index + i) % keys.length;
            if (slotIsDeleted(slot)) {
                continue;
            }
            if (keys[slot] == null) {    // not found
                return -1;
            }
            if (keys[slot].equals(key)) {    // found
                return slot;
            }
        }
        throw new IllegalStateException(NO_FREE_SLOTS_MSG);
    }

    private void resize() {
//        System.out.println("resize() starting with: " + sizesToString());
        int capacity = calculateNewSizes();
        K[] oldKeys = keys;
        V[] oldValues = values;
        createInternalDataStructures(capacity);

        for(int i = 0; i < oldKeys.length; i++) {
            if (oldKeys[i] != null && oldKeys[i] != REFERENCE_OF_DELETED_SLOT) {
                put(oldKeys[i], oldValues[i]);
            }
        }

//        System.out.println("resize() done with: " + sizesToString());
    }

    private void createInternalDataStructures(int capacity) {
        size = 0;
        deletedSlots = 0;
        keys = (K[]) new Object[capacity];
        values = (V[]) new Object[capacity];
        threshold = (int) (capacity * loadFactor);
    }

    private int calculateNewSizes() {
        long current = keys.length;
        if(current < Integer.MAX_VALUE) {
            current = current * 2;
            current = current < Integer.MAX_VALUE ? current : Integer.MAX_VALUE;
        } else {
            if(deletedSlots > 0) {
                ; // we still can gain some space by copying into new tables
            } else {
               if( loadFactor < MAX_LOAD_FACTOR) {
                   loadFactor = MAX_LOAD_FACTOR;    // last hope
               } else {
                   throw new IllegalStateException("NanoHashMap reached its maximum capacity: " +
                           size + " key-value pairs at loadFactor = " + loadFactor);
               }
            }
        }

        // At this point 'current' is new size of internal data arrays, recalculate threshold:
        threshold = (int) (current * loadFactor);
        return (int) current;
    }

    private String sizesToString() {
        return "capacity: " + keys.length + ", KVP count: " + size +
                ", delCount: " + deletedSlots + ", threshold: " + threshold +
                ", loadFactor: " + loadFactor;
    }

}
