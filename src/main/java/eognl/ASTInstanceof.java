/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTConst;
import eognl.EOgnlRuntime;
import eognl.Node;
import eognl.NodeType;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.OgnlParser;
import eognl.SimpleNode;

public class ASTInstanceof
extends SimpleNode
implements NodeType {
    private String targetType;

    public ASTInstanceof(int id) {
        super(id);
    }

    public ASTInstanceof(OgnlParser p, int id) {
        super(p, id);
    }

    void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    String getTargetType() {
        return this.targetType;
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object value = this.children[0].getValue(context, source);
        return EOgnlRuntime.isInstance(context, value, this.targetType) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Class<?> getGetterClass() {
        return Boolean.TYPE;
    }

    @Override
    public Class<?> getSetterClass() {
        return null;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        try {
            String ret = "";
            ret = ASTConst.class.isInstance(this.children[0]) ? ((Boolean)this.getValueBody(context, target)).toString() : String.valueOf(this.children[0].toGetSourceString(context, target)) + " instanceof " + this.targetType;
            context.setCurrentType(Boolean.TYPE);
            return ret;
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        return this.toGetSourceString(context, target);
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

