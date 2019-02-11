/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import java.util.Map;

public interface ClassResolver {
    public Class<?> classForName(String var1, Map<String, Object> var2) throws ClassNotFoundException;
}

