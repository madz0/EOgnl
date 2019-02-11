/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal.entry;

import eognl.internal.CacheException;

public interface CacheEntryFactory<K, V> {
    public V create(K var1) throws CacheException;
}

