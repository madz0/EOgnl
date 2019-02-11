/*
 * Decompiled with CFR 0.139.
 */
package eognl.enhance;

import eognl.Node;
import eognl.OgnlContext;

public interface ExpressionAccessor {
    public Object get(OgnlContext var1, Object var2);

    public void set(OgnlContext var1, Object var2, Object var3);

    public void setExpression(Node var1);
}

