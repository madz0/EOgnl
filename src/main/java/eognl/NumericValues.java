/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

class NumericValues {
    private final Map<Class<?>, String> map = new HashMap();

    NumericValues() {
        this.map.put(Double.class, "doubleValue()");
        this.map.put(Float.class, "floatValue()");
        this.map.put(Integer.class, "intValue()");
        this.map.put(Long.class, "longValue()");
        this.map.put(Short.class, "shortValue()");
        this.map.put(Byte.class, "byteValue()");
        this.map.put(BigDecimal.class, "doubleValue()");
        this.map.put(BigInteger.class, "doubleValue()");
        this.map.put(Boolean.class, "booleanValue()");
    }

    String get(Class<?> cls) {
        return this.map.get(cls);
    }
}

