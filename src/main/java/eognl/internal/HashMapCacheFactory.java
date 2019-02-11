/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal;

import eognl.internal.Cache;
import eognl.internal.CacheFactory;
import eognl.internal.ClassCache;
import eognl.internal.HashMapCache;
import eognl.internal.HashMapClassCache;
import eognl.internal.entry.CacheEntryFactory;
import eognl.internal.entry.ClassCacheEntryFactory;

public class HashMapCacheFactory
implements CacheFactory {
    @Override
    public <K, V> Cache<K, V> createCache(CacheEntryFactory<K, V> entryFactory) {
        return new HashMapCache<K, V>(entryFactory);
    }

    @Override
    public <V> ClassCache<V> createClassCache() {
        return this.createClassCache(null);
    }

    @Override
    public <V> ClassCache<V> createClassCache(ClassCacheEntryFactory<V> entryFactory) {
        return new HashMapClassCache(entryFactory);
    }
}

