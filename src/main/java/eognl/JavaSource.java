/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.OgnlContext;

public interface JavaSource {
    public String toGetSourceString(OgnlContext var1, Object var2);

    public String toSetSourceString(OgnlContext var1, Object var2);
}

