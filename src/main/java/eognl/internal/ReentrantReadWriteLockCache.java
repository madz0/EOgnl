/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal;

import eognl.internal.Cache;
import eognl.internal.CacheException;
import eognl.internal.entry.CacheEntryFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantReadWriteLockCache<K, V>
implements Cache<K, V> {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = this.lock.readLock();
    private final Lock writeLock = this.lock.writeLock();
    final Map<K, V> cache = new HashMap();
    private CacheEntryFactory<K, V> cacheEntryFactory;

    public ReentrantReadWriteLockCache() {
    }

    public ReentrantReadWriteLockCache(CacheEntryFactory<K, V> cacheEntryFactory) {
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

    @Override
    public V get(K key) throws CacheException {
        V v;
        boolean shouldCreate;
        this.readLock.lock();
        try {
            v = this.cache.get(key);
            shouldCreate = this.shouldCreate(this.cacheEntryFactory, v);
        }
        finally {
            this.readLock.unlock();
        }
        if (shouldCreate) {
            try {
                this.writeLock.lock();
                v = this.cache.get(key);
                if (!this.shouldCreate(this.cacheEntryFactory, v)) {
                    V v2 = v;
                    return v2;
                }
                v = this.cacheEntryFactory.create(key);
                this.cache.put(key, v);
                V v3 = v;
                return v3;
            }
            finally {
                this.writeLock.unlock();
            }
        }
        return v;
    }

    protected boolean shouldCreate(CacheEntryFactory<K, V> cacheEntryFactory, V v) throws CacheException {
        return cacheEntryFactory != null && v == null;
    }

    @Override
    public V put(K key, V value) {
        this.writeLock.lock();
        try {
            this.cache.put(key, value);
            V v = value;
            return v;
        }
        finally {
            this.writeLock.unlock();
        }
    }
}

