/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.PrimitiveDefaults;

public class ExPrimitiveDefaults
extends PrimitiveDefaults {
    public ExPrimitiveDefaults() {
        this.put(Integer.class, 0);
        this.put(Long.class, 0L);
        this.put(Float.class, Float.valueOf(0.0f));
        this.put(Double.class, 0.0);
        this.put(Boolean.TYPE, false);
        this.put(Byte.TYPE, (byte)0);
        this.put(Short.TYPE, (short)0);
        this.put(Integer.TYPE, 0);
        this.put(Long.TYPE, 0L);
        this.put(Float.TYPE, Float.valueOf(0.0f));
        this.put(Double.TYPE, 0.0);
    }
}

