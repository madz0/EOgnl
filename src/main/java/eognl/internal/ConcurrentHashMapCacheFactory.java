/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal;

import eognl.internal.Cache;
import eognl.internal.CacheFactory;
import eognl.internal.ClassCache;
import eognl.internal.ConcurrentHashMapCache;
import eognl.internal.ConcurrentHashMapClassCache;
import eognl.internal.entry.CacheEntryFactory;
import eognl.internal.entry.ClassCacheEntryFactory;

public class ConcurrentHashMapCacheFactory
implements CacheFactory {
    @Override
    public <K, V> Cache<K, V> createCache(CacheEntryFactory<K, V> entryFactory) {
        return new ConcurrentHashMapCache<K, V>(entryFactory);
    }

    @Override
    public <V> ClassCache<V> createClassCache() {
        return this.createClassCache(null);
    }

    @Override
    public <V> ClassCache<V> createClassCache(ClassCacheEntryFactory<V> entryFactory) {
        return new ConcurrentHashMapClassCache(entryFactory);
    }
}

