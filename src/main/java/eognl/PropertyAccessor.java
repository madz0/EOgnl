/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.OgnlContext;
import eognl.OgnlException;

public interface PropertyAccessor {
    public Object getProperty(OgnlContext var1, Object var2, Object var3) throws OgnlException;

    public void setProperty(OgnlContext var1, Object var2, Object var3, Object var4) throws OgnlException;

    public String getSourceAccessor(OgnlContext var1, Object var2, Object var3);

    public String getSourceSetter(OgnlContext var1, Object var2, Object var3);
}

