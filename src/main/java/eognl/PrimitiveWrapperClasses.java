/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import java.util.IdentityHashMap;
import java.util.Map;

class PrimitiveWrapperClasses {
    private Map<Class<?>, Class<?>> map = new IdentityHashMap();

    PrimitiveWrapperClasses() {
        this.map.put(Boolean.TYPE, Boolean.class);
        this.map.put(Boolean.class, Boolean.TYPE);
        this.map.put(Byte.TYPE, Byte.class);
        this.map.put(Byte.class, Byte.TYPE);
        this.map.put(Character.TYPE, Character.class);
        this.map.put(Character.class, Character.TYPE);
        this.map.put(Short.TYPE, Short.class);
        this.map.put(Short.class, Short.TYPE);
        this.map.put(Integer.TYPE, Integer.class);
        this.map.put(Integer.class, Integer.TYPE);
        this.map.put(Long.TYPE, Long.class);
        this.map.put(Long.class, Long.TYPE);
        this.map.put(Float.TYPE, Float.class);
        this.map.put(Float.class, Float.TYPE);
        this.map.put(Double.TYPE, Double.class);
        this.map.put(Double.class, Double.TYPE);
    }

    Class<?> get(Class<?> cls) {
        return this.map.get(cls);
    }
}

