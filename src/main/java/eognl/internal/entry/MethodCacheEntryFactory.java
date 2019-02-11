/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal.entry;

import eognl.EOgnlRuntime;
import eognl.internal.CacheException;
import eognl.internal.entry.CacheEntryFactory;
import eognl.internal.entry.MethodCacheEntry;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MethodCacheEntryFactory<T extends MethodCacheEntry>
implements CacheEntryFactory<T, Map<String, List<Method>>> {
    @Override
    public Map<String, List<Method>> create(T key) throws CacheException {
        HashMap<String, List<Method>> result = new HashMap<String, List<Method>>(23);
        for (Class<?> c = key.targetClass; c != null; c = c.getSuperclass()) {
            for (Method method : c.getDeclaredMethods()) {
                if (!EOgnlRuntime.isMethodCallable(method) || !this.shouldCache(key, method)) continue;
                ArrayList<Method> ml = (ArrayList<Method>)result.get(method.getName());
                if (ml == null) {
                    ml = new ArrayList<Method>();
                    result.put(method.getName(), ml);
                }
                ml.add(method);
            }
        }
        return result;
    }

    protected abstract boolean shouldCache(T var1, Method var2);
}

