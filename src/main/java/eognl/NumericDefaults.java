/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

class NumericDefaults {
    private final Map<Class<?>, Object> NUMERIC_DEFAULTS = new HashMap();

    NumericDefaults() {
        this.NUMERIC_DEFAULTS.put(Boolean.class, Boolean.FALSE);
        this.NUMERIC_DEFAULTS.put(Byte.class, (byte)0);
        this.NUMERIC_DEFAULTS.put(Short.class, (short)0);
        this.NUMERIC_DEFAULTS.put(Character.class, Character.valueOf('\u0000'));
        this.NUMERIC_DEFAULTS.put(Integer.class, 0);
        this.NUMERIC_DEFAULTS.put(Long.class, 0L);
        this.NUMERIC_DEFAULTS.put(Float.class, Float.valueOf(0.0f));
        this.NUMERIC_DEFAULTS.put(Double.class, 0.0);
        this.NUMERIC_DEFAULTS.put(BigInteger.class, BigInteger.ZERO);
        this.NUMERIC_DEFAULTS.put(BigDecimal.class, BigDecimal.ZERO);
    }

    Object get(Class<?> cls) {
        return this.NUMERIC_DEFAULTS.get(cls);
    }
}

