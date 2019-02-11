/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import java.util.HashMap;
import java.util.Map;

public class PrimitiveWrapperType {
    private final Map<String, Class<?>> map = new HashMap(101);

    PrimitiveWrapperType() {
        this.map.put("boolean", Boolean.class);
        this.map.put("byte", Byte.class);
        this.map.put("short", Short.class);
        this.map.put("char", Character.class);
        this.map.put("int", Integer.class);
        this.map.put("long", Long.class);
        this.map.put("float", Float.class);
        this.map.put("double", Double.class);
        this.put("java.lang.Boolean", Boolean.class);
        this.put("java.lang.Byte", Byte.class);
        this.put("java.lang.Short", Short.class);
        this.put("java.lang.Character", Character.class);
        this.put("java.lang.Integer", Integer.class);
        this.put("java.lang.Long", Long.class);
        this.put("java.lang.Float", Float.class);
        this.put("java.lang.Double", Double.class);
    }

    protected void put(String name, Class<?> t) {
        this.map.put(name, t);
    }

    Class<?> get(String className) {
        return this.map.get(className);
    }

    Class<?> get(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        return this.get(cls.getName());
    }
}

