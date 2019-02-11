/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

class NumericCasts {
    private final Map<Class<? extends Number>, String> map = new HashMap<Class<? extends Number>, String>();

    NumericCasts() {
        this.map.put(Double.class, "(double)");
        this.map.put(Float.class, "(float)");
        this.map.put(Integer.class, "(int)");
        this.map.put(Long.class, "(long)");
        this.map.put(BigDecimal.class, "(double)");
        this.map.put(BigInteger.class, "");
    }

    String get(Class<? extends Number> cls) {
        return this.map.get(cls);
    }
}

