/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal;

import eognl.internal.Cache;
import eognl.internal.ClassCache;
import eognl.internal.entry.CacheEntryFactory;
import eognl.internal.entry.ClassCacheEntryFactory;

public interface CacheFactory {
    public <K, V> Cache<K, V> createCache(CacheEntryFactory<K, V> var1);

    public <V> ClassCache<V> createClassCache();

    public <V> ClassCache<V> createClassCache(ClassCacheEntryFactory<V> var1);
}

