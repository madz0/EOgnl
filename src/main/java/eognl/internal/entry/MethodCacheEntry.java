/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal.entry;

import eognl.internal.entry.CacheEntry;

public class MethodCacheEntry
implements CacheEntry {
    public Class<?> targetClass;

    public MethodCacheEntry(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MethodCacheEntry)) {
            return false;
        }
        MethodCacheEntry that = (MethodCacheEntry)o;
        return this.targetClass.equals(that.targetClass);
    }

    public int hashCode() {
        return this.targetClass.hashCode();
    }
}

