/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal.entry;

import eognl.internal.entry.MethodCacheEntry;

public class DeclaredMethodCacheEntry
extends MethodCacheEntry {
    MethodType type;

    public DeclaredMethodCacheEntry(Class<?> targetClass) {
        super(targetClass);
    }

    public DeclaredMethodCacheEntry(Class<?> targetClass, MethodType type) {
        super(targetClass);
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeclaredMethodCacheEntry)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        DeclaredMethodCacheEntry that = (DeclaredMethodCacheEntry)o;
        return this.type == that.type;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.type != null ? this.type.hashCode() : 0);
        return result;
    }

    public static enum MethodType {
        STATIC,
        NON_STATIC;
        
    }

}

