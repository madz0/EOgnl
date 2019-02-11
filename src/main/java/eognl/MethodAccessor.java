/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.OgnlException;
import java.util.Map;

public interface MethodAccessor {
    public Object callStaticMethod(Map<String, Object> var1, Class<?> var2, String var3, Object[] var4) throws OgnlException;

    public Object callMethod(Map<String, Object> var1, Object var2, String var3, Object[] var4) throws OgnlException;
}

