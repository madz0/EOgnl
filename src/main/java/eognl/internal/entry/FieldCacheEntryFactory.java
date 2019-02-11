/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal.entry;

import eognl.internal.CacheException;
import eognl.internal.entry.ClassCacheEntryFactory;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class FieldCacheEntryFactory
implements ClassCacheEntryFactory<Map<String, Field>> {
    @Override
    public Map<String, Field> create(Class<?> key) throws CacheException {
        Field[] declaredFields = key.getDeclaredFields();
        HashMap<String, Field> result = new HashMap<String, Field>(declaredFields.length);
        for (Field field : declaredFields) {
            result.put(field.getName(), field);
        }
        return result;
    }
}

