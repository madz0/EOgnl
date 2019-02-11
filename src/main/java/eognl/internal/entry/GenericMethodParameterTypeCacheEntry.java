/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal.entry;

import eognl.internal.entry.CacheEntry;
import java.lang.reflect.Method;

public class GenericMethodParameterTypeCacheEntry
implements CacheEntry {
    Method method;
    Class<?> type;

    public GenericMethodParameterTypeCacheEntry(Method method, Class<?> type) {
        this.method = method;
        this.type = type;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GenericMethodParameterTypeCacheEntry)) {
            return false;
        }
        GenericMethodParameterTypeCacheEntry that = (GenericMethodParameterTypeCacheEntry)o;
        return this.method.equals(that.method) && this.type.equals(that.type);
    }

    public int hashCode() {
        int result = this.method.hashCode();
        result = 31 * result + this.type.hashCode();
        return result;
    }
}

