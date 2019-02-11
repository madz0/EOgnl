/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal.entry;

import eognl.internal.CacheException;
import eognl.internal.entry.CacheEntryFactory;
import eognl.internal.entry.MethodAccessEntryValue;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MethodAccessCacheEntryFactory
implements CacheEntryFactory<Method, MethodAccessEntryValue> {
    public static final MethodAccessEntryValue INACCESSIBLE_NON_PUBLIC_METHOD = new MethodAccessEntryValue(false, true);
    public static final MethodAccessEntryValue ACCESSIBLE_NON_PUBLIC_METHOD = new MethodAccessEntryValue(true, true);
    public static final MethodAccessEntryValue PUBLIC_METHOD = new MethodAccessEntryValue(true);

    @Override
    public MethodAccessEntryValue create(Method method) throws CacheException {
        boolean notPublic;
        boolean bl = notPublic = !Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers());
        if (notPublic) {
            if (!method.isAccessible()) {
                return INACCESSIBLE_NON_PUBLIC_METHOD;
            }
            return ACCESSIBLE_NON_PUBLIC_METHOD;
        }
        return PUBLIC_METHOD;
    }
}

