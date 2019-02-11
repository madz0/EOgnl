/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

class NumericLiterals {
    private final Map<Class<? extends Number>, String> map = new HashMap<Class<? extends Number>, String>();

    NumericLiterals() {
        this.map.put(Integer.class, "");
        this.map.put(Integer.TYPE, "");
        this.map.put(Long.class, "l");
        this.map.put(Long.TYPE, "l");
        this.map.put(BigInteger.class, "d");
        this.map.put(Float.class, "f");
        this.map.put(Float.TYPE, "f");
        this.map.put(Double.class, "d");
        this.map.put(Double.TYPE, "d");
        this.map.put(BigInteger.class, "d");
        this.map.put(BigDecimal.class, "d");
    }

    String get(Class<? extends Number> clazz) {
        return this.map.get(clazz);
    }
}

