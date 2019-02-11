/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.OgnlContext;
import java.lang.reflect.Member;

public interface TypeConverter {
    public <T> T convertValue(OgnlContext var1, Object var2, Member var3, String var4, Object var5, Class<T> var6);
}

