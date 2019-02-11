/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

class PrimitiveDefaults {
    private final Map<Class<?>, Object> map = new HashMap(20);

    PrimitiveDefaults() {
        this.map.put(Boolean.TYPE, Boolean.FALSE);
        this.map.put(Boolean.class, Boolean.FALSE);
        this.map.put(Byte.TYPE, (byte)0);
        this.map.put(Byte.class, (byte)0);
        this.map.put(Short.TYPE, (short)0);
        this.map.put(Short.class, (short)0);
        this.map.put(Character.TYPE, Character.valueOf('\u0000'));
        this.map.put(Integer.TYPE, 0);
        this.map.put(Long.TYPE, 0L);
        this.map.put(Float.TYPE, Float.valueOf(0.0f));
        this.map.put(Double.TYPE, 0.0);
        this.map.put(BigInteger.class, BigInteger.ZERO);
        this.map.put(BigDecimal.class, BigDecimal.ZERO);
    }

    protected void put(Class<?> cls, Object value) {
        this.map.put(cls, value);
    }

    Object get(Class<?> cls) {
        return this.map.get(cls);
    }
}

