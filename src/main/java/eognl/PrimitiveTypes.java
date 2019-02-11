/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import java.util.HashMap;
import java.util.Map;

class PrimitiveTypes {
    private final Map<String, Class<?>> map = new HashMap(101);

    PrimitiveTypes() {
        this.map.put("boolean", Boolean.TYPE);
        this.map.put("byte", Byte.TYPE);
        this.map.put("short", Short.TYPE);
        this.map.put("char", Character.TYPE);
        this.map.put("int", Integer.TYPE);
        this.map.put("long", Long.TYPE);
        this.map.put("float", Float.TYPE);
        this.map.put("double", Double.TYPE);
    }

    protected void put(String name, Class<?> t) {
        this.map.put(name, t);
    }

    Class<?> get(String className) {
        return this.map.get(className);
    }
}

