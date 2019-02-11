/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal.entry;

import eognl.internal.entry.DeclaredMethodCacheEntry;
import eognl.internal.entry.MethodCacheEntry;
import eognl.internal.entry.MethodCacheEntryFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class DeclaredMethodCacheEntryFactory
extends MethodCacheEntryFactory<DeclaredMethodCacheEntry> {
    @Override
    protected boolean shouldCache(DeclaredMethodCacheEntry key, Method method) {
        if (key.type == null) {
            return true;
        }
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        if (key.type == DeclaredMethodCacheEntry.MethodType.STATIC) {
            return isStatic;
        }
        return !isStatic;
    }
}

