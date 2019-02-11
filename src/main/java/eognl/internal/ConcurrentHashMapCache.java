/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal;

import eognl.internal.Cache;
import eognl.internal.CacheException;
import eognl.internal.entry.CacheEntryFactory;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapCache<K, V>
implements Cache<K, V> {
    private ConcurrentHashMap<K, V> cache = new ConcurrentHashMap();
    private CacheEntryFactory<K, V> cacheEntryFactory;

    public ConcurrentHashMapCache() {
    }

    public ConcurrentHashMapCache(CacheEntryFactory<K, V> cacheEntryFactory) {
        this.cacheEntryFactory = cacheEntryFactory;
    }

    @Override
    public void clear() {
        this.cache.clear();
    }

    @Override
    public int getSize() {
        return this.cache.size();
    }

    @Override
    public V get(K key) throws CacheException {
        V v = this.cache.get(key);
        if (this.shouldCreate(this.cacheEntryFactory, v)) {
            return this.put(key, this.cacheEntryFactory.create(key));
        }
        return v;
    }

    protected boolean shouldCreate(CacheEntryFactory<K, V> cacheEntryFactory, V v) throws CacheException {
        return cacheEntryFactory != null && v == null;
    }

    @Override
    public V put(K key, V value) {
        V collision = this.cache.putIfAbsent(key, value);
        if (collision != null) {
            return collision;
        }
        return value;
    }

    public boolean contains(K key) {
        return this.cache.contains(key);
    }
}

