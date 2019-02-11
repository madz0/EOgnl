/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.JavaSource;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.enhance.ExpressionAccessor;

public interface Node
extends JavaSource {
    public void jjtOpen();

    public void jjtClose();

    public void jjtSetParent(Node var1);

    public Node jjtGetParent();

    public void jjtAddChild(Node var1, int var2);

    public Node jjtGetChild(int var1);

    public int jjtGetNumChildren();

    public Object getValue(OgnlContext var1, Object var2) throws OgnlException;

    public void setValue(OgnlContext var1, Object var2, Object var3) throws OgnlException;

    public ExpressionAccessor getAccessor();

    public void setAccessor(ExpressionAccessor var1);

    public <R, P> R accept(NodeVisitor<? extends R, ? super P> var1, P var2) throws OgnlException;
}

