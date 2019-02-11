/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal.entry;

import eognl.OgnlInvokePermission;
import eognl.internal.CacheException;
import eognl.internal.entry.CacheEntryFactory;
import eognl.internal.entry.PermissionCacheEntry;
import java.lang.reflect.Method;
import java.security.Permission;

public class PermissionCacheEntryFactory
implements CacheEntryFactory<PermissionCacheEntry, Permission> {
    @Override
    public Permission create(PermissionCacheEntry key) throws CacheException {
        return new OgnlInvokePermission("invoke." + key.method.getDeclaringClass().getName() + "." + key.method.getName());
    }
}

