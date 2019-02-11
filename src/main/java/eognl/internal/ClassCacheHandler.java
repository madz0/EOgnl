/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal;

import eognl.internal.CacheException;
import eognl.internal.ClassCache;

public class ClassCacheHandler {
    private ClassCacheHandler() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> T getHandler(Class forClass, ClassCache<T> handlers) throws CacheException {
        T answer;
        ClassCache<T> classCache = handlers;
        synchronized (classCache) {
            answer = handlers.get(forClass);
            if (answer == null) {
                Class<?> keyFound;
                if (forClass.isArray()) {
                    answer = handlers.get(Object[].class);
                    keyFound = null;
                } else {
                    keyFound = forClass;
                    block3 : for (Class<?> clazz = forClass; clazz != null; clazz = clazz.getSuperclass()) {
                        answer = handlers.get(clazz);
                        if (answer == null) {
                            for (Class<?> iface : clazz.getInterfaces()) {
                                answer = handlers.get(iface);
                                if (answer == null) {
                                    answer = ClassCacheHandler.getHandler(iface, handlers);
                                }
                                if (answer == null) continue;
                                keyFound = iface;
                                break block3;
                            }
                            continue;
                        }
                        keyFound = clazz;
                        break;
                    }
                }
                if (answer != null && keyFound != forClass) {
                    handlers.put(forClass, answer);
                }
            }
        }
        return (T)answer;
    }
}

