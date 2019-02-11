/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal;

import eognl.internal.CacheException;

public interface Cache<K, V> {
    public void clear();

    public int getSize();

    public V get(K var1) throws CacheException;

    public V put(K var1, V var2);
}

