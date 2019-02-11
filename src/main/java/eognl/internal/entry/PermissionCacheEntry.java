/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal.entry;

import eognl.internal.entry.CacheEntry;
import java.lang.reflect.Method;

public class PermissionCacheEntry
implements CacheEntry {
    public Method method;

    public PermissionCacheEntry(Method method) {
        this.method = method;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermissionCacheEntry)) {
            return false;
        }
        PermissionCacheEntry that = (PermissionCacheEntry)o;
        return !(this.method != null ? !this.method.equals(that.method) : that.method != null);
    }

    public int hashCode() {
        return this.method != null ? this.method.hashCode() : 0;
    }
}

