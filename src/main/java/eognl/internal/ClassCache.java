/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal;

import eognl.ClassCacheInspector;
import eognl.internal.Cache;

public interface ClassCache<V>
extends Cache<Class<?>, V> {
    public void setClassInspector(ClassCacheInspector var1);
}

