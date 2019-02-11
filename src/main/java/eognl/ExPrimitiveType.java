/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.PrimitiveTypes;

public class ExPrimitiveType
extends PrimitiveTypes {
    ExPrimitiveType() {
        this.put("java.lang.Boolean", Boolean.TYPE);
        this.put("java.lang.Byte", Byte.TYPE);
        this.put("java.lang.Short", Short.TYPE);
        this.put("java.lang.Character", Character.TYPE);
        this.put("java.lang.Integer", Integer.TYPE);
        this.put("java.lang.Long", Long.TYPE);
        this.put("java.lang.Float", Float.TYPE);
        this.put("java.lang.Double", Double.TYPE);
    }

    Class<?> get(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        return this.get(cls.getName());
    }
}

