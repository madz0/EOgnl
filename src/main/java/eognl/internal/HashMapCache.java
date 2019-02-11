/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal;

import eognl.internal.Cache;
import eognl.internal.CacheException;
import eognl.internal.entry.CacheEntryFactory;
import java.util.HashMap;
import java.util.Map;

public class HashMapCache<K, V>
implements Cache<K, V> {
    private final Map<K, V> cache = new HashMap(512);
    private CacheEntryFactory<K, V> cacheEntryFactory;

    public HashMapCache(CacheEntryFactory<K, V> cacheEntryFactory) {
        this.cacheEntryFactory = cacheEntryFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        Map<K, V> map = this.cache;
        synchronized (map) {
            this.cache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getSize() {
        Map<K, V> map = this.cache;
        synchronized (map) {
            return this.cache.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V get(K key) throws CacheException {
        V v = this.cache.get(key);
        if (this.shouldCreate(this.cacheEntryFactory, v)) {
            Map<K, V> map = this.cache;
            synchronized (map) {
                v = this.cache.get(key);
                if (v != null) {
                    return v;
                }
                return this.put(key, this.cacheEntryFactory.create(key));
            }
        }
        return v;
    }

    protected boolean shouldCreate(CacheEntryFactory<K, V> cacheEntryFactory, V v) throws CacheException {
        return cacheEntryFactory != null && v == null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V put(K key, V value) {
        Map<K, V> map = this.cache;
        synchronized (map) {
            this.cache.put(key, value);
            return value;
        }
    }

    public boolean contains(K key) {
        return this.cache.containsKey(key);
    }
}

